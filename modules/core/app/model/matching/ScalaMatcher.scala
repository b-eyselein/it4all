package model.matching

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

abstract class ScalaMatcher[T, M <: ScalaMatch[T]](matchName: String, canMatch: (T, T) => Boolean, matchInstantiation: (Option[T], Option[T]) => M) {

  def doMatch(firstCollection: List[T], secondCollection: List[T]) = {
    val matches: ListBuffer[M] = ListBuffer.empty

    val firstList = ListBuffer.empty ++ firstCollection
    val secondList = ListBuffer.empty ++ secondCollection

    for (arg1 <- firstList) {

      var matched = false
      for (arg2 <- secondList if !matched) {
        matched = canMatch.apply(arg1, arg2)
        if (matched) {
          matches += matchInstantiation.apply(Some(arg1), Some(arg2))
          firstList -= arg1
          secondList -= arg2
        }
      }
    }

    val wrong = firstList.map(t => matchInstantiation.apply(Some(t), None))
    val missing = secondList.map(t => matchInstantiation.apply(None, Some(t)))

    new ScalaMatchingResult[T, M](matchName, (matches ++ wrong ++ missing).toList)
  }

}

class ScalaStringMatcher(matchName: String) extends ScalaMatcher[String, ScalaMatch[String]](matchName, _ == _, new ScalaGenericMatch[String](_, _))