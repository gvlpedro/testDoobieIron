import sbt.Keys.sourceManaged

enablePlugins(ScalaUnidocPlugin)
// Usinng unidoc: https://github.com/delta-io/delta/blob/master/build.sbt

def genVersion(module: String): Setting[_] =
  sourceGenerators in Compile += Def.task {
    val file: File =new File( s"versions/$module.version")
    IO.write(file, content = version.value)
    Seq(file)
  }.taskValue

lazy val doobiePrj = project
  .in(file("doobie-prj"))
  .settings(
    version := "2.0.0",
    name := "doobie-prj",
    scalaVersion := "3.3.1",
    runMain := "com.example.TryDoobieWithIron",
    genVersion("doobie-prj"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-unchecked"
    ),
    libraryDependencies ++= Seq(
      "io.github.iltotore" %% "iron" % "2.3.0",
      "org.xerial" % "sqlite-jdbc" % "3.23.1",
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC4", // HikariCP transactor.
      "org.tpolecat" %% "doobie-specs2" % "1.0.0-RC4", // Specs2 support for typechecking statements.
      "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC4", // ScalaTest support for typechecking statements.
      "io.github.iltotore" %% "iron-doobie" % "2.3.0-7-1a121d-SNAPSHOT"
    ),
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )

lazy val prj2 = project
  .in(file("prj2"))
  .settings(
    version := "2.0.0",
    name := "prj2",
    scalaVersion := "3.3.1",
    genVersion("prj2"),
    libraryDependencies ++= Seq()
  )

lazy val prj3 = project
  .in(file("prj3"))
  .settings(
    version := "2.0.0",
    name := "prj3",
    scalaVersion := "3.3.1",
    genVersion("prj3"),
    libraryDependencies ++= Seq()
  )

lazy val autoTag = taskKey[Unit]("Auto git tagging")
autoTag := GitUtil.autoTag()
