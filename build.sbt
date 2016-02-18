name := """it4all"""

version := "0.1"

lazy val commonSettings = Seq(
  organization := "example.com",
  version := "0.1.0",
  scalaVersion := "2.11.6"
)

lazy val root = (project in file("."))
	.enablePlugins(PlayJava, PlayEbean)
	.aggregate(web, mindmap)
	.dependsOn(web, mindmap)
	.settings(commonSettings: _*)
	
lazy val web = (project in file("modules/web"))
	.enablePlugins(PlayJava)
	.settings(commonSettings: _*)

lazy val mindmap = (project in file("modules/mindmap"))
	.enablePlugins(PlayJava)
	.settings(commonSettings: _*)

// Used libraries from Maven Repository
libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  
  "mysql" % "mysql-connector-java" % "5.1.27",
  
  // Apache POI for Excel
  "org.apache.poi" % "poi" % "3.13",
  "org.apache.poi" % "poi-excelant" % "3.13",
  "org.apache.poi" % "poi-ooxml" % "3.13",
  "org.apache.poi" % "poi-ooxml-schemas" % "3.13",
  "org.apache.poi" % "poi-scratchpad" % "3.13",
  "org.apache.xmlbeans" % "xmlbeans" % "2.6.0",
  
  
  // ODF Toolkit for OpenOffice Calc
  "commons-validator" % "commons-validator" % "1.4.0",
  "net.rootdev" % "java-rdfa" % "0.4.2",
  "org.apache.jena" % "jena-arq" % "2.9.4",
  "org.apache.jena" % "jena-core" % "2.7.4",
  "org.apache.jena" % "jena-iri" % "3.0.0",
  "org.apache.odftoolkit" % "odfdom-java" % "0.8.10-incubating",
  "org.apache.odftoolkit" % "simple-odf" % "0.8.1-incubating",
  "xerces" % "xercesImpl" % "2.9.1",
  "xml-apis" % "xml-apis" % "1.3.04",
  
  
  // Selenium for Html Tests
  "org.seleniumhq.selenium" % "selenium-java" % "2.48.1",
  
  // Mockito for Testing
  "org.mockito" % "mockito-core" % "1.9.5"
  
)

EclipseKeys.withSource := true

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

// JaCoCo - siehe project/plugins.sbt
jacoco.settings