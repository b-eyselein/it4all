name := """it4all"""

organization := "is.informatik.uni-wuerzburg.de"

version := "0.9.1"

scalaVersion := "2.13.1"

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

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    packageName in Universal := s"${name.value}"
  )

// TODO: For ActionRefiner ToolMainAction
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)


val artifactoryUrl = "http://artifactory-ls6.informatik.uni-wuerzburg.de/artifactory"

resolvers ++= Seq(
  // LS 6 Uni Wue Artifactory
  ("Artifactory" at s"$artifactoryUrl/libs-release").withAllowInsecureProtocol(true),
  ("Snapshot Artifactory" at s"$artifactoryUrl/libs-snapshot/").withAllowInsecureProtocol(true),

  // Typescript Interfaces Plugin
  // Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  guice,
  ws,

  "com.pauldijou" %% "jwt-play" % "4.2.0",

  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,

  "mysql" % "mysql-connector-java" % "8.0.19", // GPL 2.0

  "com.typesafe.play" %% "play-slick" % "5.0.0", // Apache 2.0
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0", // Apache 2.0

  "com.beachape" %% "enumeratum-play" % "1.5.16", // MIT
  "com.beachape" %% "enumeratum-play-json" % "1.5.16", // MIT

  "com.github.t3hnar" %% "scala-bcrypt" % "4.1", // Apache 2.0

  "com.github.pathikrit" %% "better-files" % "3.8.0", // MIT

  "com.spotify" % "docker-client" % "8.16.0", // Apache 2.0

  // Typescript interface generation
  "nl.codestar" %% "scala-tsi" % "0.2.2-SNAPSHOT",

  // Sql
  "com.github.jsqlparser" % "jsqlparser" % "3.1", // Apache 2.0

  // DTD Parser
  "de.uniwue" %% "it4all_dtd_parser" % "0.5.0",

  // Web tester
  "de.uniwue" %% "it4all_webtester" % "0.5.0"
)
