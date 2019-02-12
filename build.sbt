name := """it4all"""

organization := "is.informatik.uni-wuerzburg.de"

version := "0.9.0"

scalaVersion := "2.12.8"

// Compile to java 8 for debian...
javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

scalacOptions ++= CompilerOptions.allOptions

// Wart remover for scalac options
wartremoverWarnings ++= Warts.allBut(Wart.DefaultArguments, Wart.Equals, Wart.ImplicitParameter, Wart.Nothing)

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


val webJarDependencies = Seq(
  "org.webjars.npm" % "jquery" % "3.3.1", "org.webjars.npm" % "types__jquery" % "3.3.29",

  "org.webjars" % "popper.js" % "1.14.6",

  "org.webjars" % "octicons" % "4.3.0",

  "org.webjars.npm" % "bootstrap" % "4.3.0", "org.webjars.npm" % "types__bootstrap" % "4.2.0",

  "org.webjars.npm" % "systemjs" % "0.21.5", "org.webjars.npm" % "types__systemjs" % "0.20.6",

  "org.webjars.npm" % "jointjs" % "2.2.1",

  "org.webjars.npm" % "types__backbone" % "1.3.45",

  "org.webjars.npm" % "lodash" % "4.17.11", "org.webjars.npm" % "types__lodash" % "4.14.120",
  "org.webjars.npm" % "types__underscore" % "1.8.9",

  "org.webjars.npm" % "codemirror" % "5.43.0", "org.webjars.npm" % "types__codemirror" % "0.0.71",

  "org.webjars.npm" % "graphlib" % "2.1.7", "org.webjars.npm" % "types__graphlib" % "2.1.4"
)


libraryDependencies ++= webJarDependencies

resolveFromWebjarsNodeModulesDir := true

dependencyOverrides ++= Seq(
  "org.webjars.npm" % "types__jquery" % "3.3.29",
  "org.webjars.npm" % "types__underscore" % "1.8.9",
  "org.webjars.npm" % "types__sizzle" % "2.3.2",
  "org.webjars.npm" % "types__estree" % "0.0.39"
  //  , "org.webjars.npm" % "types__tern" % "0.22.1"
)

excludeDependencies ++= Seq(
  //  TODO: exclude tern since it has an compile error
  ExclusionRule(organization = "org.webjars.npm", name = "types__tern")
)

// Used libraries from Maven Repository
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % "test",

  "mysql" % "mysql-connector-java" % "8.0.15",

  // Better enums for scala
  "com.beachape" %% "enumeratum-play" % "1.5.16",
  "com.beachape" %% "enumeratum-play-json" % "1.5.16",

  // Dependency injection
  guice,
  "net.codingwell" %% "scala-guice" % "4.2.2",

  "net.jcazevedo" %% "moultingyaml" % "0.4.0",

  ws,

  // core
  "com.typesafe.play" %% "play-slick" % "4.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0",

  "com.github.t3hnar" %% "scala-bcrypt" % "3.1",

  // Betterfiles
  "com.github.pathikrit" %% "better-files" % "3.7.0",

  // Selenium and HtmlUnitDriver for Web+Js
  "org.seleniumhq.selenium" % "selenium-java" % "3.141.59",
  "org.seleniumhq.selenium" % "htmlunit-driver" % "2.33.3",

  // Json Schema Parser/Validator for Json
  "com.eclipsesource" %% "play-json-schema-validator" % "0.9.5-M4",

  // MyBatis and JSqlParser for SQL
  "com.github.jsqlparser" % "jsqlparser" % "1.4",

  // Programming
  "com.spotify" % "docker-client" % "8.15.0",

  // Apache POI for Excel
  "org.apache.poi" % "poi" % "3.17",
  "org.apache.poi" % "poi-excelant" % "3.17",
  "org.apache.poi" % "poi-ooxml" % "3.17",
  "org.apache.poi" % "poi-ooxml-schemas" % "3.17",
  "org.apache.poi" % "poi-scratchpad" % "3.17",
  "org.apache.xmlbeans" % "xmlbeans" % "2.6.0",

  // ODF Toolkit for OpenOffice Calc
  "commons-validator" % "commons-validator" % "1.5.0", // 1.6
  "net.rootdev" % "java-rdfa" % "0.4.2",
  "org.apache.jena" % "jena-core" % "2.11.2", // 3.4.0
  "org.apache.odftoolkit" % "odfdom-java" % "0.8.11-incubating",
  "org.apache.odftoolkit" % "simple-odf" % "0.8.2-incubating",
  "org.apache.odftoolkit" % "taglets" % "0.8.11-incubating",
  "xerces" % "xercesImpl" % "2.9.0", // 2.11.0-22
  "xml-apis" % "xml-apis" % "1.3.04",

  // Apache Commons IO
  "commons-io" % "commons-io" % "2.4",

  // DTD Parser
  "de.uniwue" %% "scala_dtd" % "0.3.0-SNAPSHOT",
  //  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1"
)

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator
