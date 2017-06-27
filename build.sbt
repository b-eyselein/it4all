name := """it4all"""

Common.settings

lazy val root = (project in file("."))
	.enablePlugins(PlayJava, PlayEbean)
	.aggregate(binary, bool, mindmap, programming, question, spread, sql, uml, web, xml)
	.dependsOn(binary, bool, mindmap, programming, question, spread, sql, uml, web, xml, core)

lazy val core: Project = (project in file("modules/core"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.settings(aggregateReverseRoutes := Seq(binary, bool, mindmap, programming, question, spread, sql, uml, web, xml, root))

lazy val binary = (project in file("modules/binary"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val bool = (project in file("modules/bool"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val mindmap = (project in file("modules/mindmap"))
	.enablePlugins(PlayJava)
	.dependsOn(core)

lazy val programming = (project in file("modules/programming"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val question = (project in file("modules/question"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val spread = (project in file("modules/spread"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val sql = (project in file("modules/sql"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val uml = (project in file("modules/uml"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val web = (project in file("modules/web"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val xml = (project in file("modules/xml"))
	.enablePlugins(PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

// Used libraries from Maven Repository
libraryDependencies ++= Seq(
  javaJdbc,
  Common.mysqlDependency,
  guice
)

EclipseKeys.classpathTransformerFactories := Seq(ClasspathentryTransformer)

EclipseKeys.skipParents in ThisBuild := false

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

// JaCoCo - siehe project/plugins.sbt
jacoco.settings
