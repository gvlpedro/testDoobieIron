import cats.*
import cats.effect.*
import cats.effect.unsafe.implicits.global
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor.Aux

import scala.util.Properties

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.doobie.given

opaque type Age = Int :| Positive
object Age extends RefinedTypeOps[Int, Positive, Age]

object TryDoobieWithIron extends App {
  private val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO]("org.sqlite.JDBC", "jdbc:sqlite:sample.db","","",None)

  private val y = xa.yolo
  import y.*

  private val drop =
    sql"""
    DROP TABLE IF EXISTS person
  """.update.run

  private val create =
    sql"""
    CREATE TABLE person (
      name TEXT NOT NULL UNIQUE,
      age  INTEGER
    )
  """.update.run

  private val res = (drop, create).mapN(_ + _).transact(xa).unsafeRunSync()
  println(res)

  private def insert1(name: String, age: Option[Age]): Update0 =
    sql"insert into person (name, age) values ($name, $age)".update

  insert1("Alice", Some(Age(12))).run.transact(xa).unsafeRunSync()
  insert1("Bob", None).quick.unsafeRunSync() // switch to YOLO mode
  sql"insert into person (name, age) values ('Pepe', 99)".update.quick.unsafeRunSync()
  // sql"insert into person (name, age) values ('V', -1)".update.quick.unsafeRunSync() // ERROR: because: Should be strictly positive

  case class Person(id: Long, name: String, age: Option[Age])

  val l = sql"select rowid, name, age from person"
    .query[Person]
    .to[List]
    .transact(xa)
    .unsafeRunSync()
  l.foreach(println)
}
