package model

import better.files.File

trait Consts {

  val analysisResultName: String = "analysisResult"

  val resourcesBasePath: File = File.currentWorkingDirectory / "conf" / "resources"

  val difficultyName: String = "difficulty"
  val durationName  : String = "duration"

  val idName: String = "id"

  val matchesName          : String = "matches"
  val matchNameName        : String = "matchName"
  val matchSingularNameName: String = "matchSingularName"
  val matchTypeName        : String = "matchType"
  val maxPointsName        : String = "maxPoints"
  val messageName          : String = "message"

  val nameName = "name"

  val pointsName: String = "points"
  val pwName    : String = "passwort"


  val relativePathName: String = "relativePath"
  val resultsName     : String = "results"
  val roleName        : String = "role"

  val sampleArgName    : String = "sampleArg"
  val solutionName     : String = "solution"
  val solutionSavedName: String = "solutionSaved"
  val solutionsSubDir  : String = "solutions"
  val successName      : String = "success"

  val textName : String = "text"
  val titleName: String = "title"
  val typeName : String = "type"

  val userArgName: String = "userArg"

}

