package model

trait Consts {

  val analysisResultName: String = "analysisResult"
  val attributesName    : String = "attributes"
  val authorName        : String = "author"
  val awaitedName       : String = "awaited"

  val contentName: String = "content"
  val correctName: String = "correct"

  val descriptionName: String = "description"
  val difficultyName: String = "difficulty"
  val durationName  : String = "duration"

  val errorName       : String = "error"
  val exerciseTypeName: String = "exerciseType"
  val explanationsName         = "explanations"

  val filesName: String = "files"

  val gottenName: String = "gotten"

  val idName        : String = "id"
  val inputTypeName : String = "inputType"
  val inputTypesName: String = "inputTypes"

  val keyName: String = "key"

  val languageName: String = "language"

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
  val pathName  : String = "path"
  val pointsName: String = "points"
  val pwName    : String = "passwort"
  val pw2Name   : String = "passwort_wieder"

  val questionsName: String = "questions"

  val resultsName: String = "results"
  val roleName   : String = "role"

  val sampleName         : String = "sample"
  val sampleArgName      : String = "sampleArg"
  val sampleSolutionName : String = "sampleSolution"
  val samplesName        : String = "samples"
  val semanticVersionName: String = "semanticVersion"
  val shortNameName      : String = "shortName"
  val statusName         : String = "status"
  val stdStep            : Int    = 10
  val successTypeName    : String = "successType"
  val solutionName       : String = "solution"
  val solutionSavedName  : String = "solutionSaved"
  val solutionsSubDir    : String = "solutions"
  val successName        : String = "success"

  val tagJoinChar       = "#"
  val textName : String = "text"
  val titleName: String = "title"
  val typeName : String = "type"

  val userArgName : String = "userArg"
  val usernameName: String = "username"

  val valueName: String = "value"

}

trait CollectionConsts extends Consts {

  val collIdName     = "collId"
  val collSemVerName = "collSemVer"

  def arrayName(subName: String)(count: Option[Int]): String = s"$subName[${count.map(_.toString).getOrElse[String]("")}]"

}
