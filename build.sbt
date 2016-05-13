name := """it4all"""

Common.settings

lazy val root = (project in file("."))
	.enablePlugins(PlayJava, PlayEbean)
	.aggregate(binary, mindmap, sql, web, xml)
	.dependsOn(binary, mindmap, sql, web, xml, core)

lazy val core: Project = (project in file("modules/core"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.settings(aggregateReverseRoutes := Seq(binary, mindmap, sql, web, xml, root))

lazy val web = (project in file("modules/web"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val sql = (project in file("modules/sql"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val xml = (project in file("modules/xml"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val binary = (project in file("modules/binary"))
	.enablePlugins(PlayJava)
	.dependsOn(core)

lazy val mindmap = (project in file("modules/mindmap"))
	.enablePlugins(PlayJava)
	.dependsOn(core)

// Used libraries from Maven Repository
libraryDependencies ++= Seq(
  javaJdbc,
  cache,

  Common.mysqlDependency,

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

  // Mockito for Testing
  "org.mockito" % "mockito-core" % "1.10.19"

)

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

// JaCoCo - siehe project/plugins.sbt
jacoco.settings
