name := """it4all"""

Common.settings

lazy val root = (project in file("."))
	.enablePlugins(PlayJava, PlayEbean)
	.aggregate(binary, mindmap, spread, sql, web, xml, uml)
	.dependsOn(binary, mindmap, spread, sql, web, xml, uml, core)

lazy val core: Project = (project in file("modules/core"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.settings(aggregateReverseRoutes := Seq(binary, mindmap, spread, sql, web, xml, root))

lazy val web = (project in file("modules/web"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val sql = (project in file("modules/sql"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val spread = (project in file("modules/spread"))
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
	
lazy val uml = (project in file("modules/uml"))
	.enablePlugins(PlayJava)
	.dependsOn(core)

// Used libraries from Maven Repository
libraryDependencies ++= Seq(
  javaJdbc,
  cache,

  Common.mysqlDependency,

  Common.mockitoDep,
  
  // Test Jython
  "org.python" % "jython-standalone" % "2.7.0"

)

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

// JaCoCo - siehe project/plugins.sbt
jacoco.settings
