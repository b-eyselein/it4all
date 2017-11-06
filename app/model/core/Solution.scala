package model.core

import play.api.data.Form
import play.api.data.Forms._

object Solution {
  val stringSolForm = Form(mapping(StringConsts.FORM_VALUE -> nonEmptyText)(StringSolution.apply)(StringSolution.unapply))
}

abstract class Solution

case class StringSolution(learnerSolution: String) extends Solution