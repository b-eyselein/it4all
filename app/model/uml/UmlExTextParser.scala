package model.uml

import java.util.regex.Pattern

import scala.language.postfixOps
import scala.util.matching.Regex

class UmlExTextParser(rawText: String, val mappings: Map[String, String], val toIgnore: Seq[String]) {

  private val capWordsRegex: Regex   = """[A-Z][a-zäöüß]*""".r
  private val cssClassName : String  = "non-marked"
  private val classSelectionFunction = "onclick=\"select(this)\""
  private val diagramDrawingFunction = "draggable=\"true\" ondragstart=\"drag(event)\""

  val capitalizedWords  : Set[String] = capWordsRegex findAllIn rawText toSet
  val simpleReplacements: Set[String] = capitalizedWords filter (k => !mappings.isDefinedAt(k) && !toIgnore.contains(k))

  def replaceWithMappingSpan(text: String, key: String, value: String, function: String): String = {
    val matcher = Pattern.compile(key + "\\b").matcher(text)
    matcher.replaceAll( s"""<span class="$cssClassName" $function data-baseform="$value">$key</span>""")
  }

  def parseTextForClassSel: String = parseText(classSelectionFunction)

  def parseTextForDiagDrawing: String = parseText(diagramDrawingFunction)

  private def parseText(function: String): String = {
    var newText = rawText

    for (simpleRep <- simpleReplacements)
      newText = replaceWithMappingSpan(newText, simpleRep, simpleRep, function)

    for ((k, v) <- mappings)
      newText = replaceWithMappingSpan(newText, k, v, function)

    newText
  }

}
