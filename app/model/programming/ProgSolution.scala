package model.programming

trait ProgSolutionType

case class TestdataSolution(completeCommitedTestData: Seq[CompleteCommitedTestData]) extends ProgSolutionType

case class ImplementationSolution(language: ProgLanguage, implementation: String) extends ProgSolutionType

case class UmlActivitySolution(implementation: String) extends ProgSolutionType