name := """it4all"""

organization := "is.informatik.uni-wuerzburg.de"

version := "0.9.1"

scalaVersion := "2.13.6"

scalacOptions ++= CompilerOptions.allOptions

updateOptions := updateOptions.value.withCachedResolution(true)

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    Universal / packageName := s"${name.value}",
    Compile / doc / sources := Seq.empty,
    Compile / packageDoc / publishArtifact := false
  )

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
  "com.pauldijou" %% "jwt-play" % "5.0.0", // Apache 2.0

  // Mongo database
  "org.reactivemongo" %% "play2-reactivemongo"            % "1.0.7-play28", // Apache 2.0
  "org.reactivemongo" %% "reactivemongo-play-json-compat" % "1.0.7-play29", // Apache 2.0

  // Other helpers
  "com.beachape"         %% "enumeratum-play"      % "1.7.0", // MIT
  "com.beachape"         %% "enumeratum-play-json" % "1.7.0", // MIT
  "com.github.t3hnar"    %% "scala-bcrypt"         % "4.3.0", // Apache 2.0
  "com.github.pathikrit" %% "better-files"         % "3.9.1", // MIT

  // Docker
  "com.spotify" % "docker-client" % "8.16.0", // Apache 2.0

  // GraphQL
  "org.sangria-graphql" %% "sangria"           % "2.1.3", // Apache 2.0
  "org.sangria-graphql" %% "sangria-play-json" % "2.0.2", // Apache 2.0

  // Sql
  "mysql"                 % "mysql-connector-java" % "8.0.26", // GPL 2.0
  "com.typesafe.play"    %% "play-slick"           % "5.0.0", // Apache 2.0
  "com.github.jsqlparser" % "jsqlparser"           % "4.2", // Apache 2.0

  // DTD Parser,
  "de.uniwue" %% "it4all_dtd_parser" % "0.5.0",
  // Web correction
  "org.nanohttpd"           % "nanohttpd-webserver" % "2.3.1" % Test,
  "org.seleniumhq.selenium" % "selenium-java"       % "3.141.59",
  "org.seleniumhq.selenium" % "htmlunit-driver"     % "2.52.0"
)
