name := """it4all"""

organization := "is.informatik.uni-wuerzburg.de"

version := "0.9.1"

scalaVersion := "2.13.0" // TODO: 2.13.1 after https://github.com/scoverage/scalac-scoverage-plugin/pull/279

scalacOptions ++= CompilerOptions.allOptions

// Wart remover for scalac options
wartremoverWarnings ++= Warts.allBut(
  Wart.DefaultArguments, Wart.Equals, Wart.ImplicitParameter, Wart.Nothing, Wart.Recursion, Wart.Any
)

wartremoverExcluded ++= routes.in(Compile).value
wartremoverExcluded += sourceManaged.value
wartremoverExcluded += (target in TwirlKeys.compileTemplates).value

updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

val tsiClasses = Seq(
  "ExerciseCollection",
  "Exercise",
  "ProgSolution",
  "RegexExerciseContent",
  "SqlExerciseContent",
  "UmlExerciseContent",
  "WebExerciseContent", "WebCompleteResult",
  "XmlExerciseContent"
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    packageName in Universal := s"${name.value}",

    typescriptClassesToGenerateFor := tsiClasses,
    typescriptOutputFile := baseDirectory.value.getParentFile / "client" / "src" / "app" / "_interfaces" / "models.ts",
    typescriptGenerationImports := Seq(
      "model.tools.collectionTools._",
      "model.tools.collectionTools.programming._",
      "model.tools.collectionTools.regex._",
      "model.tools.collectionTools.sql._",
      "model.tools.collectionTools.uml._",
      "model.tools.collectionTools.web._",
      "model.tools.collectionTools.xml._",
      "model.MyTSInterfaceTypes._"
    )
  )

val artifactoryUrl = "http://artifactory-ls6.informatik.uni-wuerzburg.de/artifactory"

resolvers ++= Seq(
  // LS 6 Uni Wue Artifactory
  ("Artifactory" at s"$artifactoryUrl/libs-release").withAllowInsecureProtocol(true),
  ("Snapshot Artifactory" at s"$artifactoryUrl/libs-snapshot/").withAllowInsecureProtocol(true),

  // Typescript Interfaces Plugin
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  guice,
  ws,

  "com.pauldijou" %% "jwt-play" % "4.2.0",

  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,

  "mysql" % "mysql-connector-java" % "8.0.18", // GPL 2.0

  "com.typesafe.play" %% "play-slick" % "4.0.2", // Apache 2.0
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2", // Apache 2.0

  "com.beachape" %% "enumeratum-play" % "1.5.16", // MIT
  "com.beachape" %% "enumeratum-play-json" % "1.5.16", // MIT

  "com.github.t3hnar" %% "scala-bcrypt" % "4.1", // Apache 2.0

  "com.github.pathikrit" %% "better-files" % "3.8.0", // MIT

  "com.spotify" % "docker-client" % "8.16.0", // Apache 2.0

  // Sql
  "com.github.jsqlparser" % "jsqlparser" % "3.1", // Apache 2.0

  // DTD Parser
  "de.uniwue" %% "it4all_dtd_parser" % "0.5.0",

  // Web tester
  "de.uniwue" %% "it4all_webtester" % "0.5.0"
)
