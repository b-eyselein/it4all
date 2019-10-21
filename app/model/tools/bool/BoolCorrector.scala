package model.tools.bool

import model.tools.bool.BoolConsts.{LerVariable, SolVariable}
import BoolNodeParser.parseBoolFormula
import model.core.result.SuccessType

import scala.util.{Failure, Success}

object BoolCorrector {

  def correctPart(exPart: BoolExPart, boolSolution: BoolSolution): BooleanQuestionResult = exPart match {
    case BoolExParts.TableFillout    => correctFilloutQuestion(boolSolution)
    case BoolExParts.FormulaCreation => correctCreateQuestion(boolSolution)
  }

  private def correctFilloutQuestion(boolSolution: BoolSolution): BooleanQuestionResult = parseBoolFormula(boolSolution.formula) match {
    case Failure(_)       => FilloutQuestionResult(SuccessType.ERROR, Seq.empty, Some("There has been an internal error!"))
    case Success(formula) =>
      val allAssignments = boolSolution.rows.map(as => as + (SolVariable -> formula(as)))

      val successType = if (allAssignments.forall(as => as.isSet(LerVariable) && as(LerVariable) == as(SolVariable))) {
        SuccessType.COMPLETE
      } else {
        SuccessType.PARTIALLY
      }

      FilloutQuestionResult(successType, allAssignments)
  }

  private def correctCreateQuestion(boolSolution: BoolSolution): BooleanQuestionResult = parseBoolFormula(boolSolution.formula) match {
    case Failure(error)   => CreationQuestionError(boolSolution.formula, error.getMessage)
    case Success(formula) => CreationQuestionSuccess(formula, CreationQuestion(boolSolution.rows))
  }

}
