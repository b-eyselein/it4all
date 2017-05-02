name := """it4all"""

Common.settings

lazy val root = (project in file("."))
	.enablePlugins(PlayJava, PlayEbean)
	.aggregate(binary, bool, choice, js, mindmap, spread, sql, web, xml, python, uml)
	.dependsOn(binary, bool, choice, js, mindmap, spread, sql, web, xml, python, uml, core)

lazy val core: Project = (project in file("modules/core"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.settings(aggregateReverseRoutes := Seq(binary, bool, choice, js, mindmap, spread, sql, web, xml, python, uml, root))

lazy val web = (project in file("modules/web"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val sql = (project in file("modules/sql"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val spread = (project in file("modules/spread"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val js = (project in file("modules/js"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val xml = (project in file("modules/xml"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val binary = (project in file("modules/binary"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val choice = (project in file("modules/choice"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val bool = (project in file("modules/bool"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val mindmap = (project in file("modules/mindmap"))
	.enablePlugins(PlayJava)
	.dependsOn(core)

lazy val python = (project in file("modules/python"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val uml = (project in file("modules/uml"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

// Used libraries from Maven Repository
libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  Common.mysqlDependency
)

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

// JaCoCo - siehe project/plugins.sbt
jacoco.settings
