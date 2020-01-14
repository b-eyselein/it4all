package model.core.matching

import model.points._


trait Matcher[T, AR <: AnalysisResult, M <: Match[T, AR]] {

  protected val matchName: String

  protected val matchSingularName: String


  protected def canMatch(t1: T, t2: T): Boolean

  protected def instantiatePartMatch(ua: Option[T], sa: Option[T]): M

  protected def instantiateCompleteMatch(ua: T, sa: T): M

  //  protected def pointsForMatch(m: M, maxPoints: Points): Points = (-1).points

  //  protected def maxPointsForMatch(m: M): Points = (-1).points


  private def findMatchInSecondCollection(firstHead: T, secondCollection: List[T]): (M, List[T]) = {

    @annotation.tailrec
    def go(firstHead: T, secondCollection: List[T], notMatched: List[T]): (M, List[T]) = secondCollection match {
      case Nil =>
        val m = instantiatePartMatch(Some(firstHead), None)

        (m, notMatched)

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


  def doMatch(firstCollection: Seq[T], secondCollection: Seq[T]): MatchingResult[T, AR, M] = {

    @annotation.tailrec
    def go(firstCollection: List[T], secondCollection: List[T], matches: List[M]): MatchingResult[T, AR, M] = firstCollection match {
      case Nil =>
        val allMatches = matches ++ secondCollection.map(s => instantiatePartMatch(None, Some(s)))

        val points   : Points = addUp(allMatches.map(_.points))
        val maxPoints: Points = addUp(allMatches.map(_.maxPoints))

        MatchingResult(matchName, matchSingularName, allMatches, points, maxPoints)

      case firstHead :: firstTail =>
        val (foundMatch, notMatchedInSecond) = findMatchInSecondCollection(firstHead, secondCollection)
        go(firstTail, notMatchedInSecond, matches :+ foundMatch)
    }

    go(firstCollection.toList, secondCollection.toList, matches = List.empty)
  }


}
