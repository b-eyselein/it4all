val enumeratumVersion = "1.7.2"
val playSlickVersion  = "5.1.0"
val slickPgVersion    = "0.21.1"
val seleniumVersion   = "4.8.0"

val commonSettings = Seq(
  scalaVersion := "2.13.10",
  organization := "de.uniwue.is",
  libraryDependencies ++= Seq(
    "org.scalatestplus.play" %% "scalatestplus-play"   % "5.1.0" % Test, // Apache 2.0
    "com.github.pathikrit"   %% "better-files"         % "3.9.1", // MIT
    "com.beachape"           %% "enumeratum-play"      % enumeratumVersion, // MIT
    "com.beachape"           %% "enumeratum-play-json" % enumeratumVersion // MIT
  )
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

Universal / mappings += (baseDirectory.value / "docker-compose.yaml") -> "docker-compose.yaml"

libraryDependencies ++= Seq(
  guice,

  // JWT
  "com.github.jwt-scala" %% "jwt-play" % "9.1.2", // Apache 2.0

  // Other helpers
  "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0", // Apache 2.0

  // Docker
  "com.spotify" % "docker-client" % "8.16.0", // Apache 2.0

  // GraphQL
  "org.sangria-graphql" %% "sangria"           % "3.5.0", // Apache 2.0
  "org.sangria-graphql" %% "sangria-play-json" % "2.0.2", // Apache 2.0

  // Sql
  "org.postgresql"       % "postgresql"            % "42.5.3",         // BSD-2
  "com.typesafe.play"   %% "play-slick"            % playSlickVersion, // Apache 2.0
  "com.typesafe.play"   %% "play-slick-evolutions" % playSlickVersion, // Apache 2.0
  "com.github.tminglei" %% "slick-pg"              % slickPgVersion,   // BSD-2
  "com.github.tminglei" %% "slick-pg_play-json"    % slickPgVersion,   // BSD-2

  // Sql correction
  "mysql"                 % "mysql-connector-java" % "8.0.32", // GPL 2.0
  "com.github.jsqlparser" % "jsqlparser"           % "4.5",    // Apache 2.0

  // Web correction
  "org.nanohttpd"           % "nanohttpd-webserver" % "2.3.1" % Test, // BSD 3-clause
  "org.seleniumhq.selenium" % "selenium-java"       % seleniumVersion, // Apache 2.0
  "org.seleniumhq.selenium" % "htmlunit-driver"     % seleniumVersion // Apache 2.0
)
