import sbt._
import Keys._

object Common {
  val settings: Seq[Setting[_]] = Seq(
    organization := "is.informatik.uni-wuerzburg.de",
    version := "0.9.0",
    scalaVersion := "2.12.2",

    libraryDependencies ++= Seq(
      // Mockito for Testing
      Common.mockitoDep
    )
  )

  val mysqlDependency = "mysql" % "mysql-connector-java" % "6.0.6"

  val seleniumDep = "org.seleniumhq.selenium" % "selenium-java" % "3.4.0"
  val htmlUnitDep = "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0"

  val mockitoDep = "org.mockito" % "mockito-core" % "2.8.47"

  val jsonSchemaValidator = "com.github.fge" % "json-schema-validator" % "2.2.6"

  // Programming
  val dockerjava = "com.github.docker-java" % "docker-java" % "3.0.10"

  // SQL
  // Mybatis for ScriptRunner
  val mybatis = "org.mybatis" % "mybatis" % "3.4.4"

  // JSQL-Parser
  val jsqlparser = "com.github.jsqlparser" % "jsqlparser" % "1.0"

}