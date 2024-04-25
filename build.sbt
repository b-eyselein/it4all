val commonSettings = Seq(
  scalaVersion := "2.13.13",
  organization := "de.uniwue.is",
  libraryDependencies ++= Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test, // Apache 2.0
    "com.github.pathikrit"   %% "better-files"       % "3.9.2" // MIT
  ),
  // scalacOptions ++= Seq("-Wunused"),
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)

lazy val dtd_parser = (project in file("./dtd_parser"))
  .settings(commonSettings)
  .settings(
    name                                            := "dtd_parser",
    version                                         := "0.1.0-SNAPSHOT",
    libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
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

Universal / mappings += (baseDirectory.value / "docker-compose_prod.yaml") -> "docker-compose.yaml"

val playSlickVersion = "6.0.0-M2"
val seleniumVersion  = "4.13.0"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.2"

libraryDependencies ++= Seq(
  guice,

  // better enums
  "com.beachape" %% "enumeratum-play-json" % "1.7.3", // MIT

  // JWT
  "com.github.jwt-scala" %% "jwt-play-json" % "9.4.5", // Apache 2.0

  // BCrypt
  "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0", // Apache 2.0

  // Docker
  "org.mandas"                       % "docker-client"                    % "7.0.8",       // Apache 2.0
  "org.jboss.resteasy"               % "resteasy-client"                  % "6.1.0.Final", // Apache 2.0
  "org.jboss.resteasy"               % "resteasy-core"                    % "6.1.0.Final", // Apache 2.0
  "com.fasterxml.jackson.jakarta.rs" % "jackson-jakarta-rs-json-provider" % "2.13.1",      // Apache 2.0

  // GraphQL
  "org.sangria-graphql" %% "sangria"           % "4.0.2", // Apache 2.0
  "org.sangria-graphql" %% "sangria-play-json" % "2.0.2", // Apache 2.0

  // Sql
  "org.postgresql"     % "postgresql"            % "42.7.1",         // BSD-2
  "org.playframework" %% "play-slick"            % playSlickVersion, // Apache 2.0
  "org.playframework" %% "play-slick-evolutions" % playSlickVersion, // Apache 2.0

  // Sql correction
  "mysql"                 % "mysql-connector-java" % "8.0.33", // GPL 2.0
  "com.github.jsqlparser" % "jsqlparser"           % "4.6",    // Apache 2.0

  // Web correction
  "org.nanohttpd"           % "nanohttpd-webserver" % "2.3.1" % Test, // BSD 3-clause
  "org.seleniumhq.selenium" % "selenium-java"       % seleniumVersion, // Apache 2.0
  "org.seleniumhq.selenium" % "htmlunit-driver"     % seleniumVersion // Apache 2.0
)
