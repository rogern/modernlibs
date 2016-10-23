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

  private def getPersons = Ok(db.list().asJson)

  private def getPerson(name: String) = db.find(name).fold(NotFound())(r => Ok(r.asJson))

  private def newPerson(r: Request) = {
    r.as(jsonOf[Person]).flatMap(p => Created(db.save(p).asJson))
  }
}
