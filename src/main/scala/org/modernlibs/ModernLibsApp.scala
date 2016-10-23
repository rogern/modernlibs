package org.modernlibs


import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.{Server, ServerApp}

import scalaz.concurrent.Task


object ModernLibsApp extends ServerApp {

  private val logger = org.log4s.getLogger

  val db = new DB
  val service = new ModernLibsService(db).modernLibsService
  val port: Int = 9200

  override def server(args: List[String]): Task[Server] = {
    val serverTask = BlazeBuilder
      .bindHttp(port, "localhost")
      .mountService(service, "/")
      .start

    logger.info(s"Server started on port $port")
    serverTask
  }
}
