name := """it4all"""

Common.settings

lazy val root = (project in file("."))
	.enablePlugins(PlayJava, PlayEbean)
	.aggregate(binary, mindmap, spread, sql, web, xml)
	.dependsOn(binary, mindmap, spread, sql, web, xml, core)

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
	.enablePlugins(PlayJava)
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

  Common.mockitoDep

)

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

// JaCoCo - siehe project/plugins.sbt
jacoco.settings
