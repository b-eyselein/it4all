package model

import scala.language.postfixOps
import scala.util.{Failure, Try}
import scala.util.matching.Regex

object SemanticVersionHelper {

  private val semanticVersionRegex: Regex = "(\\d+)\\.(\\d+)\\.(\\d+)".r

  val DEFAULT = SemanticVersion(0, 1, 0)

  def parseFromString(str: String): Option[SemanticVersion] = tryParseFromString(str) toOption


  def tryParseFromString(str: String): Try[SemanticVersion] = str match {
    case semanticVersionRegex(maj, min, pat) => for {
      major <- Try(maj.toInt)
      minor <- Try(min.toInt)
      patch <- Try(pat.toInt)
    } yield SemanticVersion(major, minor, patch)

    case _ => Failure(new Exception(s"Could not parse String '$str' as Semantic Version!"))
  }

}


final case class SemanticVersion(major: Int, minor: Int, patch: Int) {

  def asString: String = major + "." + minor + "." + patch

  def isCompatibleTo(that: SemanticVersion): Boolean = this.major == that.major && this.minor == that.minor

}