name := """JAV_Masterpraktikum"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  
  "mysql" % "mysql-connector-java" % "5.1.27",
  
  // Apache POI for Excel
  "org.apache.poi" % "poi" % "3.13",
  
  // Selenium for Html Tests
  "org.seleniumhq.selenium" % "selenium-java" % "2.48.1"
)

EclipseKeys.withSource := true

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
