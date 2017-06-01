import sbt._
import Keys._

object Common {
  val settings: Seq[Setting[_]] = Seq(
    organization := "is.informatik.uni-wuerzburg.de",
    version := "0.9.0",
    scalaVersion := "2.11.8")

  val mysqlDependency = "mysql" % "mysql-connector-java" % "6.0.6"

  val seleniumDep = "org.seleniumhq.selenium" % "selenium-java" % "3.4.0"
  val htmlUnitDep = "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0"

  val mockitoDep = "org.mockito" % "mockito-core" % "2.8.9"
  
  var jsonSchemaValidator = "com.github.fge" % "json-schema-validator" % "2.2.6"
}