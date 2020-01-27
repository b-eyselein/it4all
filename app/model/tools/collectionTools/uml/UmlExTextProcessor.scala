package model.tools.collectionTools.uml

import java.util.regex.Pattern

import scala.util.matching.Regex

object UmlExTextProcessor {

  private val capWordsRegex: Regex  = """[A-Z][a-zäöüß]*""".r
  private val cssClassName : String = "text-muted"

  private def replaceWithMappingSpan(text: String, key: String, value: String): String =
    Pattern.compile(key + "\\b")
      .matcher(text)
      .replaceAll(s"""<span class="$cssClassName" data-baseform="$value">$key</span>""")

  def parseText(rawText: String, mappings: Map[String, String], toIgnore: Seq[String]): String = {

    val simpleReplacements: Set[(String, String)] = capWordsRegex
      .findAllIn(rawText)
      .toSet
      .filter(k => !mappings.isDefinedAt(k) && !toIgnore.contains(k))
      .map(str => (str, str))


    @annotation.tailrec
    def go(currentText: String, remainingReplacers: List[(String, String)]): String = remainingReplacers match {
      case Nil                          => currentText
      case (headKey, headValue) :: tail => go(replaceWithMappingSpan(currentText, headKey, headValue), tail)
    }

    go(rawText, (simpleReplacements ++ mappings.toSeq).toList)

  }

}
