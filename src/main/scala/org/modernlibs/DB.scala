package org.modernlibs

import doobie.imports._

import scalaz.\/
import scalaz.concurrent.Task

case class Person(name: String, age: Int)

class DB {
  val xa = DriverManagerTransactor[Task](
    "org.mariadb.jdbc.Driver", "jdbc:mariadb://localhost:3306/test", "root", ""
  )

  def list(): Task[\/[Throwable, List[Person]]] = queryList().transact(xa).attempt

  def find(n: String): Task[\/[Throwable, Option[Person]]] = queryFind(n).transact(xa).attempt

  def save(person: Person): Task[\/[Throwable, Int]] = insertNewPerson(person).run.transact(xa).attempt

  private def queryList(): ConnectionIO[List[Person]] =
    sql"select name, age from modernlibs".query[Person].list

  private def insertNewPerson(person: Person): Update0 =
    sql"insert into modernlibs (name, age) values (${person.name}, ${person.age})".update

  private def queryFind(n: String): ConnectionIO[Option[Person]] =
    sql"select name, age from modernlibs where name = $n".query[Person].option

}
