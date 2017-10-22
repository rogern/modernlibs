package org.modernlibs

import fs2.{Stream, Task}
import org.http4s.server.blaze._
import org.http4s.util.StreamApp

object ModernLibsApp extends StreamApp {

  private val logger = org.log4s.getLogger

  val db = new DB
  val service = new ModernLibsService(db).modernLibsService
  val port: Int = 9200

  override def stream(args: List[String]): Stream[Task, Nothing] = {
    val serverTask = BlazeBuilder
      .bindHttp(port, "localhost")
      .mountService(service, "/")
      .serve

    logger.info(s"Server started on port $port")
    serverTask
  }
}
