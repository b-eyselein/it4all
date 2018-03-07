package model.core

import model.core.CoreConsts.learnerSolutionName
import play.api.data.Form
import play.api.data.Forms._

object SolutionFormHelper {

  val stringSolForm = Form(mapping(learnerSolutionName -> nonEmptyText)(StringSolutionFormHelper.apply)(StringSolutionFormHelper.unapply))

}

abstract class SolutionFormHelper

case class StringSolutionFormHelper(learnerSolution: String) extends SolutionFormHelper