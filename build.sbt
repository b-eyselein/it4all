name := """it4all"""

organization := "is.informatik.uni-wuerzburg.de"

version := "0.9.0"

scalaVersion := "2.12.8"

// Compile to java 8 for debian...
javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

scalacOptions ++= Seq(
  //  "-Xfatal-warnings",
  "-Xfuture",
  "-Ypatmat-exhaust-depth", "40",
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked"
)

// Xlint options for scalac
scalacOptions ++= Seq(
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match", // Pattern match may not be typesafe.
)

// Warnings for scalac
scalacOptions ++= Seq(
  "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification", // Enable partial unification in type constructor inference
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  //  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  //  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  //  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
)

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
  "org.webjars.npm" % "jquery" % "3.3.1", "org.webjars.npm" % "types__jquery" % "3.3.22",

  "org.webjars" % "popper.js" % "1.14.3",

  "org.webjars" % "octicons" % "4.3.0",

  "org.webjars.npm" % "bootstrap" % "4.1.3", "org.webjars.npm" % "types__bootstrap" % "4.1.2",

  "org.webjars.npm" % "systemjs" % "0.21.4", "org.webjars.npm" % "types__systemjs" % "0.20.6", // TODO: "2.0.2" and OK

  "org.webjars.npm" % "jointjs" % "2.1.4", // TODO: 2.2.1

  //  "org.webjars.npm" % "backbone" % "1.3.3",
  "org.webjars.npm" % "types__backbone" % "1.3.43",

  "org.webjars.npm" % "lodash" % "4.17.10", "org.webjars.npm" % "types__lodash" % "4.14.116",
  "org.webjars.npm" % "types__underscore" % "1.8.9",

  "org.webjars.npm" % "codemirror" % "5.42.2", "org.webjars.npm" % "types__codemirror" % "0.0.65", // TODO: 0.0.70

  "org.webjars.npm" % "graphlib" % "2.1.5", "org.webjars.npm" % "types__graphlib" % "2.1.4",

  //  "org.webjars.npm" % "autosize" % "4.0.0",

  //  "org.webjars.bower" % "filesaver" % "1.3.3"
)

libraryDependencies ++= webJarDependencies

resolveFromWebjarsNodeModulesDir := true

dependencyOverrides ++= Seq(
  "org.webjars.npm" % "types__jquery" % "3.3.17",
  "org.webjars.npm" % "types__underscore" % "1.8.9",
  "org.webjars.npm" % "types__sizzle" % "2.3.2"
)

// Used libraries from Maven Repository
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test",

  "mysql" % "mysql-connector-java" % "8.0.13",

  // Better enums for scala
  "com.beachape" %% "enumeratum-play" % "1.5.14",
  "com.beachape" %% "enumeratum-play-json" % "1.5.14",

  // Dependency injection
  guice,
  "net.codingwell" %% "scala-guice" % "4.2.1",

  "net.jcazevedo" %% "moultingyaml" % "0.4.0",

  ws,

  // core
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3",

  "com.github.t3hnar" %% "scala-bcrypt" % "3.1",

  // Betterfiles
  "com.github.pathikrit" %% "better-files" % "3.7.0",

  // Selenium and HtmlUnitDriver for Web+Js
  "org.seleniumhq.selenium" % "selenium-java" % "3.141.59",
  "org.seleniumhq.selenium" % "htmlunit-driver" % "2.33.3",

  // Json Schema Parser/Validator for Json
  "com.eclipsesource"  %% "play-json-schema-validator" % "0.9.5-M4",
  
  // MyBatis and JSqlParser for SQL
  "com.github.jsqlparser" % "jsqlparser" % "1.3",

  // Programming
  "com.spotify" % "docker-client" % "8.14.5",

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
  //  "de.uni-wuerzburg.is" % "scala_dtd_2.12" % "0.3.0-SNAPSHOT"
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1"
)

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator
