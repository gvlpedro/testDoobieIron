import sbt.*

import scala.sys.process.*
import scala.util.*

object GitUtil {
  def autoTag(): Unit = {
    println("Autotagging to v1.0.0")
    // git log --oneline --format=%s $(git describe --tags --abbrev=0)..HEAD
  }

  def styledWith(in: Any, color: String = scala.Console.CYAN): String =
    color + in + scala.Console.RESET

  def styled(in: Any): String = styledWith(in)

  def prompt(projectName: String): String = {
    val prj = projectPrompt(projectName)
    val gitDetail = gitPrompt(prj)
    s"$prj $gitDetail"
  }

  private def projectPrompt(projectName: String): String =
    s"[${styledWith(projectName, scala.Console.MAGENTA)}]"

  def projectName(state: State): String =
    Project
      .extract(state)
      .currentRef
      .project

  private def gitPrompt(projectName: String): String = (
    for {
      b <- branch.map(styled)
      t <- lastTag(projectName).map(styledWith(_, scala.Console.YELLOW))
    } yield s"$b:$t"
  ).get

  private def branch: Option[String] =
    run("git rev-parse --abbrev-ref HEAD")

  private def lastTag(projectName: String): Option[String] = Some {
    val major: Option[String] = for {
      v <- run(s"git describe --tags --abbrev=0 | grep '$projectName'")
      m <- Try { v.split("\\.")(0) }.toOption
    } yield m
    major.getOrElse("<no-tags>")
  }

  private def run(command: String): Option[String] =
    Try(
      command
        .split(" ")
        .toSeq
        .!!(noopProcessLogger)
        .trim
    ).toOption

  private val noopProcessLogger: ProcessLogger = ProcessLogger(_ => (), _ => ())
}
