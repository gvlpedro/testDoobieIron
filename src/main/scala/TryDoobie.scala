import cats.*
import cats.effect.*
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.util.ExecutionContexts
import cats.effect.unsafe.implicits.global

import scala.util.Properties

private object TryDoobie extends App {
  private val xa = Transactor.fromDriverManager[IO]("org.sqlite.JDBC", "jdbc:sqlite:sample.db","","",None)

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

  private def insert1(name: String, age: Option[Int]): Update0 =
    sql"insert into person (name, age) values ($name, $age)".update

  insert1("Alice", Some(12)).run.transact(xa).unsafeRunSync()
  insert1("Bob", None).quick.unsafeRunSync() // switch to YOLO mode

  case class Person(id: Long, name: String, age: Option[Int])

  val l = sql"select rowid, name, age from person"
    .query[Person]
    .to[List]
    .transact(xa)
    .unsafeRunSync()
  l.foreach(println)
}
