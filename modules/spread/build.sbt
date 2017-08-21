name := "spread"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")

// JaCoCo - siehe project/plugins.sbt
jacoco.settings

libraryDependencies ++= Seq(
  // Apache POI for Excel
  "org.apache.poi" % "poi" % "3.16",
  "org.apache.poi" % "poi-excelant" % "3.16",
  "org.apache.poi" % "poi-ooxml" % "3.16",
  "org.apache.poi" % "poi-ooxml-schemas" % "3.16",
  "org.apache.poi" % "poi-scratchpad" % "3.16",
  "org.apache.xmlbeans" % "xmlbeans" % "2.6.0",


  // ODF Toolkit for OpenOffice Calc
  "commons-validator" % "commons-validator" % "1.5.0",
  "net.rootdev" % "java-rdfa" % "0.4.2",
  "org.apache.jena" % "jena-core" % "2.11.2",
  "org.apache.odftoolkit" % "odfdom-java" % "0.8.11-incubating",
  "org.apache.odftoolkit" % "simple-odf" % "0.8.2-incubating",
  "org.apache.odftoolkit" % "taglets" % "0.8.11-incubating",
  "xerces" % "xercesImpl" % "2.9.",
  "xml-apis" % "xml-apis" % "1.3.04"
)
