package model

import scala.util.Try

object SemanticVersionHelper {

  //  private val semVerRegex = """(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)"""

  def fromString(str: String): SemanticVersion = {
    // FIXME: test!
    val parts = str.split("\\.")

    val major = if (parts.nonEmpty) parts(0) else 0
    val minor = if (parts.length >= 2) parts(1) else 0
    val patch = if (parts.length > 3) parts(2) else 0

    SemanticVersion(parts(0).toInt, parts(1).toInt, parts(2).toInt)
  }

  def tryFromString(str: String): Try[SemanticVersion] = Try {

    val parts = str.split("\\.")

//    println(str + " :: " + parts.mkString(", "))

    SemanticVersion(parts(0).toInt, parts(1).toInt, parts(2).toInt)
  }

}


case class SemanticVersion(major: Int, minor: Int, patch: Int) {

  def asString: String = major + "." + minor + "." + patch

}