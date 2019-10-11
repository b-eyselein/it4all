package model

import model.core.CoreConsts._
import net.jcazevedo.moultingyaml.{YamlObject, YamlString, YamlValue}
import play.api.data.Form
import play.api.data.Forms._
import scala.util.matching.Regex
import scala.util.{Failure, Try}

object SemanticVersionHelper {

  private val semanticVersionRegex: Regex = "(\\d+)\\.(\\d+)\\.(\\d+)".r

  val DEFAULT: SemanticVersion = SemanticVersion(0, 1, 0)

  def parseFromString(str: String): Option[SemanticVersion] = tryParseFromString(str).toOption


  val semanticVersionForm: Form[SemanticVersion] = Form(
    mapping(
      majorName -> number,
      minorName -> number,
      patchName -> number
    )(SemanticVersion.apply)(SemanticVersion.unapply)
  )

  def semanticVersionYamlField(yamlValue: YamlValue): Try[SemanticVersion] = yamlValue match {
    case YamlString(str)     => tryParseFromString(str)
    case yamlObj: YamlObject =>
      import MyYamlProtocol._

      for {
        major <- yamlObj.intField(majorName)
        minor <- yamlObj.intField(minorName)
        patch <- yamlObj.intField(patchName)
      } yield SemanticVersion(major, minor, patch)
    case other               => Failure(new Exception(s"Could not parse '${other.toString}' as Semantic Version!"))
  }

  def tryParseFromString(str: String): Try[SemanticVersion] = str match {
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
