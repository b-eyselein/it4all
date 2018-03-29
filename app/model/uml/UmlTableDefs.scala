package model.uml

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.persistence.SingleExerciseTableDefs
import model.uml.UmlConsts._
import model.uml.UmlEnums._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

// Classes for use

case class UmlCompleteEx(ex: UmlExercise, mappings: Seq[UmlMapping], classes: Seq[UmlCompleteClass], associations: Seq[UmlAssociation], implementations: Seq[UmlImplementation])
  extends PartsCompleteEx[UmlExercise, UmlExPart] {

  override def preview: Html = views.html.uml.umlPreview(this)

  def getClassesForDiagDrawingHelp: String = {
    val sqrt = Math.round(Math.sqrt(classes.size))

    classes.zipWithIndex.map { case (clazz, index) =>
      s"""{
         |  name: "${clazz.clazz.className}",
         |  classType: "${clazz.clazz.classType.toString.toUpperCase}", attributes: [], methods: [],
         |  position: { x: ${(index / sqrt) * GapHorizontal + OFFSET}, y: ${(index % sqrt) * GapVertival + OFFSET} }
         |}""".stripMargin
    } mkString ","
  }

  override def hasPart(partType: UmlExPart): Boolean = partType match {
    case (ClassSelection) => true
    case DiagramDrawing   => false // TODO: Currently deactivated...
    case _                => false
  }

  def allAttributes: Seq[UmlClassAttribute] = classes flatMap (_.attributes) groupBy (attr => (attr.name, attr.umlType)) map (_._2.head) toSeq

  def allMethods: Seq[UmlClassMethod] = classes flatMap (_.methods) groupBy (method => (method.name, method.umlType)) map (_._2.head) toSeq

}


case class UmlCompleteClass(clazz: UmlClass, attributes: Seq[UmlClassAttribute], methods: Seq[UmlClassMethod]) {

  def allMembers: Seq[UmlClassMember] = attributes ++ methods

}

// Table classes

case class UmlExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, classSelText: String, diagDrawText: String, toIgnore: String) extends Exercise {

  def this(baseValues: (Int, String, String, String, ExerciseState), classSelText: String, diagDrawText: String, toIgnore: String) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, classSelText, diagDrawText, toIgnore)

  def splitToIgnore: Seq[String] = toIgnore split tagJoinChar

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
                          firstEnd: String, firstMult: UmlMultiplicity, secondEnd: String, secondMult: UmlMultiplicity) {

  def displayMult(turn: Boolean = false): String =
    if (turn) secondMult.representant + " : " + firstMult.representant
    else firstMult.representant + " : " + secondMult.representant

}

// Tables

class UmlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[UmlExercise, UmlCompleteEx, UmlSolution, UmlExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = UmlExercisesTable

  override protected type SolTableDef = UmlSolutionsTable

  // Table Queries

  override protected val exTable = TableQuery[UmlExercisesTable]

  override protected val solTable = TableQuery[UmlSolutionsTable]

  val umlMappings = TableQuery[UmlMappingsTable]

  val umlClasses = TableQuery[UmlClassTable]

  val umlClassAttributes = TableQuery[UmlClassAttributesTable]

  val umlClassMethods = TableQuery[UmlClassMethodsTable]

  val umlAssociations = TableQuery[UmlAssociationsTable]

  val umlImplementations = TableQuery[UmlImplementationsTable]

  // Reading

  override def completeExForEx(ex: UmlExercise)(implicit ec: ExecutionContext): Future[UmlCompleteEx] = for {
    classes <- completeClassesForEx(ex)
    mappings <- db.run(umlMappings filter (_.exerciseId === ex.id) result)
    assocs <- db.run(umlAssociations filter (_.exerciseId === ex.id) result)
    impls <- db.run(umlImplementations filter (_.exerciseId === ex.id) result)
  } yield UmlCompleteEx(ex, mappings, classes, assocs, impls)


  private def completeClassesForEx(ex: UmlExercise): Future[Seq[UmlCompleteClass]] = db.run(umlClasses filter (_.exerciseId === ex.id) result) flatMap { classesSeq =>
    Future.sequence(classesSeq map { clazz =>
      for {
        attrs <- db.run(umlClassAttributes filter (attr => attr.exerciseId === ex.id && attr.className === clazz.className) result)
        methods <- db.run(umlClassMethods filter (method => method.exerciseId === ex.id && method.className === clazz.className) result)
      } yield UmlCompleteClass(clazz, attrs, methods)
    })
  }

  // Saving

  override protected def saveExerciseRest(compEx: UmlCompleteEx)(implicit ec: ExecutionContext): Future[Boolean] = for {
    mappings <- saveSeq[UmlMapping](compEx.mappings, m => db.run(umlMappings += m))
    classes <- saveSeq[UmlCompleteClass](compEx.classes, saveClass)
    assocs <- saveSeq[UmlAssociation](compEx.associations, a => db.run(umlAssociations += a))
    impl <- saveSeq[UmlImplementation](compEx.implementations, i => db.run(umlImplementations += i))
  } yield mappings && classes && assocs && impl

  private def saveClass(umlCompleteClass: UmlCompleteClass): Future[Boolean] = for {
    classSaved <- db.run(umlClasses += umlCompleteClass.clazz) map (_ => true) recover { case _: Throwable => false }
    attrsSaved <- saveSeq[UmlClassAttribute](umlCompleteClass.attributes, a => db.run(umlClassAttributes += a))
    methodsSaved <- saveSeq[UmlClassMethod](umlCompleteClass.methods, m => db.run(umlClassMethods += m))
  } yield classSaved && attrsSaved && methodsSaved

  // Implicit column types

  implicit val UmlClassTypeColumnType: BaseColumnType[UmlClassType] =
    MappedColumnType.base[UmlClassType, String](_.name, str => UmlClassType.byString(str) getOrElse UmlClassType.CLASS)

  implicit val UmlAssoctypeColumnType: BaseColumnType[UmlAssociationType] =
    MappedColumnType.base[UmlAssociationType, String](_.name, str => UmlAssociationType.byString(str) getOrElse UmlAssociationType.ASSOCIATION)

  implicit val UmlMultiplicityColumnType: BaseColumnType[UmlMultiplicity] =
    MappedColumnType.base[UmlMultiplicity, String](_.name, str => UmlMultiplicity.byString(str) getOrElse UmlMultiplicity.UNBOUND)

  override protected implicit val partTypeColumnType: BaseColumnType[UmlExPart] =
    MappedColumnType.base[UmlExPart, String](_.urlName, str => UmlExParts.values.find(_.urlName == str) getOrElse ClassSelection)


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

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    def * = (exerciseId, mappingKey, mappingValue) <> (UmlMapping.tupled, UmlMapping.unapply)

  }

  class UmlClassTable(tag: Tag) extends Table[UmlClass](tag, "uml_sol_classes") {

    def className = column[String]("class_name")

    def exerciseId = column[Int]("exercise_id")

    def classType = column[UmlClassType]("class_type")


    def pk = primaryKey("pk", (exerciseId, className))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


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

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


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

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    def * = (exerciseId, assocType, assocName.?, firstEnd, firstMult, secondEnd, secondMult) <> (UmlAssociation.tupled, UmlAssociation.unapply)

  }

  class UmlSolutionsTable(tag: Tag) extends PartSolutionsTable[UmlSolution](tag, "uml_solutions") {

    def solutionJson = column[String]("solution_json")

    // FIXME: use json!

    //    def classes = column[Seq[UmlCompleteClass]]("classes")

    //    def assocs = column[Seq[UmlAssociation]]("assocs")

    //    def impls = column[Seq[UmlImplementation]]("impls")


    override def pk = primaryKey("pk", (username, exerciseId, part))


    override def * = (username, exerciseId, part, solutionJson) <> (tupled, unapply)


    private def tupled(vals: (String, Int, UmlExPart, String)) = UmlSolution(vals._1, vals._2, vals._3, Seq.empty, Seq.empty, Seq.empty)

    private def unapply(solution: UmlSolution) = Some(solution.username, solution.exerciseId, solution.part, solution.toString)

  }

}