import sbt._
import Keys._

object Common {
  val settings = Seq(
    organization := "is.informatik.uni-wuerzburg.de",
    version := "0.9.0",
    scalaVersion := "2.12.3",
    libraryDependencies ++= Seq(
      "org.mockito" % "mockito-core" % "2.11.0",
      "org.scalatest" %% "scalatest" % "3.0.4" % "test"
    ),
    scalacOptions ++= Seq("-feature"))

  // Scalatest

  val mysqlDependency = "mysql" % "mysql-connector-java" % "8.0.8-dmr"

  val seleniumDep = "org.seleniumhq.selenium" % "selenium-java" % "3.5.2"
  val htmlUnitDep = "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0"

  val mockitoDep = "org.mockito" % "mockito-core" % "2.8.47"

  // Json Schema
  val jsonSchemaValidator = "com.github.fge" % "json-schema-validator" % "2.2.6"
  val jsonSchemaGenerator = "com.kjetland" % "mbknor-jackson-jsonschema_2.12" % "1.0.24"

  // Programming
  val dockerjava = "com.github.docker-java" % "docker-java" % "3.0.13"

  // SQL
  // Mybatis for ScriptRunner
  val mybatis = "org.mybatis" % "mybatis" % "3.4.5"

  // JSQL-Parser
  val jsqlparser = "com.github.jsqlparser" % "jsqlparser" % "1.1"

}