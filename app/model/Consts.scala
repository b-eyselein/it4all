package model

trait Consts {

  val adminFolder = "admin"
  val answersName = "answers"
  val attrsName   = "attributes"
  val authorName  = "author"

  val correct = "correct"

  val errorName        = "error"
  val exercisesName    = "exercises"
  val exerciseTypeName = "exerciseType"

  val learnerSolutionName = "learnerSolution"

  val idName = "id"

  val keyName = "key"

  val maxPointsName = "maxPoints"

  val nameName = "name"

  val pwName  = "passwort"
  val pw2Name = "passwort_wieder"

  val RoleName = "role"

  val SAMPLE_SUB_DIRECTORY    = "samples"
  val stateName               = "status"
  val STEP                    = 10
  val SOLUTIONS_SUB_DIRECTORY = "solutions"

  val TagJoinChar            = "#"
  val TEMPLATE_SUB_DIRECTORY = "templates"
  val TEST_COUNT_NAME        = "testCount"
  val textName               = "text"
  val titleName              = "title"

  val VALUE_NAME = "value"
  val VARS_NAME  = "vars"

}

trait CollectionConsts extends Consts {

  val collIdName = "collId"

  def arrayName(subName: String)(count: Option[Int]): String = subName + "[" + count.getOrElse("") + "]"

}