name := """it4all"""

organization := "is.informatik.uni-wuerzburg.de"

version := "0.9.0"

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-feature")

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

// Used libraries from Maven Repository
libraryDependencies ++= Seq(
  "org.mockito" % "mockito-core" % "2.11.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test",

  "mysql" % "mysql-connector-java" % "8.0.8-dmr",
  guice,

  // Test YAML for ex reading...
  "net.jcazevedo" %% "moultingyaml" % "0.4.0",

  // core
  "com.github.fge" % "json-schema-validator" % "2.2.6",
  "com.kjetland" % "mbknor-jackson-jsonschema_2.12" % "1.0.24",

  "com.typesafe.play" %% "play-slick" % "3.0.0",
  evolutions,
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",

  // Js-Libraries
  "org.webjars" % "ace" % "1.2.8",
  "org.webjars" % "bootstrap" % "3.3.7-1",
  "org.webjars.npm" % "jointjs" % "1.1.0",

  // Js-Libs for Uml
  "org.webjars" % "lodash" % "3.10.1",
  "org.webjars" % "backbonejs" % "1.3.3",

  "com.github.t3hnar" %% "scala-bcrypt" % "3.0",

  // Selenium and HtmlUnitDriver for Web+Js
  "org.seleniumhq.selenium" % "selenium-java" % "2.52.0",
  "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0",

  // MyBatis and JSqlParser for SQL
  "org.mybatis" % "mybatis" % "3.4.5",
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
  "commons-io" % "commons-io" % "2.4"

)

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator
