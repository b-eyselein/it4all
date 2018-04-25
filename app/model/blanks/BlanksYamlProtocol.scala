package model.blanks

import model.ExerciseState
import model.MyYamlProtocol
import model.MyYamlProtocol._
import net.jcazevedo.moultingyaml.{YamlObject, YamlString, YamlValue}

import scala.collection.mutable
import scala.language.postfixOps
import scala.util.Try
import scala.util.matching.Regex

object BlanksYamlProtocol extends MyYamlProtocol {

  val parseRegex: Regex = """<blank.*?/>""".r

  val solRegex: Regex = "sol=\"(.*?)\"".r

  def parseBlanksText(exerciseId: Int, blanksText: String): (String, Seq[BlanksAnswer]) = {

    var newText = blanksText
    var i = 1
    var solutions: mutable.Map[Int, String] = mutable.Map.empty

    val iterator: Iterator[Regex.Match] = parseRegex.findAllMatchIn(blanksText)
    while (iterator.hasNext) {
      val current = iterator.next

      val sol = solRegex.findFirstMatchIn(current.toString) map (_.group(1)) getOrElse "FEHLER!"

      newText = newText.replace(current.toString,
        s"""<div class="form-group">
           |  <input type="text" class="form-control" id="$i">
           |  <span id="span_$i"></span>
           |</div>""".stripMargin)

      solutions += (i -> sol)

      i += 1
    }
    (newText, solutions map (samp => BlanksAnswer(samp._1, exerciseId, samp._2)) toSeq)
  }

  implicit object BlanksYamlFormat extends HasBaseValuesYamlFormat[BlanksCompleteExercise] {

    override protected def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[BlanksCompleteExercise] = for {
      rawBlanksText <- yamlObject.stringField("blankstext")
      (parsedText, samples) = parseBlanksText(baseValues._1, rawBlanksText)
    } yield BlanksCompleteExercise(new BlanksExercise(baseValues, rawBlanksText, parsedText), samples)

    override protected def writeRest(completeEx: BlanksCompleteExercise): Map[YamlValue, YamlValue] = Map(
      YamlString("blankstext") -> YamlString(completeEx.ex.rawBlanksText)
    )

  }

}
