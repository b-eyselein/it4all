package model

trait Consts {

  val adminFolder    = "admin"
  val answersName    = "answers"
  val attributesName = "attributes"
  val authorName     = "author"
  val awaitedName    = "awaited"

  val contentName     = "content"
  val correct         = "correct"
  val correctnessName = "correctness"

  val errorName        = "error"
  val exercisesName    = "exercises"
  val exerciseIdName   = "exerciseId"
  val exerciseTypeName = "exerciseType"
  val explanationName  = "explanation"
  val explanationsName = "explanations"

  val gottenName = "gotten"

  val learnerSolutionName = "learnerSolution"

  val idName = "id"

  val keyName = "key"

  val matchesName   = "matches"
  val maxPointsName = "maxPoints"

  val nameName = "name"

  val pointsName = "points"
  val pwName     = "passwort"
  val pw2Name    = "passwort_wieder"

  val questionsName    = "questions"
  val questionTypeName = "questionType"

  val roleName = "role"

  val sampleArgName     = "sampleArg"
  val sampleSubDir      = "samples"
  val stateName         = "status"
  val stdStep           = 10
  val successTypeName   = "successType"
  val solutionName      = "solution"
  val solutionSavedName = "solutionSaved"
  val solutionsSubDir   = "solutions"
  val successName       = "success"

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

  val collIdName = "collId"

  def arrayName(subName: String)(count: Option[Int]): String = subName + "[" + count.getOrElse("") + "]"

}