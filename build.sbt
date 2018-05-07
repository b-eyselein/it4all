name := """it4all"""

organization := "is.informatik.uni-wuerzburg.de"

version := "0.9.0"

scalaVersion := "2.12.5"

scalacOptions ++= Seq("-feature")

lazy val root = (project in file(".")).enablePlugins(PlayScala)
  .settings(packageName in Universal := s"${name.value}")


// Resolver for JFrog Uni Wue
resolvers ++= Seq(
  "Artifactory" at "http://artifactory-ls6.informatik.uni-wuerzburg.de/artifactory/libs-release",
  "Snapshot Artifactory" at "http://artifactory-ls6.informatik.uni-wuerzburg.de/artifactory/libs-snapshot/"
)

val webJarDependencies = Seq(
  // Js-Libraries
  "org.webjars.npm" % "ace-builds" % "1.3.3",

  "org.webjars" % "jquery" % "3.3.1",
  "org.webjars" % "bootstrap" % "3.3.7-1",
  // FIXME: update to version 4.0

  "org.webjars.npm" % "jointjs" % "2.0.1",

  // Js-Libs for Uml
  "org.webjars" % "lodash" % "3.10.1",
  "org.webjars" % "backbonejs" % "1.3.3",

  "org.webjars.npm" % "autosize" % "4.0.0",
  "org.webjars.bower" % "filesaver" % "1.3.3",

)

libraryDependencies ++= webJarDependencies

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
  evolutions,
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
  "de.uni-wuerzburg.is" % "scala_dtd_2.12" % "0.2.0-SNAPSHOT"
)

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator
