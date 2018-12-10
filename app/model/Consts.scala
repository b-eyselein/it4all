package model

trait Consts {

  val analysisResultName: String = "analysisResult"
  val answersName       : String = "answers"
  val attributesName    : String = "attributes"
  val authorName        : String = "author"
  val awaitedName       : String = "awaited"

  val contentName    : String = "content"
  val correctName    : String = "correct"
  val correctnessName: String = "correctness"

  val difficultyName: String = "difficulty"
  val durationName  : String = "duration"

  val errorName       : String = "error"
  val exercisesName   : String = "exercises"
  val exerciseIdName           = "exerciseId"
  val exerciseTypeName: String = "exerciseType"
  val explanationName          = "explanation"
  val explanationsName         = "explanations"

  val gottenName: String = "gotten"

  val learnerSolutionName = "learnerSolution"

  val idName        : String = "id"
  val inputTypeName : String = "inputType"
  val inputTypesName: String = "inputTypes"

  val keyName: String = "key"

  val languageName: String = "language"

  val majorName: String     = "major"
  val matchesName           = "matches"
  val matchNameName         = "matchName"
  val matchSingularNameName = "matchSingularName"
  val matchTypeName         = "matchType"
  val maxPointsName         = "maxPoints"
  val messageName           = "message"
  val minorName             = "minor"

  val nameName = "name"

  val patchName: String = "patch"
  val pointsName        = "points"
  val pwName            = "passwort"
  val pw2Name           = "passwort_wieder"

  val questionsName    = "questions"
  val questionTypeName = "questionType"

  val resultsName = "results"
  val roleName    = "role"

  val sampleName          = "sample"
  val sampleArgName       = "sampleArg"
  val sampleSolutionName  = "sampleSolution"
  val sampleSubDir        = "samples"
  val samplesName: String = "samples"
  val selfName            = "self"
  val semanticVersionName = "semanticVersion"
  val statusName          = "status"
  val stdStep             = 10
  val successTypeName     = "successType"
  val solutionName        = "solution"
  val solutionSavedName   = "solutionSaved"
  val solutionsSubDir     = "solutions"
  val successName         = "success"

  val tagJoinChar    = "#"
  val templateSubDir = "templates"
  val textName       = "text"
  val titleName      = "title"
  val typeName       = "type"

  val userArgName  = "userArg"
  val usernameName = "username"

  val valueName = "value"

}

trait CollectionConsts extends Consts {

  val collIdName     = "collId"
  val collSemVerName = "collSemVer"

  def arrayName(subName: String)(count: Option[Int]): String = s"$subName[${count.map(_.toString).getOrElse[String]("")}]"

}