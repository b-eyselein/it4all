package model.matching

import model.points._

trait Matcher[T, M <: Match[T]] {

  protected def canMatch(t1: T, t2: T): Boolean

  protected def instantiateOnlyUserMatch(ua: T): M

  protected def instantiateOnlySampleMatch(sa: T): M

  protected def instantiateCompleteMatch(ua: T, sa: T): M

  private def findMatchInSecondCollection(firstHead: T, secondCollection: List[T]): (M, List[T]) = {

    @annotation.tailrec
    def go(firstHead: T, secondCollection: List[T], notMatched: List[T]): (M, List[T]) =
      secondCollection match {
        case Nil => (instantiateOnlyUserMatch(firstHead), notMatched)
        case secondHead :: secondTail =>
          if (canMatch(firstHead, secondHead)) {
            val m = instantiateCompleteMatch(firstHead, secondHead)

            (m, notMatched ++ secondTail)
          } else {
            go(firstHead, secondTail, notMatched :+ secondHead)
          }

      }

    go(firstHead, secondCollection, List.empty)
  }

  def doMatch(firstCollection: Seq[T], secondCollection: Seq[T]): MatchingResult[T, M] = {

    @annotation.tailrec
    def go(firstCollection: List[T], secondCollection: List[T], matches: List[M]): MatchingResult[T, M] =
      firstCollection match {
        case Nil =>
          val allMatches = matches ++ secondCollection.map(instantiateOnlySampleMatch)

          val points: Points    = addUp(allMatches.map(_.points))
          val maxPoints: Points = addUp(allMatches.map(_.maxPoints))

          MatchingResult(allMatches, points, maxPoints)

        case firstHead :: firstTail =>
          val (foundMatch, notMatchedInSecond) = findMatchInSecondCollection(firstHead, secondCollection)
          go(firstTail, notMatchedInSecond, matches :+ foundMatch)
      }

    go(firstCollection.toList, secondCollection.toList, matches = List.empty)
  }

}
