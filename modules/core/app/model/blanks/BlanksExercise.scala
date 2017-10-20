package model.blanks


import model.exercise.Exercise
import model.result.SuccessType
import play.twirl.api.Html

class BlanksExercise extends Exercise {

  val objects: List[BlankObject] = List(
    new BlankObject(1,
      """&lt?xml version="1.0" encoding="UTF-8"?&gt"
        |&lt!DOCTYPE root SYSTEM "doctype.dtd"&gt
        |&lt""".stripMargin, 4, "&gt", "root"),
    new BlankObject(2, "&lt", 5, "&gt", "/root")
  )

  def correct(inputs: List[String]): List[SuccessType] =
    for {(inp, obj) <- inputs zip objects}
      yield if (inp == obj.getSolution) SuccessType.COMPLETE else SuccessType.NONE


  def render = new Html(objects.map(_.render).mkString("\n"))

}
