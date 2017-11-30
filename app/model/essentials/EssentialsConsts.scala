package model.essentials

import model.Consts

import scala.util.Random

object EssentialsConsts extends Consts {

  val generator = new Random

  // Nary

  val AssignmentsName = "assignments"

  val BaseName  = "base"
  val BinaryAbs = "binaryAbs"

  val FirstSummand = "summand1"
  val FormulaName  = "formula"

  val InvertedAbs = "invertedAbs"

  val LearnerSol = "solution"

  val RandomName = "RANDOM"

  val SecondSummand   = "summand2"
  val StartingNumBase = "startingNB"

  val TargetNumBase = "targetNB"

  val VariablesName = "variables"


  // Boolean

  val SolVariable: Variable = Variable('z')
  val LerVariable: Variable = Variable('y')

}
