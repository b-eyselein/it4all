name := """it4all"""

Common.settings

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayEbean, PlayEnhancer)
  .aggregate(bool, ebnf, mindmap, programming, question, spread, sql, uml, web, xml)
  .dependsOn(bool, ebnf, mindmap, programming, question, spread, sql, uml, web, xml, core)

lazy val core: Project = (project in file("modules/core"))
  .enablePlugins(PlayScala, PlayEbean, PlayEnhancer)
  .settings(
    aggregateReverseRoutes := Seq(bool, ebnf, mindmap, programming, question, spread, sql, uml, web, xml, root)
  )

lazy val bool = (project in file("modules/bool"))
  .enablePlugins(PlayScala, PlayEbean, PlayEnhancer)
  .dependsOn(core)

lazy val ebnf = (project in file("modules/ebnf"))
  .enablePlugins(PlayScala, PlayEbean, PlayEnhancer)
  .dependsOn(core)

lazy val mindmap = (project in file("modules/mindmap"))
  .enablePlugins(PlayScala, PlayEbean, PlayEnhancer)
  .dependsOn(core)

lazy val programming = (project in file("modules/programming"))
  .enablePlugins(PlayScala, PlayEbean, PlayEnhancer)
  .dependsOn(core)

lazy val question = (project in file("modules/question"))
  .enablePlugins(PlayScala, PlayEbean, PlayEnhancer)
  .dependsOn(core)

lazy val spread = (project in file("modules/spread"))
  .enablePlugins(PlayScala, PlayEbean, PlayEnhancer)
  .dependsOn(core)

lazy val sql = (project in file("modules/sql"))
  .enablePlugins(PlayScala, PlayEbean, PlayEnhancer)
  .dependsOn(core)

lazy val uml = (project in file("modules/uml"))
  .enablePlugins(PlayScala, PlayEbean, PlayEnhancer)
  .dependsOn(core)

lazy val web = (project in file("modules/web"))
  .enablePlugins(PlayScala, PlayEbean, PlayEnhancer)
  .dependsOn(core)

lazy val xml = (project in file("modules/xml"))
  .enablePlugins(PlayScala, PlayEbean, PlayEnhancer)
  .dependsOn(core)

// Used libraries from Maven Repository
libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.8-dmr",

  javaJdbc,
  //  "com.typesafe.play" %% "play-slick" % "3.0.0",
  //  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  guice
)

scalacOptions ++= Seq("-feature")

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator
