package org.modernlibs

import org.http4s._
import org.http4s.dsl._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.{jsonEncoder, jsonOf}
import cats.implicits._
import fs2.Task

class ModernLibsService(db: DB) {

  object NamesQueryParamMatcher extends QueryParamDecoderMatcher[String]("name")

  val expand = (l: String) => l.split(',').map(_.trim).toList.filter(_.nonEmpty)

  val modernLibsService = HttpService {
    case GET -> Root / "ml" :? NamesQueryParamMatcher(names) => getPersonsIn(expand(names))
    case GET -> Root / "ml" / name => getPerson(name)
    case GET -> Root / "ml" => listPersons
    case r@POST -> Root / "ml" => newPerson(r)
  }

  private def listPersons = {
    for {
      dbResult <- db.list()
      result <- dbResult.map(r => Ok(r.asJson)) getOrElse InternalServerError("server error, probably DB down")
    } yield result
  }

  private def getPersonsIn(names: List[String]) = {

    names.toNel.fold(BadRequest()) { nel =>
      for {
        dbResult <- db.findAllIn(nel)
        result <- dbResult.map(r => Ok(r.asJson)) getOrElse InternalServerError("server error, probably DB down")
      } yield result
    }
  }

  private def getPerson(name: String) = {
    for {
      dbResult <- db.find(name)
      result <- dbResult.map(_.fold(NotFound())(r => Ok(r.asJson))) getOrElse InternalServerError("server error, probably DB down")
    } yield result
  }

  private def newPerson(r: Request): Task[Response] = {
    for {
      person <- r.as(jsonOf[Person])
      dbResponse <- db.save(person)
      response <- dbResponse.map {
        case 1 => Created()
        case _ => Conflict()
      } getOrElse InternalServerError("server error, probably DB down")
    } yield response
  }
}
