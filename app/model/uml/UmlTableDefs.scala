package model.uml

import controllers.exes.idPartExes.UmlToolObject
import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.uml.UmlConsts._
import model.uml.UmlEnums._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Call
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

case class UmlCompleteEx(ex: UmlExercise, mappings: Seq[UmlMapping], solution: UmlSolution) extends PartsCompleteEx[UmlExercise, UmlExPart] {

  override def preview: Html = views.html.uml.umlPreview(this)

  def getClassesForDiagDrawingHelp: String = {
    val classes = solution.classes
    val sqrt = Math.round(Math.sqrt(classes.size))

    classes.zipWithIndex.map { case (clazz, index) =>
      s"""{
         |  name: "${clazz.clazz.className}",
         |  classType: "${clazz.clazz.classType.toString.toUpperCase}", attributes: [], methods: [],
         |  position: { x: ${(index / sqrt) * GapHorizontal + OFFSET}, y: ${(index % sqrt) * GapVertival + OFFSET} }
         |}""".stripMargin
    } mkString ","
  }

  override def exerciseRoutes: Map[Call, String] = UmlToolObject.exerciseRoutes(this)

  override def hasPart(partType: UmlExPart): Boolean = partType match {
    case (ClassSelection | DiagramDrawing) => true
    case _                                 => false
  }
}

case class UmlSolution(classes: Seq[UmlCompleteClass], associations: Seq[UmlAssociation], implementations: Seq[UmlImplementation]) {

  def allAttributes: Seq[UmlClassAttribute] = classes flatMap (_.attributes) groupBy (attr => (attr.name, attr.umlType)) map (_._2.head) toSeq

  def allMethods: Seq[UmlClassMethod] = classes flatMap (_.methods) groupBy (method => (method.name, method.umlType)) map (_._2.head) toSeq

}

case class UmlCompleteClass(clazz: UmlClass, attributes: Seq[UmlClassAttribute], methods: Seq[UmlClassMethod]) {

  def allMembers: Seq[UmlClassMember] = attributes ++ methods

}

// Table classes

object UmlExercise {

  def tupled(t: (Int, String, String, String, ExerciseState, String, String, String)): UmlExercise =
    UmlExercise(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, classSelText: String, diagDrawText: String, toIgnore: String) =
    new UmlExercise(BaseValues(id, title, author, text, state), classSelText, diagDrawText, toIgnore)

  def unapply(arg: UmlExercise): Option[(Int, String, String, String, ExerciseState, String, String, String)] =
    Some(arg.id, arg.title, arg.author, arg.text, arg.state, arg.classSelText, arg.diagDrawText, arg.toIgnore)

}

case class UmlExercise(baseValues: BaseValues, classSelText: String, diagDrawText: String, toIgnore: String) extends Exercise {

  def splitToIgnore: Seq[String] = toIgnore split TagJoinChar

}


case class UmlMapping(exerciseId: Int, key: String, value: String)


case class UmlClass(exerciseId: Int, className: String, classType: UmlClassType)

trait UmlClassMember {

  val exerciseId: Int
  val className : String
  val name      : String
  val umlType   : String

  def render: String = name + ": " + umlType

}

case class UmlClassAttribute(exerciseId: Int, className: String, name: String, umlType: String) extends UmlClassMember

case class UmlClassMethod(exerciseId: Int, className: String, name: String, umlType: String) extends UmlClassMember

case class UmlImplementation(exerciseId: Int, subClass: String, superClass: String)

case class UmlAssociation(exerciseId: Int, assocType: UmlAssociationType, assocName: Option[String],
                          firstEnd: String, firstMult: UmlMultiplicity,
                          secondEnd: String, secondMult: UmlMultiplicity) {

  def displayMult(turn: Boolean = false): String =
    if (turn) secondMult.representant + " : " + firstMult.representant
    else firstMult.representant + " : " + secondMult.representant

}

// Tables

class UmlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseTableDefs[UmlExercise, UmlCompleteEx] {

  import profile.api._

  // Reading

  override def completeExForEx(ex: UmlExercise)(implicit ec: ExecutionContext): Future[UmlCompleteEx] =
    db.run(mappingsAction(ex.id) zip classesAction(ex.id) zip associationsAction(ex.id) zip implementationsAction(ex.id)) flatMap {
      case (((mappings, classes), assocs), impls) =>
        completeClasses(ex, classes) map { compClasses =>
          val solution = UmlSolution(compClasses, assocs, impls)
          UmlCompleteEx(ex, mappings, solution)
        }
    }

  private def mappingsAction(id: Int) = umlMappings filter (_.exerciseId === id) result

  private def completeClasses(ex: UmlExercise, classes: Seq[UmlClass])(implicit ec: ExecutionContext): Future[Seq[UmlCompleteClass]] = Future.sequence(classes map { clazz =>
    db.run(attrsAction(ex.id, clazz.className) zip methodsAction(ex.id, clazz.className)) map { case (attrs, methods) => UmlCompleteClass(clazz, attrs, methods) }
  })

  private def classesAction(id: Int) = umlClasses filter (_.exerciseId === id) result

  private def attrsAction(id: Int, className: String) = umlClassAttributes filter (attr => attr.exerciseId === id && attr.className === className) result

  private def methodsAction(id: Int, className: String) = umlClassMethods filter (method => method.exerciseId === id && method.className === className) result

  private def associationsAction(id: Int) = umlAssociations filter (_.exerciseId === id) result

  private def implementationsAction(id: Int) = umlImplementations filter (_.exerciseId === id) result

  // Saving

  override protected def saveExerciseRest(compEx: UmlCompleteEx)(implicit ec: ExecutionContext): Future[Boolean] =
    saveMappings(compEx.mappings) zip saveSampleSolution(compEx.solution) map (future => future._1 && future._2)

  private def saveMappings(mappings: Seq[UmlMapping])(implicit ec: ExecutionContext): Future[Boolean] = saveSeq[UmlMapping](mappings, m => db.run(umlMappings += m))

  private def saveSampleSolution(solution: UmlSolution)(implicit ec: ExecutionContext): Future[Boolean] = saveSeq(solution.classes, saveClass) zip
    saveSeq[UmlAssociation](solution.associations, a => db.run(umlAssociations += a)) zip
    saveSeq[UmlImplementation](solution.implementations, i => db.run(umlImplementations += i)) map (t => t._1._1 && t._1._2 && t._2)

  private def saveClass(umlCompleteClass: UmlCompleteClass)(implicit ec: ExecutionContext): Future[Boolean] = db.run(umlClasses += umlCompleteClass.clazz) flatMap { _ =>
    saveSeq[UmlClassAttribute](umlCompleteClass.attributes, a => db.run(umlClassAttributes += a)) zip
      saveSeq[UmlClassMethod](umlCompleteClass.methods, m => db.run(umlClassMethods += m))
  } map (t => t._1 && t._2)

  // Deletion

  // Table Queries

  val umlExercises = TableQuery[UmlExercisesTable]


  val umlMappings = TableQuery[UmlMappingsTable]

  val umlClasses = TableQuery[UmlClassTable]

  val umlClassAttributes = TableQuery[UmlClassAttributesTable]

  val umlClassMethods = TableQuery[UmlClassMethodsTable]


  val umlAssociations = TableQuery[UmlAssociationsTable]

  val umlImplementations = TableQuery[UmlImplementationsTable]

  override type ExTableDef = UmlExercisesTable

  override val exTable = umlExercises

  // Implicit column types

  implicit val UmlClassTypeColumnType: BaseColumnType[UmlClassType] =
    MappedColumnType.base[UmlClassType, String](_.name, str => UmlClassType.byString(str) getOrElse UmlClassType.CLASS)

  implicit val UmlAssoctypeColumnType: BaseColumnType[UmlAssociationType] =
    MappedColumnType.base[UmlAssociationType, String](_.name, str => UmlAssociationType.byString(str) getOrElse UmlAssociationType.ASSOCIATION)

  implicit val UmlMultiplicityColumnType: BaseColumnType[UmlMultiplicity] =
    MappedColumnType.base[UmlMultiplicity, String](_.name, str => UmlMultiplicity.byString(str) getOrElse UmlMultiplicity.UNBOUND)

  // Table definitions

  class UmlExercisesTable(tag: Tag) extends HasBaseValuesTable[UmlExercise](tag, "uml_exercises") {

    def classSelText = column[String]("class_sel_text")

    def diagDrawText = column[String]("diag_draw_text")

    def toIgnore = column[String]("to_ignore")


    def pk = primaryKey("pk", id)


    def * = (id, title, author, text, state, classSelText, diagDrawText, toIgnore) <> (UmlExercise.tupled, UmlExercise.unapply)

  }

  class UmlMappingsTable(tag: Tag) extends Table[UmlMapping](tag, "uml_mappings") {

    def exerciseId = column[Int]("exercise_id")

    def mappingKey = column[String]("mapping_key")

    def mappingValue = column[String]("mapping_value")


    def pk = primaryKey("pk", (exerciseId, mappingKey))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, umlExercises)(_.id)


    def * = (exerciseId, mappingKey, mappingValue) <> (UmlMapping.tupled, UmlMapping.unapply)

  }

  class UmlClassTable(tag: Tag) extends Table[UmlClass](tag, "uml_sol_classes") {

    def className = column[String]("class_name")

    def exerciseId = column[Int]("exercise_id")

    def classType = column[UmlClassType]("class_type")


    def pk = primaryKey("pk", (exerciseId, className))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, umlExercises)(_.id)


    def * = (exerciseId, className, classType) <> (UmlClass.tupled, UmlClass.unapply)

  }


  class UmlClassAttributesTable(tag: Tag) extends Table[UmlClassAttribute](tag, "uml_sol_classes_attrs") {

    def exerciseId = column[Int]("exercise_id")

    def className = column[String]("class_name")

    def attrName = column[String]("attr_name")

    def attrType = column[String]("attr_type")


    def pk = primaryKey("pk", (exerciseId, className, attrName))

    def classFk = foreignKey("class_fk", (exerciseId, className), umlClasses)(c => (c.exerciseId, c.className))


    def * = (exerciseId, className, attrName, attrType) <> (UmlClassAttribute.tupled, UmlClassAttribute.unapply)

  }

  class UmlClassMethodsTable(tag: Tag) extends Table[UmlClassMethod](tag, "uml_sol_classes_methods") {

    def exerciseId = column[Int]("exercise_id")

    def className = column[String]("class_name")

    def methodName = column[String]("method_name")

    def returnType = column[String]("return_type")


    def pk = primaryKey("pk", (exerciseId, className, methodName, returnType))

    def classFk = foreignKey("class_fk", (exerciseId, className), umlClasses)(c => (c.exerciseId, c.className))


    def * = (exerciseId, className, methodName, returnType) <> (UmlClassMethod.tupled, UmlClassMethod.unapply)

  }

  class UmlImplementationsTable(tag: Tag) extends Table[UmlImplementation](tag, "uml_sol_impls") {

    def exerciseId = column[Int]("exercise_id")

    def subClass = column[String]("sub_class")

    def superClass = column[String]("super_class")


    def pk = primaryKey("pk", (exerciseId, subClass, superClass))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, umlExercises)(_.id)


    def * = (exerciseId, subClass, superClass) <> (UmlImplementation.tupled, UmlImplementation.unapply)

  }

  class UmlAssociationsTable(tag: Tag) extends Table[UmlAssociation](tag, "uml_sol_assocs") {

    def assocType = column[UmlAssociationType]("assoc_type")

    def assocName = column[String]("assoc_name")

    def exerciseId = column[Int]("exercise_id")

    def firstEnd = column[String]("first_end")

    def firstMult = column[UmlMultiplicity]("first_mult")

    def secondEnd = column[String]("second_end")

    def secondMult = column[UmlMultiplicity]("second_mult")


    def pk = primaryKey("pk", (exerciseId, firstEnd, secondEnd))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, umlExercises)(_.id)


    def * = (exerciseId, assocType, assocName.?, firstEnd, firstMult, secondEnd, secondMult) <> (UmlAssociation.tupled, UmlAssociation.unapply)

  }

}