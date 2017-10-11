package model

import java.util.regex.Pattern

import scala.collection.JavaConverters.{asScalaBufferConverter, mapAsScalaMapConverter, seqAsJavaListConverter}
import scala.util.matching.Regex

class UmlExTextParser(rawText: String, theMappings: java.util.Map[String, String], theToIgnore: java.util.List[String]) {

  val mappings: scala.collection.mutable.Map[String, String] = theMappings.asScala
  val toIgnore: List[String] = theToIgnore.asScala.toList

  val capitalizedWords: Set[String] = UmlExTextParser.CapWords.findAllIn(rawText).toSet
  val simpleReplacements: Set[String] = capitalizedWords.filter(k => !mappings.isDefinedAt(k) && !toIgnore.contains(k))

  def getCapitalizedWords: java.util.List[String] = capitalizedWords.toList.asJava

  def replaceWithMappingSpan(text: String, key: String, value: String, function: String): String = {
    val matcher = Pattern.compile(s"""$key\b""").matcher(text)
    matcher.replaceAll(
      s"""<span class="${UmlExTextParser.CssClassName}" $function data-baseform="$value">$key</span>"""
    )
  }

  def parseTextForClassSel: String = parseText(UmlExTextParser.ClassSelectionFunction)

  def parseTextForDiagDrawing: String = parseText(UmlExTextParser.DiagramDrawingFunction)

  def parseText(function: String): String = {
    var newText = rawText
    for (simpleRep <- simpleReplacements)
      newText = replaceWithMappingSpan(newText, simpleRep, simpleRep, function)
    for ((k, v) <- mappings)
      newText = replaceWithMappingSpan(newText, k, v, function)
    newText
  }

}

object UmlExTextParser {
  val CapWords: Regex = """[A-Z][a-zäöüß]*""".r

  val CssClassName = "non-marked"

  val ClassSelectionFunction = """onclick="select(this)""""
  val DiagramDrawingFunction = """draggable="true" ondragstart="drag(event)""""

}
