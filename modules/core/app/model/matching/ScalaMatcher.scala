package model.matching

abstract class ScalaMatcher[T, M <: Match[T]](canMatch: (T, T) => Boolean) {

  def doMatch(userCol: List[T], sampleCol: List[T]): MatchingResult[T, M] = {
     val matches = for {
      userArg <- userCol
      sampleArg <- sampleCol
      if canMatch.apply(userArg, sampleArg)
    } yield instantiateMatch(userArg, sampleArg)

    null
  }

  def instantiateMatch(userArg: T, sampleArg: T): Match[T]
  
}

object StringMatcher extends ScalaMatcher[String, Match[String]]((s1, s2) => s1 == s2) {
  override def instantiateMatch(ua: String, sa: String) = new GenericMatch(ua, sa)
}