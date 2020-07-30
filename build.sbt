name := """it4all"""

organization := "is.informatik.uni-wuerzburg.de"

version := "0.9.1"

scalaVersion := "2.13.3"

scalacOptions ++= CompilerOptions.allOptions

// Wart remover for scalac options
wartremoverWarnings ++= Warts.allBut(
  Wart.DefaultArguments,
  Wart.Equals,
  Wart.ImplicitParameter,
  Wart.Nothing,
  Wart.Recursion,
  Wart.Any,
  Wart.Serializable,
  Wart.JavaSerializable,
  Wart.Product
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
  ("Snapshot Artifactory" at s"$artifactoryUrl/libs-snapshot/").withAllowInsecureProtocol(true)
)

libraryDependencies ++= Seq(
  guice,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test, // Apache 2.0

  // JWT
  "com.pauldijou" %% "jwt-play" % "4.3.0", // Apache 2.0

  // Mongo database
  "org.reactivemongo" %% "play2-reactivemongo"            % "1.0.0-play28-rc.2", // Apache 2.0
  "org.reactivemongo" %% "reactivemongo-play-json-compat" % "1.0.0-play29-rc.2", // Apache 2.0

  // Other helpers
  "com.beachape"         %% "enumeratum-play"      % "1.6.0", // MIT
  "com.beachape"         %% "enumeratum-play-json" % "1.6.0", // MIT
  "com.github.t3hnar"    %% "scala-bcrypt"         % "4.3.0", // Apache 2.0
  "com.github.pathikrit" %% "better-files"         % "3.9.1", // MIT

  // Docker
  "com.spotify" % "docker-client" % "8.16.0", // Apache 2.0

  // GraphQL
  "org.sangria-graphql" %% "sangria"           % "2.0.0", // Apache 2.0
  "org.sangria-graphql" %% "sangria-play-json" % "2.0.1", // Apache 2.0

  // Sql
  "mysql"                 % "mysql-connector-java" % "8.0.21", // GPL 2.0
  "com.typesafe.play"    %% "play-slick"           % "5.0.0", // Apache 2.0
  "com.github.jsqlparser" % "jsqlparser"           % "3.2", // Apache 2.0

  // DTD Parser
  "de.uniwue" %% "it4all_dtd_parser" % "0.5.0",
  // Web tester
  "de.uniwue" %% "it4all_webtester" % "0.5.0"
)
