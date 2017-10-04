name := """it4all"""

Common.settings

lazy val root = (project in file("."))
	.enablePlugins(PlayJava, PlayScala, PlayEbean, PlayEnhancer)
	.aggregate(bool, ebnf, mindmap, programming, question, spread, sql, uml, web, xml)
	.dependsOn(bool, ebnf, mindmap, programming, question, spread, sql, uml, web, xml, core)

lazy val core: Project = (project in file("modules/core"))
	.enablePlugins(PlayScala, PlayJava, PlayEbean, PlayEnhancer)
	.settings(
		aggregateReverseRoutes := Seq(bool, ebnf, mindmap, programming, question, spread, sql, uml, web, xml, root)
	)

lazy val bool = (project in file("modules/bool"))
	.enablePlugins(PlayScala, PlayJava)
	.dependsOn(core)

lazy val ebnf = (project in file("modules/ebnf"))
	.enablePlugins(PlayScala, PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val mindmap = (project in file("modules/mindmap"))
	.enablePlugins( PlayScala, PlayJava,PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val programming = (project in file("modules/programming"))
	.enablePlugins(PlayScala, PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val question = (project in file("modules/question"))
	.enablePlugins(PlayScala, PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val spread = (project in file("modules/spread"))
	.enablePlugins(PlayScala, PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val sql = (project in file("modules/sql"))
	.enablePlugins(PlayScala, PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val uml = (project in file("modules/uml"))
	.enablePlugins(PlayScala, PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val web = (project in file("modules/web"))
	.enablePlugins(PlayScala, PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

lazy val xml = (project in file("modules/xml"))
	.enablePlugins(PlayScala, PlayJava, PlayEbean, PlayEnhancer)
	.dependsOn(core)

// Used libraries from Maven Repository
libraryDependencies ++= Seq(
  javaJdbc,
  Common.mysqlDependency,
  guice
)

scalacOptions ++= Seq("-feature")

EclipseKeys.classpathTransformerFactories := Seq(ClasspathentryTransformer)

EclipseKeys.skipParents in ThisBuild := false
EclipseKeys.withSource := true
EclipseKeys.withJavadoc := true


// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

// JaCoCo - siehe project/plugins.sbt
jacoco.settings
