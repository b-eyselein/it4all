package model

import java.util.regex.Pattern

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.collection.JavaConverters.seqAsJavaListConverter

class UmlExTextParser(rawText: String, theMappings: java.util.Map[String, String], theToIgnore: java.util.List[String]) {

  val mappings = theMappings.asScala
  val toIgnore = theToIgnore.asScala.toList

  val capitalizedWords = UmlExTextParser.CapWords.findAllIn(rawText).toSet
  val simpleReplacements = capitalizedWords.filter(k => !mappings.isDefinedAt(k) && !toIgnore.contains(k))

  def getCapitalizedWords = capitalizedWords.toList.asJava

  def replaceWithMappingSpan(text: String, key: String, value: String, function: String) = {
    val matcher = Pattern.compile(s"""$key\b""").matcher(text)
    matcher.replaceAll(
      s"""<span class="${UmlExTextParser.CssClassName}" $function data-baseform="$value">$key</span>""")
  }

  def parseTextForClassSel = parseText(UmlExTextParser.ClassSelectionFunction)

  def parseTextForDiagDrawing = parseText(UmlExTextParser.DiagramDrawingFunction)

  def parseText(function: String) = {
    var newText = rawText
    for (simpleRep <- simpleReplacements)
      newText = replaceWithMappingSpan(newText, simpleRep, simpleRep, function)
    for ((k, v) <- mappings)
      newText = replaceWithMappingSpan(newText, k, v, function)
    newText
  }

}

object UmlExTextParser {
  val CapWords = """[A-Z][a-zäöüß]*""".r

  val CssClassName = "non-marked"

  val ClassSelectionFunction = """onclick="select(this)""""
  val DiagramDrawingFunction = """draggable="true" ondragstart="drag(event)""""

}
