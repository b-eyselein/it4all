package model

import model.grammar._

import scala.language.implicitConversions

trait TestValues {

  protected implicit def variable2BaseNormalizedFormSequence(v: Variable): BaseNormalizedFormSequence =
    BaseNormalizedFormSequence(Seq(v))

  protected implicit def terminal2BaseNormalizedFormSequence(t: Terminal): BaseNormalizedFormSequence =
    BaseNormalizedFormSequence(Seq(t))

  // Terminals

  protected val x: Terminal = Terminal("x")
  protected val y: Terminal = Terminal("y")
  protected val z: Terminal = Terminal("z")

  protected val termNull: Terminal = Terminal("0")
  protected val termOne: Terminal  = Terminal("1")

  // NonTerminals

  protected val A: Variable      = Variable("A")
  protected val B: Variable      = Variable("B")
  protected val C: Variable      = Variable("C")
  protected val D: Variable      = Variable("D")
  protected val E: Variable      = Variable("E")
  protected val F: Variable      = Variable("F")
  protected val G: Variable      = Variable("G")
  protected val N: Variable      = Variable("N")
  protected val S: Variable      = Variable("S")
  protected val sSlash: Variable = Variable("S'")

  //Helper methods

  protected def alternatives(values: BaseNormalizedFormSequence*): BaseNormalizedFormAlternative =
    BaseNormalizedFormAlternative(values)

}
