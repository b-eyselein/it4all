val enumeratumVersion = "1.7.0"

val commonSettings = Seq(
  scalaVersion := "2.13.8",
  organization := "de.uniwue.is",
  libraryDependencies ++= Seq(
    "org.scalatestplus.play" %% "scalatestplus-play"   % "5.1.0" % Test, // Apache 2.0
    "com.github.pathikrit"   %% "better-files"         % "3.9.1", // MIT
    "com.beachape"           %% "enumeratum-play"      % enumeratumVersion, // MIT
    "com.beachape"           %% "enumeratum-play-json" % enumeratumVersion // MIT
  )
)

lazy val dtd_parser = (project in file("./dtd_parser"))
  .settings(commonSettings: _*)
  .settings(
    name    := "dtd_parser",
    version := "0.1.0-SNAPSHOT"
  )

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .aggregate(dtd_parser)
  .dependsOn(dtd_parser)
  .settings(commonSettings)
  .settings(
    name                                   := "it4all",
    version                                := "0.9.1",
    Universal / packageName                := s"${name.value}",
    Compile / doc / sources                := Seq.empty,
    Compile / packageDoc / publishArtifact := false
  )

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.4"

libraryDependencies ++= Seq(
  guice,

  // JWT
  "com.pauldijou" %% "jwt-play" % "5.0.0", // Apache 2.0

  // Mongo database
  "org.reactivemongo" %% "play2-reactivemongo"            % "1.1.0-play28-RC2", // Apache 2.0
  "org.reactivemongo" %% "reactivemongo-play-json-compat" % "1.1.0-play29-RC2", // Apache 2.0

  // Other helpers

  "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0", // Apache 2.0

  // Docker
  "com.spotify" % "docker-client" % "8.16.0", // Apache 2.0

  // GraphQL
  "org.sangria-graphql" %% "sangria"           % "2.1.6", // Apache 2.0
  "org.sangria-graphql" %% "sangria-play-json" % "2.0.2", // Apache 2.0

  // Sql
  "mysql"                 % "mysql-connector-java" % "8.0.27", // GPL 2.0
  "com.typesafe.play"    %% "play-slick"           % "5.0.0",  // Apache 2.0
  "com.github.jsqlparser" % "jsqlparser"           % "4.3",    // Apache 2.0

  // Web correction
  "org.nanohttpd"           % "nanohttpd-webserver" % "2.3.1" % Test, // BSD 3-clause
  "org.seleniumhq.selenium" % "selenium-java"       % "4.1.1", // Apache 2.0
  "org.seleniumhq.selenium" % "htmlunit-driver"     % "3.56.0" // Apache 2.0
)
