package model.core.matching

trait Matcher[M <: Match] {

  type T

  protected val matchName: String

  protected val matchSingularName: String

  protected def canMatch(t1: T, t2: T): Boolean

  protected def matchInstantiation(ua: Option[T], sa: Option[T]): M

  def doMatch(firstCollection: Seq[T], secondCollection: Seq[T]): MatchingResult[M] = {

    def findMatchInSecondCollection(firstHead: T, secondCollection: List[T]): (M, List[T]) = {

      @annotation.tailrec
      def go(firstHead: T, secondCollection: List[T], notMatched: List[T]): (M, List[T]) = secondCollection match {
        case Nil                      => (matchInstantiation(Some(firstHead), None), notMatched)
        case secondHead :: secondTail =>
          if (canMatch(firstHead, secondHead)) {
            (matchInstantiation(Some(firstHead), Some(secondHead)), notMatched ++ secondTail)
          } else {
            go(firstHead, secondTail, notMatched :+ secondHead)
          }
      }

      go(firstHead, secondCollection, List.empty)
    }

    @annotation.tailrec
    def go(firstCollection: List[T], secondCollection: List[T], matches: List[M]): MatchingResult[M] = firstCollection match {
      case Nil =>
        val missing = secondCollection.map(s => matchInstantiation(None, Some(s)))
        MatchingResult[M](matchName, matchSingularName, matches ++ missing)

      case firstHead :: firstTail =>
        val (foundMatch, notMatchedInSecond) = findMatchInSecondCollection(firstHead, secondCollection)
        go(firstTail, notMatchedInSecond, matches :+ foundMatch)
    }

    go(firstCollection.toList, secondCollection.toList, matches = List.empty)
  }


}
