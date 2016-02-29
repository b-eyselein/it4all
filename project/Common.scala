import sbt._
import Keys._

object Common {
  val settings: Seq[Setting[_]] = Seq(
    organization := "example.com",
    version := "0.1.0",
    scalaVersion := "2.11.6")

  val fooDependency = "com.foo" %% "foo" % "2.4"
}