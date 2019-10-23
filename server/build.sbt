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

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(packageName in Universal := s"${name.value}")

resolvers ++= Seq(
  // LS 6 Uni Wue Artifactory
  ("Artifactory" at "http://artifactory-ls6.informatik.uni-wuerzburg.de/artifactory/libs-release")
    .withAllowInsecureProtocol(true),
  ("Snapshot Artifactory" at "http://artifactory-ls6.informatik.uni-wuerzburg.de/artifactory/libs-snapshot/")
    .withAllowInsecureProtocol(true),

  Resolver.bintrayRepo("webjars", "maven"),

  // Repo for play-json-schema-validator
  "emueller-bintray" at "https://dl.bintray.com/emueller/maven"
)

libraryDependencies ++= Seq(
  guice,
  ws,

  "com.pauldijou" %% "jwt-play" % "4.1.0",

  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,

  "mysql" % "mysql-connector-java" % "8.0.18", // GPL 2.0

  "com.typesafe.play" %% "play-slick" % "4.0.2", // Apache 2.0
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2", // Apache 2.0

  "com.beachape" %% "enumeratum-play" % "1.5.16", // MIT
  "com.beachape" %% "enumeratum-play-json" % "1.5.16", // MIT

  "net.jcazevedo" %% "moultingyaml" % "0.4.1", // MIT

  "com.github.t3hnar" %% "scala-bcrypt" % "4.1", // Apache 2.0

  "com.github.pathikrit" %% "better-files" % "3.8.0", // MIT

  "com.spotify" % "docker-client" % "8.16.0", // Apache 2.0

  // Json Schema Parser/Validator for Json
  "com.eclipsesource" %% "play-json-schema-validator" % "0.9.5", // Apache 2.0

  // Sql
  "com.github.jsqlparser" % "jsqlparser" % "3.0", // Apache 2.0

  // DTD Parser
  "de.uniwue" %% "it4all_dtd_parser" % "0.5.0",

  // Web tester
  "de.uniwue" %% "it4all_webtester" % "0.3.0"
)
