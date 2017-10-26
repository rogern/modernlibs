package org.modernlibs

import cats.data.NonEmptyList
import doobie.imports._
import fs2.Task
import fs2.interop.cats._
import fs2.util.Attempt

case class Person(name: String, age: Int)

class DB {

  import fs2.util.{Catchable, Suspendable}

  implicit object fs2TaskCatchableSuspendable extends Catchable[Task] with Suspendable[Task] {
    def pure[A](a: A): Task[A] = Task.now(a)
    def flatMap[A, B](a: Task[A])(f: A => Task[B]): Task[B] = a.flatMap(f)
    def fail[A](err: Throwable): Task[A] = Task.fail(err)
    def attempt[A](fa: Task[A]): Task[Either[Throwable,A]] = fa.attempt
    def suspend[A](fa: => Task[A]): Task[A] = Task.suspend(fa)
  }

  val xa = DriverManagerTransactor[Task](
    "org.mariadb.jdbc.Driver", "jdbc:mariadb://localhost:3306/test", "root", ""
  )

  def list(): Task[Attempt[Seq[Person]]] = queryList().transact(xa).attempt

  def findAllIn(names: NonEmptyList[String]): Task[Attempt[Seq[Person]]] = queryFindAll(names).transact(xa).attempt

  def find(n: String): Task[Attempt[Option[Person]]] = queryFind(n).transact(xa).attempt

  def save(person: Person): Task[Attempt[Int]] = insertNewPerson(person).run.transact(xa).attempt

  private def queryList(): ConnectionIO[List[Person]] =
    sql"select name, age from modernlibs".query[Person].list

  private def insertNewPerson(person: Person): Update0 =
    sql"insert into modernlibs (name, age) values (${person.name}, ${person.age})".update

  private def queryFind(n: String): ConnectionIO[Option[Person]] =
    sql"select name, age from modernlibs where name = $n".query[Person].option

  private def queryFindAll(names: NonEmptyList[String]): ConnectionIO[List[Person]] = {
    (fr"select name, age from modernlibs where " ++ Fragments.in(fr"name", names)).query[Person].list
  }

}
