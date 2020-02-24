package model.tools.randomTools

import model.tools.{AToolMain, ToolConsts, ToolState}

abstract class RandomExerciseToolMain(consts: ToolConsts) extends AToolMain(consts)

object BoolConsts extends ToolConsts {

  override val toolName: String = "Boolesche Algebra"
  override val toolId: String   = "bool"

  override val toolState: ToolState = ToolState.LIVE

}

object BoolToolMain extends RandomExerciseToolMain(BoolConsts)

object NaryConsts extends ToolConsts {

  override val toolName: String = "Zahlensysteme"
  override val toolId: String   = "nary"

  override val toolState: ToolState = ToolState.LIVE

}

object NaryToolMain extends RandomExerciseToolMain(NaryConsts)
