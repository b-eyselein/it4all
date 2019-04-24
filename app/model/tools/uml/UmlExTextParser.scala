package model.tools.uml

import java.util.regex.Pattern

import scala.util.matching.Regex

class UmlExTextParser(rawText: String, val mappings: Map[String, String], val toIgnore: Seq[String]) {

  private val capWordsRegex: Regex  = """[A-Z][a-zäöüß]*""".r
  private val cssClassName : String = "text-muted"

  private val capitalizedWords  : Set[String] = capWordsRegex findAllIn rawText toSet
  private val simpleReplacements: Set[String] = capitalizedWords filter (k => !mappings.isDefinedAt(k) && !toIgnore.contains(k))

  private def replaceWithMappingSpan(text: String, key: String, value: String): String = {
    val matcher = Pattern.compile(key + "\\b").matcher(text)
    matcher.replaceAll( s"""<span class="$cssClassName" data-baseform="$value">$key</span>""")
  }

  def parseText: String = {
    var newText = rawText

    for (simpleRep <- simpleReplacements)
      newText = replaceWithMappingSpan(newText, simpleRep, simpleRep)

    for ((k, v) <- mappings)
      newText = replaceWithMappingSpan(newText, k, v)

    newText
  }

}
