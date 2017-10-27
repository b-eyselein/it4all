package model.spread

import scala.util.matching.Regex

object SpreadUtils {

  // Strings

  private[model] val noFormulaRequired = "Keine Formel notwendig."

  private[model] val formulaCorrect = "Formel richtig."

  private[model] val formulaMissing = "Formel notwendig."

  // Regexes

  private val cellFormulaPattern: Regex = """([A-Z]+)[(]""".r

  private val cellOperatorsPattern: Regex = """([+\-*/])""".r

  private val cellRangesPattern: Regex = """([A-Z]+[0-9]+)""".r

  private val excelChartTitlePattern: Regex = """<a:t>(.*?)</a:t>""".r

  private val excelCFFormulaPattern: Regex = """<main:formula>(.*?)</main:formula>""".r

  private def excelChartRangesPattern(name: String): Regex = s"""<c:f>$name!(.*?)</c:f>""".r

  // Other helper defs

  private[model] def renderDiffAsStr(name: String, diff: Seq[String]): String = diff match {
    case Nil => ""
    case d => s"Die $name [${d.mkString(", ")}] fehlen."
  }

  private[model] def getDiffOfTwoFormulas(string1: String, string2: String): String = {
    val operDiff = getCellOperatorsDiff(string1, string2)
    val rangeDiff = getCellRangesDiff(string1, string2)
    val formulaDiff = getCellFormulasDiff(string1, string2)

    (renderDiffAsStr("Operatoren", operDiff) + " " +
      renderDiffAsStr("Bereiche", rangeDiff) + " " +
      renderDiffAsStr("Funktionen", formulaDiff)).trim
  }

  private[model] def getSheetCFDiff(hs1: Set[String], hs2: Set[String]): String = (hs1 -- hs2).toList match {
    case Nil => ""
    case difference => difference.toString
  }

  private[model] def getCellFormulas(str: String): Set[String] =
    cellFormulaPattern.findAllIn(str).matchData.map(_.group(1)).toSet

  private[model] def getCellOperators(str: String): Set[String] = cellOperatorsPattern.findAllIn(str).toSet

  private[model] def getCellRanges(str: String): Set[String] = cellRangesPattern.findAllIn(str.replace("$", "")).toSet

  private[model] def getExcelChartRanges(name: String, str: String): Set[String] =
    excelChartRangesPattern(name).findAllIn(str).map(_.replace("$", "")).toSet

  private[model] def getExcelChartTitle(str: String): Option[String] = excelChartTitlePattern.findFirstMatchIn(str).map(_.group(1))

  private[model] def getExcelCFFormula(str: String): Option[String] = excelCFFormulaPattern.findFirstIn(str)

  private[model] def diff(strs1: Set[String], strs2: Set[String]): Seq[String] = (strs1 -- strs2).toSeq

  private[model] def getCellFormulasDiff(str1: String, str2: String): Seq[String] = diff(getCellFormulas(str1), getCellFormulas(str2))

  private[model] def getCellOperatorsDiff(str1: String, str2: String): Seq[String] = diff(getCellOperators(str1), getCellOperators(str2))

  private[model] def getCellRangesDiff(str1: String, str2: String): Seq[String] = diff(getCellRanges(str1), getCellRanges(str2))

  private[model] def getExcelChartRangesDiff(name1: String, str1: String, name2: String, str2: String): Seq[String] =
    diff(getExcelChartRanges(name1, str1), getExcelChartRanges(name2, str2))

}
