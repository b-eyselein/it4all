name := """it4all"""

organization := "is.informatik.uni-wuerzburg.de"

version := "0.9.0"

scalaVersion := "2.12.8"

scalacOptions ++= CompilerOptions.allOptions

// Wart remover for scalac options
wartremoverWarnings ++= Warts.allBut(Wart.DefaultArguments, Wart.Equals, Wart.ImplicitParameter, Wart.Nothing, Wart.Recursion)

wartremoverExcluded ++= routes.in(Compile).value
wartremoverExcluded += sourceManaged.value
wartremoverExcluded += (target in TwirlKeys.compileTemplates).value


updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(packageName in Universal := s"${name.value}")

// Resolver for JFrog Uni Wue
resolvers ++= Seq(
  // LS 6 Uni Wue Artifactory
  "Artifactory" at "http://artifactory-ls6.informatik.uni-wuerzburg.de/artifactory/libs-release",
  "Snapshot Artifactory" at "http://artifactory-ls6.informatik.uni-wuerzburg.de/artifactory/libs-snapshot/",

  Resolver.bintrayRepo("webjars", "maven"),

  // Repo for play-json-schema-validator
  "emueller-bintray" at "http://dl.bintray.com/emueller/maven"
)

val jqueryVersion = "3.4.1"
val jqueryTypesVersion = "3.3.29"

val webJarDependencies = Seq(
  "org.webjars.npm" % "jquery" % jqueryVersion, // MIT
  "org.webjars.npm" % "types__jquery" % jqueryTypesVersion, // MIT

  "org.webjars" % "popper.js" % "1.15.0", // MIT

  "org.webjars" % "octicons" % "4.3.0", // MIT

  "org.webjars.npm" % "bootstrap" % "4.3.1", // MIT
  "org.webjars.npm" % "types__bootstrap" % "4.3.0", // MIT

  "org.webjars.npm" % "systemjs" % "0.21.6", // MIT, TODO: 3.1.6
  "org.webjars.npm" % "types__systemjs" % "0.20.6", // MIT

  "org.webjars.npm" % "jointjs" % "3.0.1", // MPL-2.0

  "org.webjars.npm" % "types__backbone" % "1.3.46", // MIT

  "org.webjars.npm" % "codemirror" % "5.48.0", // MIT
  "org.webjars.npm" % "types__codemirror" % "0.0.74", // MIT
  "org.webjars.npm" % "cm-show-invisibles" % "2.0.2", // MIT

  //  "org.webjars.npm" % "graphlib" % "2.1.7", // MIT
  "org.webjars.npm" % "types__graphlib" % "2.1.4" // MIT

)


libraryDependencies ++= webJarDependencies

resolveFromWebjarsNodeModulesDir := true

dependencyOverrides ++= Seq(
  "org.webjars.npm" % "types__jquery" % jqueryTypesVersion,
  "org.webjars.npm" % "types__underscore" % "1.8.14",
  "org.webjars.npm" % "types__sizzle" % "2.3.2", // MIT
//  "org.webjars.npm" % "types__estree" % "0.0.39", // MIT
)

excludeDependencies ++= Seq(
  // exclude tern since it has an compile error?
  ExclusionRule(organization = "org.webjars.npm", name = "types__tern")
)

// Used libraries from Maven Repository
libraryDependencies ++= Seq(
  // Dependency injection
  guice,

  ws,

  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,

  "mysql" % "mysql-connector-java" % "8.0.16", // GPL 2.0

  "com.typesafe.play" %% "play-slick" % "4.0.2", // Apache 2.0
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2", // Apache 2.0

  "com.beachape" %% "enumeratum-play" % "1.5.16", // MIT
  "com.beachape" %% "enumeratum-play-json" % "1.5.16", // MIT

  "net.jcazevedo" %% "moultingyaml" % "0.4.0", // MIT

  "com.github.t3hnar" %% "scala-bcrypt" % "4.1", // Apache 2.0

  "com.github.pathikrit" %% "better-files" % "3.8.0", // MIT

  "com.spotify" % "docker-client" % "8.16.0", // Apache 2.0

  // Json Schema Parser/Validator for Json
  "com.eclipsesource" %% "play-json-schema-validator" % "0.9.5-M4", // Apache 2.0

  // Sql
  "com.github.jsqlparser" % "jsqlparser" % "2.0", // Apache 2.0

  // DTD Parser
  "de.uniwue" %% "scala_dtd" % "0.4.0",

  // Web tester
  "de.uniwue" %% "it4all_webtester" % "0.2.3"
)
