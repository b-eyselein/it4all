name := "sql"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")

libraryDependencies ++= Seq(
  javaJdbc, Common.mybatis, Common.jsqlparser
)

// JaCoCo - siehe project/plugins.sbt
jacoco.settings

EclipseKeys.classpathTransformerFactories := Seq(ClasspathentryTransformer)