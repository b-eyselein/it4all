package model

import scala.util.matching.Regex
import scala.util.{Failure, Try}

object SemanticVersionHelper {

  private val semanticVersionRegex: Regex = "(\\d+)\\.(\\d+)\\.(\\d+)".r

  val DEFAULT: SemanticVersion = SemanticVersion(0, 1, 0)

  def parseFromString(str: String): Option[SemanticVersion] = tryParseFromString(str).toOption

  private def tryParseFromString(str: String): Try[SemanticVersion] = str match {
    case semanticVersionRegex(maj, min, pat) => for {
      major <- Try(maj.toInt)
      minor <- Try(min.toInt)
      patch <- Try(pat.toInt)
    } yield SemanticVersion(major, minor, patch)

    case _ => Failure(new Exception(s"Could not parse String '${str.toString}' as Semantic Version!"))
  }

}


final case class SemanticVersion(major: Int, minor: Int, patch: Int) {

  def asString: String = s"$major.$minor.$patch"

}
