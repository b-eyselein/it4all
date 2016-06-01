import sbt._
import Keys._

object Common {
  val settings: Seq[Setting[_]] = Seq(
    organization := "example.com",
    version := "0.1.0",
    scalaVersion := "2.11.7")

  val mysqlDependency = "mysql" % "mysql-connector-java" % "5.1.38"

  val seleniumDep = "org.seleniumhq.selenium" % "selenium-java" % "2.53.0"
  val htmlUnitDep = "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0"

  val mockitoDep = "org.mockito" % "mockito-core" % "1.10.19"
}