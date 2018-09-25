package model

import model.core.CoreConsts._
import play.api.data.Form
import play.api.data.Forms._

import scala.language.postfixOps
import scala.util.matching.Regex
import scala.util.{Failure, Try}

object SemanticVersionHelper {

  private val semanticVersionRegex: Regex = "(\\d+)\\.(\\d+)\\.(\\d+)".r

  val DEFAULT = SemanticVersion(0, 1, 0)

  def parseFromString(str: String): Option[SemanticVersion] = tryParseFromString(str) toOption


  val semanticVersionForm: Form[SemanticVersion] = Form(
    mapping(
      majorName -> number,
      minorName -> number,
      patchName -> number
    )(SemanticVersion.apply)(SemanticVersion.unapply)
  )


  //  object SemanticVersionFormatter extends Formatter[SemanticVersion] {
  //
  //    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], SemanticVersion] =
  //      Right(SemanticVersionHelper.DEFAULT) // TODO!
  //
  //    override def unbind(key: String, value: SemanticVersion): Map[String, String] = Map(CoreConsts.semanticVersionName -> value.asString)
  //
  //  }


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

  def asString: String = s"$major.$minor.$patch"

  def isCompatibleTo(that: SemanticVersion): Boolean = this.major == that.major && this.minor == that.minor

}