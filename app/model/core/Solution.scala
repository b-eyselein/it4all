package model.core

import java.nio.file.Path

import model.core.CoreConsts.FORM_VALUE
import play.api.data.Form
import play.api.data.Forms._

object Solution {

  val stringSolForm = Form(mapping(FORM_VALUE -> nonEmptyText)(StringSolution.apply)(StringSolution.unapply))

}

abstract class Solution

case class StringSolution(learnerSolution: String) extends Solution

case class PathSolution(path: Path) extends Solution