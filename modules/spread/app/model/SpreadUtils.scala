package model

import java.util.regex.{Matcher, Pattern}

import scala.util.matching.Regex

object SpreadUtils {

  // Patterns

  private val cellFormulaPattern: Pattern = Pattern.compile("([A-Z]+)[(]")

  private val cellOperatorsPattern: Pattern = Pattern.compile("([+|-|*|/])")

  private val cellRangesPattern: Pattern = Pattern.compile("([A-Z]+[0-9]+)")

  private val excelChartTitlePattern: Regex = """<a:t>(.*?)</a:t>""".r

  private val excelCFFormulaPattern: Regex = """<main:formula>(.*?)</main:formula>""".r

  private def excelChartRangesPattern(name: String): Pattern = Pattern.compile("<c:f>" + name + "!(.*?)</c:f>")

  // Helpers for matches

  private def firstMatch(matcher: Matcher): String = if (matcher.find) matcher.group(1).replace("$", "") else ""

  private def allMatches(matcher: Matcher, mapping: (String => String) = _.toString): Set[String] = {
    var matches: Set[String] = Set.empty
    while (matcher.find) {
      matches += mapping(matcher.group(1))
    }
    matches
  }


  protected def renderDiffAsStr(name: String, diff: Seq[String]): String = diff match {
    case Nil => ""
    case d => s"Die $name [${d.mkString(", ")}] fehlen."
  }

  def getDiffOfTwoFormulas(string1: String, string2: String): String = {
    val operDiff = getCellOperatorsDiff(string1, string2)
    val rangeDiff = getCellRangesDiff(string1, string2)
    val formulaDiff = getCellFormulasDiff(string1, string2)

    (renderDiffAsStr("Operatoren", operDiff) + " " +
      renderDiffAsStr("Bereiche", rangeDiff) + " " +
      renderDiffAsStr("Funktionen", formulaDiff)).trim
  }

  def getSheetCFDiff(hs1: Set[String], hs2: Set[String]): String = {
    val difference = hs1 -- hs2
    if (difference.isEmpty) "" else difference.toString
  }

  private[model] def diff(strs1: Set[String], strs2: Set[String]): Seq[String] = (strs1 -- strs2).toSeq

  private[model] def getCellFormulasList(str: String): Set[String] = allMatches(cellFormulaPattern.matcher(str))

  private[model] def getCellOperatorsList(str: String): Set[String] = allMatches(cellOperatorsPattern.matcher(str))

  private[model] def getCellRangesList(str: String): Set[String] = allMatches(cellRangesPattern.matcher(str.replace("$", "")))

  private[model] def getExcelChartRangesList(name: String, str: String): Set[String] = allMatches(excelChartRangesPattern(name).matcher(str), _.replace("$", ""))

  def getExcelChartTitle(str: String): Option[String] = excelChartTitlePattern.findFirstIn(str)

  def getExcelCFFormulaList(str: String): Option[String] = excelCFFormulaPattern.findFirstIn(str)

  def getCellFormulasDiff(str1: String, str2: String): Seq[String] = diff(getCellFormulasList(str1), getCellFormulasList(str2))

  def getCellOperatorsDiff(str1: String, str2: String): Seq[String] = diff(getCellOperatorsList(str1), getCellOperatorsList(str2))

  def getCellRangesDiff(str1: String, str2: String): Seq[String] = diff(getCellRangesList(str1), getCellRangesList(str2))

  def getExcelChartRangesDiff(name1: String, str1: String, name2: String, str2: String): Seq[String] = diff(getExcelChartRangesList(name1, str1), getExcelChartRangesList(name2, str2))

}
