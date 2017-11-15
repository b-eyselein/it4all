package model.uml

import java.util.regex.Pattern

import model.uml.UmlConsts._

import scala.language.postfixOps

class UmlExTextParser(rawText: String, val mappings: Map[String, String], val toIgnore: Seq[String]) {

  val capitalizedWords  : Set[String] = CapWordsRegex findAllIn rawText toSet
  val simpleReplacements: Set[String] = capitalizedWords.filter(k => !mappings.isDefinedAt(k) && !toIgnore.contains(k))

  def replaceWithMappingSpan(text: String, key: String, value: String, function: String): String = {
    val matcher = Pattern.compile(key + "\\b").matcher(text)
    matcher.replaceAll( s"""<span class="$CssClassName" $function data-baseform="$value">$key</span>""")
  }

  def parseTextForClassSel: String = parseText(ClassSelectionFunction)

  def parseTextForDiagDrawing: String = parseText(DiagramDrawingFunction)

  def parseText(function: String): String = {
    var newText = rawText

    for (simpleRep <- simpleReplacements)
      newText = replaceWithMappingSpan(newText, simpleRep, simpleRep, function)

    for ((k, v) <- mappings)
      newText = replaceWithMappingSpan(newText, k, v, function)

    newText
  }

}
