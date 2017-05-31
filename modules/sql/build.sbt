name := "sql"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")

// JaCoCo - siehe project/plugins.sbt
jacoco.settings

libraryDependencies ++= Seq(
  javaJdbc,
  
  // Mybatis for ScriptRunner
  "org.mybatis" % "mybatis" % "3.4.4",
  
  // JSQL-Parser
  "com.github.jsqlparser" % "jsqlparser" % "1.0"
)
