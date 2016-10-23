name := "ModernLibs"

version := "1.0"

scalaVersion := "2.11.8"

lazy val http4sVersion = "0.14.10a"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % "0.3.0",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.5.4",
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % "0.4.1"
  ,"org.slf4j" % "slf4j-simple" % "1.7.21"
)