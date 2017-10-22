name := "ModernLibs"

version := "1.0"

scalaVersion := "2.12.3"

lazy val http4sVersion = "0.17.5"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core-cats" % "0.4.4",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.5.4",
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % "0.6.1",
  "org.slf4j" % "slf4j-simple" % "1.7.21"
)
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

dependencyOverrides := Set(
  "org.scala-lang" %% "scala-library" % "2.12.3",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5"
)