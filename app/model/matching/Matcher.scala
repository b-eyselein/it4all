package model.matching

import model.points._

trait Matcher[T, M <: Match[T]] {

  protected def canMatch(t1: T, t2: T): Boolean = t1 == t2

  protected def instantiateMatch(ua: T, sa: T): M

  private def findMatchInSecondCollection(firstHead: T, secondCollection: List[T]): (Either[T, M], List[T]) = {

    @annotation.tailrec
    def go(firstHead: T, secondColl: List[T], notMatched: List[T]): (Either[T, M], List[T]) = secondColl match {
      case Nil => (Left(firstHead), notMatched)
      case secondHead :: secondTail =>
        if (canMatch(firstHead, secondHead)) {
          val m = instantiateMatch(firstHead, secondHead)

          (Right(m), notMatched ++ secondTail)
        } else {
          go(firstHead, secondTail, notMatched :+ secondHead)
        }

    }

    go(firstHead, secondCollection, List.empty)
  }

  def doMatch(userCollection: Seq[T], secondCollection: Seq[T]): MatchingResult[T, M] = {

    @annotation.tailrec
    def go(
      firstColl: List[T],
      secondColl: List[T],
      matches: Seq[M],
      notMatchedInFirst: Seq[T]
    ): MatchingResult[T, M] = firstColl match {
      case Nil =>
        // FIXME: points!
        // val allMatches = matches ++ secondColl.map(instantiateOnlySampleMatch)

        val points: Points    = addUp(matches.map(_.points))
        val maxPoints: Points = (-1).points // addUp(allMatches.map(_.maxPoints))

        MatchingResult(matches, notMatchedInFirst, secondColl, points, maxPoints)

      case firstHead :: firstTail =>
        findMatchInSecondCollection(firstHead, secondColl) match {
          case (Left(t), notMatchedInSecond)  => go(firstTail, notMatchedInSecond, matches, notMatchedInFirst :+ t)
          case (Right(m), notMatchedInSecond) => go(firstTail, notMatchedInSecond, matches :+ m, notMatchedInFirst)
        }
    }

    go(userCollection.toList, secondCollection.toList, matches = Seq.empty, Seq.empty)
  }

}
