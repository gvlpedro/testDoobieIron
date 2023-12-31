scalaVersion := "3.3.1"

enablePlugins(ScalaUnidocPlugin)

libraryDependencies ++= Seq(
  "io.github.iltotore" %% "iron" % "2.3.0",
  "org.xerial" % "sqlite-jdbc" % "3.23.1",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC4",
  "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC4", // HikariCP transactor.
  "org.tpolecat" %% "doobie-specs2" % "1.0.0-RC4", // Specs2 support for typechecking statements.
  "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC4", // ScalaTest support for typechecking statements.
  "io.github.iltotore" %% "iron-doobie" % "2.3.0-7-1a121d-SNAPSHOT"
)
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked"
)

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

// Usinng unidoc: https://github.com/delta-io/delta/blob/master/build.sbt

releaseTagName := s"version-${if (releaseUseGlobalVersion.value) (version in ThisBuild).value else version.value}"