package model.xml

object XmlExTextParser {

  private val mappings: Map[String, String] = Map(
    "Gästen" -> "gast",
    "Vor" -> "vorname",
    "Getränke" -> "getraenk",
    "Getränk" -> "getraenk"
  )


  def parseGrammarText(text: String): String = {
    val newText = text

    text
  }

}
