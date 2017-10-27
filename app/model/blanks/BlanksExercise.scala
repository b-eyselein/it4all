package model.blanks

import model.Enums.ExerciseState
import model.Exercise
import model.core.result.SuccessType
import play.twirl.api.Html

case class BlankObject(id: Int, preText: String, length: Int, postText: String, solution: String) {

  def render: String =
    s"""$preText
       |<div class="form-group">
       |  <input name="inp$id" id="inp$id"  class="form-control" type="text" size="$length" maxlength="$length">
       |</div>"
       |$postText""".stripMargin

}

class BlanksExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState) extends Exercise(i, ti, a, te, s) {

  val objects: List[BlankObject] = List(
    BlankObject(1,
      """&lt?xml version="1.0" encoding="UTF-8"?&gt"
        |&lt!DOCTYPE root SYSTEM "doctype.dtd"&gt
        |&lt""".stripMargin, 4, "&gt", "root"),
    BlankObject(2, "&lt", 5, "&gt", "/root")
  )

  def correct(inputs: List[String]): List[SuccessType] =
    for {(inp, obj) <- inputs zip objects}
      yield if (inp == obj.solution) SuccessType.COMPLETE else SuccessType.NONE


  def render = new Html(objects.map(_.render).mkString("\n"))

}
