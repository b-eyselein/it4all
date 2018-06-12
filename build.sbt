name := """it4all"""

organization := "is.informatik.uni-wuerzburg.de"

version := "0.9.0"

scalaVersion := "2.12.6"

scalacOptions ++= Seq("-feature")

updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(packageName in Universal := s"${name.value}")

// Resolver for JFrog Uni Wue
resolvers ++= Seq(
  "Artifactory" at "http://artifactory-ls6.informatik.uni-wuerzburg.de/artifactory/libs-release",
  "Snapshot Artifactory" at "http://artifactory-ls6.informatik.uni-wuerzburg.de/artifactory/libs-snapshot/",

  Resolver.bintrayRepo("webjars", "maven")
)

val webJarDependencies = Seq(
  "org.webjars.npm" % "jquery" % "3.3.1", "org.webjars.npm" % "types__jquery" % "3.3.2",

  "org.webjars" % "popper.js" % "1.14.1",

  "org.webjars" % "octicons" % "4.3.0",

  "org.webjars.npm" % "bootstrap" % "4.1.1", "org.webjars.npm" % "types__bootstrap" % "4.1.0",

  "org.webjars.npm" % "systemjs" % "0.21.3", "org.webjars.npm" % "types__systemjs" % "0.20.6",

  "org.webjars.npm" % "jointjs" % "2.1.2",

//  "org.webjars.npm" % "backbone" % "1.3.3",
  "org.webjars.npm" % "types__backbone" % "1.3.42",

  "org.webjars.npm" % "lodash" % "4.17.10", "org.webjars.npm" % "types__lodash" % "4.14.109", "org.webjars.npm" % "types__underscore" % "1.8.8",

  "org.webjars.npm" % "codemirror" % "5.38.0", "org.webjars.npm" % "types__codemirror" % "0.0.56",

  "org.webjars.npm" % "graphlib" % "2.1.5", "org.webjars.npm" % "types__graphlib" % "2.1.4"

//  "org.webjars.npm" % "autosize" % "4.0.0",

//  "org.webjars.bower" % "filesaver" % "1.3.3"
)

libraryDependencies ++= webJarDependencies

resolveFromWebjarsNodeModulesDir := true

// Used libraries from Maven Repository
libraryDependencies ++= Seq(
  "org.mockito" % "mockito-core" % "2.18.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test",

  "mysql" % "mysql-connector-java" % "6.0.6",

  // Better enums for scala
  "com.beachape" %% "enumeratum-play" % "1.5.14",
  "com.beachape" %% "enumeratum-play-json" % "1.5.14",

  // Dependency injection
  guice,
  "net.codingwell" %% "scala-guice" % "4.2.0",

  "net.jcazevedo" %% "moultingyaml" % "0.4.0",

  ws,

  // core
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3",

  "com.github.t3hnar" %% "scala-bcrypt" % "3.1",

  // Selenium and HtmlUnitDriver for Web+Js
  "org.seleniumhq.selenium" % "selenium-java" % "3.11.0",
  "org.seleniumhq.selenium" % "htmlunit-driver" % "2.30.0",

  // MyBatis and JSqlParser for SQL
  //  "org.mybatis" % "mybatis" % "3.4.5",
  "com.github.jsqlparser" % "jsqlparser" % "1.1",

  // Programming
  "com.github.docker-java" % "docker-java" % "3.0.14",

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
  "xerces" % "xercesImpl" % "2.9.", // 2.11.0-22
  "xml-apis" % "xml-apis" % "1.3.04",

  // Apache Commons IO
  "commons-io" % "commons-io" % "2.4",

  // DTD Parser
  //  "de.uni-wuerzburg.is" % "scala_dtd_2.12" % "0.3.0-SNAPSHOT"
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.0"
)

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator
