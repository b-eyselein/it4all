package model

import better.files.File

trait Consts {

  val analysisResultName: String = "analysisResult"
  val authorName        : String = "author"

  val resourcesBasePath: File = File.currentWorkingDirectory / "conf" / "resources"

  val contentName: String = "content"

  val difficultyName: String = "difficulty"
  val durationName  : String = "duration"

  val filenameName: String = "filename"

  val idName: String = "id"

  val majorName            : String = "major"
  val matchesName          : String = "matches"
  val matchNameName        : String = "matchName"
  val matchSingularNameName: String = "matchSingularName"
  val matchTypeName        : String = "matchType"
  val maxPointsName        : String = "maxPoints"
  val messageName          : String = "message"
  val minorName            : String = "minor"

  val nameName = "name"

  val partName  : String = "part"
  val patchName : String = "patch"
  val pointsName: String = "points"
  val pwName    : String = "passwort"

  val questionsName: String = "questions"

  val relativePathName: String = "relativePath"
  val resultsName     : String = "results"
  val roleName        : String = "role"

  val sampleName       : String = "sample"
  val sampleArgName    : String = "sampleArg"
  val shortNameName    : String = "shortName"
  val statusName       : String = "status"
  val solutionName     : String = "solution"
  val solutionSavedName: String = "solutionSaved"
  val solutionsSubDir  : String = "solutions"
  val successName      : String = "success"

  val textName : String = "text"
  val titleName: String = "title"
  val typeName : String = "type"

  val userArgName : String = "userArg"
  val usernameName: String = "username"

}

trait CollectionConsts extends Consts {

  val collIdName     = "collId"
  val collSemVerName = "collSemVer"

  def arrayName(subName: String)(count: Option[Int]): String = s"$subName[${count.map(_.toString).getOrElse[String]("")}]"

}
