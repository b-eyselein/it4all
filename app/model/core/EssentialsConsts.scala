package model.core

import model.Consts

import scala.util.Random

trait EssentialsConsts extends Consts {

  val generator = new Random

  val Correct = "correct"
  val ERROR   = "error"

}