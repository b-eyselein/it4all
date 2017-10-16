name := "sql"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")

libraryDependencies ++= Seq(
  javaJdbc, Common.mybatis, Common.jsqlparser
)

EclipseKeys.classpathTransformerFactories := Seq(ClasspathentryTransformer)