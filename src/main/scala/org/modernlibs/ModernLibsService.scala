package org.modernlibs

import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.{jsonEncoder, jsonOf}
import org.http4s.dsl._
import org.http4s.{HttpService, Request}

class ModernLibsService(db: DB) {

  val modernLibsService = HttpService {
    case GET -> Root / "ml" / name => getPerson(name)
    case GET -> Root / "ml" => getPersons
    case r@POST -> Root / "ml" => newPerson(r)
  }

  private def getPersons = {
    for {
      dbResult <- db.list()
      result <- dbResult.map(r => Ok(r.asJson)) | InternalServerError()
    } yield result
  }

  private def getPerson(name: String) = {
    for {
      dbResult <- db.find(name)
      result <- dbResult.map(_.fold(NotFound())(r => Ok(r.asJson))) | InternalServerError()
    } yield result
  }

  private def newPerson(r: Request) = {
    for {
      person <- r.as(jsonOf[Person])
      dbResponse <- db.save(person)
      response <- dbResponse.map {
        case 1 => Created()
        case _ => Conflict()
      } | InternalServerError()
    } yield response
  }
}
