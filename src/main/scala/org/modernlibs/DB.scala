package org.modernlibs

import doobie.imports._

import scalaz.concurrent.Task

case class Person(name: String, age: Int)

class DB {
  val xa = DriverManagerTransactor[Task](
    "org.mariadb.jdbc.Driver", "jdbc:mariadb://localhost:3306/test", "root", ""
  )

  def list(): Seq[Person] = queryList().transact(xa).unsafePerformSync

  def find(n: String): Option[Person] = queryFind(n).transact(xa).unsafePerformSync

  def save(person: Person) = insertNewPerson(person).run.transact(xa).unsafePerformSync

  private def queryList(): ConnectionIO[List[Person]] =
    sql"select name, age from modernlibs".query[Person].list

  private def insertNewPerson(person: Person): Update0 =
    sql"insert into modernlibs (name, age) values (${person.name}, ${person.age})".update

  private def queryFind(n: String): ConnectionIO[Option[Person]] =
    sql"select name, age from modernlibs where name = $n".query[Person].option

}
