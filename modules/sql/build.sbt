name := "sql"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")

libraryDependencies ++= Seq(
  javaJdbc,
  
  // JSQL-Parser
  "com.github.jsqlparser" % "jsqlparser" % "0.9.6"
)
