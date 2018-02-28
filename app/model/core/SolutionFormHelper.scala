package model.core

import model.core.CoreConsts.FORM_VALUE
import play.api.data.Form
import play.api.data.Forms._

object SolutionFormHelper {

  val stringSolForm = Form(mapping(FORM_VALUE -> nonEmptyText)(StringSolutionFormHelper.apply)(StringSolutionFormHelper.unapply))

}

abstract class SolutionFormHelper

case class StringSolutionFormHelper(learnerSolution: String) extends SolutionFormHelper