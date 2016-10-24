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
    case r @ POST -> Root / "ml" => newPerson(r)
  }

  private def getPersons = db.list().flatMap(_.map(r => Ok(r.asJson)) | InternalServerError())

  private def getPerson(name: String) = db.find(name).flatMap(_.map(_.fold(NotFound())(r => Ok(r.asJson))) | InternalServerError())

  private def newPerson(r: Request) = {
    r.as(jsonOf[Person]).flatMap(p => db.save(p).flatMap(_.map {
      case 1 => Created()
      case _ => Conflict()
    } | InternalServerError()))
  }
}
