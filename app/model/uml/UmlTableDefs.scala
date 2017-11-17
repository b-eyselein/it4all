package model.uml

import model.Enums.ExerciseState
import model.uml.UmlConsts._
import model.uml.UmlEnums._
import model.uml.UmlExYamlProtocol.UmlSolutionYamlFormat
import model.{BaseValues, CompleteEx, Exercise, TableDefs}
import net.jcazevedo.moultingyaml._
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.{Html, HtmlFormat}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}
import scala.util.Try


case class UmlCompleteEx(ex: UmlExercise, mappings: Seq[UmlMapping], ignoreWords: Seq[UmlIgnore], solution: UmlSolution) extends CompleteEx[UmlExercise] {

  import views.html.core.helperTemplates.modal

  // FIXME: -> umlPreview.scala.html
  override def preview: Html = new Html(
    s"""<td>${modal.render("Klassenwahltext", new Html(ex.classSelText + "<hr>" + HtmlFormat.escape(ex.classSelText)), "Klassenwahltext")}</td>
       |<td>${modal.render("Diagrammzeichnentext", new Html(ex.diagDrawText + "<hr>" + HtmlFormat.escape(ex.diagDrawText)), "Diagrammzeichnentext")}</td>
       |<td>${modal.render("Lösung", new Html("<pre>" + solution.toYaml(UmlSolutionYamlFormat(ex.id)).prettyPrint + "<pre>"), "Lösung" + ex.id)}</td>
       |<td><ul>${mappings.map(m => s"<li>${m.key} --> ${m.value}</li>").mkString}</ul></td>
       |<td><ul>${ignoreWords.map(i => s"<li>$i</li>").mkString}</ul></td>""".stripMargin)


  def getClassesForDiagDrawingHelp: String = {
    val classes = solution.classes
    val sqrt = Math.round(Math.sqrt(classes.size))

    classes.zipWithIndex.map { case (clazz, index) =>
      s"""{
         |  name: "${clazz.clazz.className}",
         |  classType: "${clazz.clazz.classType.toString.toUpperCase}",
         |  attributes: [], methods: [],
         |  position: { x: ${(index / sqrt) * GAP + OFFSET}, y: ${(index % sqrt) * GAP + OFFSET} }
         |}""".stripMargin
    } mkString ","
  }

}

case class UmlSolution(classes: Seq[UmlCompleteClass], associations: Seq[UmlAssociation], implementations: Seq[UmlImplementation]) {

  def allAttributes: Seq[String] = classes.flatMap(_.attributes).distinct map (_.toString)

  def allMethods: Seq[String] = classes.flatMap(_.methods).distinct map (_.toString)
}

case class UmlCompleteClass(clazz: UmlClass, attributes: Seq[UmlClassAttribute], methods: Seq[UmlClassMethod]) {

  def allAttrs: Seq[String] = attributes map (_.toString)

  def allMethods: Seq[String] = methods map (_.toString)

}

// Table classes

object UmlExercise {

  def tupled(t: (Int, String, String, String, ExerciseState, String, String)): UmlExercise =
    UmlExercise(t._1, t._2, t._3, t._4, t._5, t._6, t._7)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, classSelText: String, diagDrawText: String) =
    new UmlExercise(BaseValues(id, title, author, text, state), classSelText, diagDrawText)

  def unapply(arg: UmlExercise): Option[(Int, String, String, String, ExerciseState, String, String)] =
    Some(arg.id, arg.title, arg.author, arg.text, arg.state, arg.classSelText, arg.diagDrawText)

}

case class UmlExercise(bv: BaseValues, classSelText: String, diagDrawText: String) extends Exercise(bv)


case class UmlMapping(exerciseId: Int, key: String, value: String)

case class UmlIgnore(exerciseId: Int, toIgnore: String)


case class UmlClass(exerciseId: Int, className: String, classType: UmlClassType)

abstract class UmlClassMember(exerciseId: Int, className: String, name: String, umlType: String) {

  val render: String = name + ": " + umlType

}

case class UmlClassAttribute(exId: Int, cn: String, attrName: String, attrType: String) extends UmlClassMember(exId, cn, attrName, attrType)

case class UmlClassMethod(en: Int, cn: String, methodName: String, returns: String) extends UmlClassMember(en, cn, methodName, returns)


case class UmlImplementation(exerciseId: Int, subClass: String, superClass: String)

case class UmlAssociation(exerciseId: Int, assocType: UmlAssociationType, assocName: Option[String], firstEnd: String, firstMult: UmlMultiplicity, secondEnd: String, secondMult: UmlMultiplicity)

// Tables

trait UmlTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  object umlExercises extends ExerciseTableQuery[UmlExercise, UmlCompleteEx, UmlExercisesTable](new UmlExercisesTable(_)) {

    override protected def completeExForEx(ex: UmlExercise)(implicit ec: ExecutionContext): Future[UmlCompleteEx] =
      db.run(mappingsAction(ex.id) zip ignoreAction(ex.id) zip classesAction(ex.id) zip associationsAction(ex.id) zip implementationsAction(ex.id)) flatMap {
        case ((((mappings, ignore), classes), assocs), impls) =>
          completeClasses(ex, classes) map { compClasses =>
            val solution = UmlSolution(compClasses, assocs, impls)
            UmlCompleteEx(ex, mappings, ignore, solution)
          }
      }

    override def saveCompleteEx(completeEx: UmlCompleteEx)(implicit ec: ExecutionContext): Future[Int] = {
      //      println("\n\nSaving complete ex!")

      val query = saveEx(completeEx.ex) zip saveMappings(completeEx.mappings) zip saveIgnoreWords(completeEx.ignoreWords) zip saveSolution(completeEx.solution)

      db.run(query) map (_._1._1._1)
    }

    private def saveMappings(mappings: Seq[UmlMapping]) = DBIO.sequence(mappings map (mapping => umlMappings insertOrUpdate mapping))

    private def saveIgnoreWords(ignoreWords: Seq[UmlIgnore]) = DBIO.sequence(ignoreWords map (ignore => umlToIgnore insertOrUpdate ignore))

    // Mappings and ignore words

    private def mappingsAction(id: Int) = umlMappings filter (_.exerciseId === id) result

    private def ignoreAction(id: Int) = umlToIgnore filter (_.exerciseId === id) result


    // Solution

    private def saveSolution(solution: UmlSolution) = saveCompClasses(solution.classes) zip saveAssocs(solution.associations) zip saveImpls(solution.implementations)

    private def saveCompClasses(compClasses: Seq[UmlCompleteClass]) = DBIO.sequence(compClasses map { compClass =>
      umlClasses insertOrUpdate compClass.clazz zip saveAttributes(compClass.attributes) zip saveMethods(compClass.methods)
    })

    private def saveAttributes(attributes: Seq[UmlClassAttribute]) = DBIO.sequence(attributes map (attr => umlClassAttributes insertOrUpdate attr))

    private def saveMethods(methods: Seq[UmlClassMethod]) = DBIO.sequence(methods map (method => umlClassMethods insertOrUpdate method))

    private def saveAssocs(assocs: Seq[UmlAssociation]) = DBIO.sequence(assocs map (assoc => umlAssociations insertOrUpdate assoc))

    private def saveImpls(impls: Seq[UmlImplementation]) = DBIO.sequence(impls map (impl => umlImplementations insertOrUpdate impl))

    private def completeClasses(ex: UmlExercise, classes: Seq[UmlClass])(implicit ec: ExecutionContext): Future[Seq[UmlCompleteClass]] = Future.sequence(classes map { clazz =>
      db.run(attrsAction(ex.id, clazz.className) zip methodsAction(ex.id, clazz.className)) map { case (attrs, methods) => UmlCompleteClass(clazz, attrs, methods) }
    })

    private def classesAction(id: Int) = umlClasses filter (_.exerciseId === id) result

    private def attrsAction(id: Int, className: String) = umlClassAttributes filter (attr => attr.exerciseId === id && attr.className === className) result

    private def methodsAction(id: Int, className: String) = umlClassMethods filter (method => method.exerciseId === id && method.className === className) result

    private def associationsAction(id: Int) = umlAssociations filter (_.exerciseId === id) result

    private def implementationsAction(id: Int) = umlImplementations filter (_.exerciseId === id) result

  }

  val umlMappings = TableQuery[UmlMappingsTable]

  val umlToIgnore = TableQuery[UmlIgnoreTable]


  val umlClasses = TableQuery[UmlClassTable]

  val umlClassAttributes = TableQuery[UmlClassAttributesTable]

  val umlClassMethods = TableQuery[UmlClassMethodsTable]


  val umlAssociations = TableQuery[UmlAssociationsTable]

  val umlImplementations = TableQuery[UmlImplementationsTable]

  // Implicit column types

  implicit val UmlClassTypeColumnType: BaseColumnType[UmlClassType] =
    MappedColumnType.base[UmlClassType, String](_.name, str => Try(UmlClassType.valueOf(str)).getOrElse(UmlClassType.CLASS))

  implicit val UmlAssoctypeColumnType: BaseColumnType[UmlAssociationType] =
    MappedColumnType.base[UmlAssociationType, String](_.name, str => Try(UmlAssociationType.valueOf(str)).getOrElse(UmlAssociationType.ASSOCIATION))

  implicit val UmlMultiplicityColumnType: BaseColumnType[UmlMultiplicity] =
    MappedColumnType.base[UmlMultiplicity, String](_.name, str => Try(UmlMultiplicity.valueOf(str)).getOrElse(UmlMultiplicity.UNBOUND))

  // Table definitions

  class UmlExercisesTable(tag: Tag) extends HasBaseValuesTable[UmlExercise](tag, "uml_exercises") {

    def classSelText = column[String]("class_sel_text")

    def diagDrawText = column[String]("diag_draw_text")


    def * = (id, title, author, text, state, classSelText, diagDrawText) <> (UmlExercise.tupled, UmlExercise.unapply)

  }

  trait ForeignKeyUmlExercise {
    self: Table[_] =>

    def exerciseId = column[Int]("exercise_id")

    def exerciseFk = foreignKey("exercise_fk", exerciseId, umlExercises)(_.id)

  }

  class UmlIgnoreTable(tag: Tag) extends Table[UmlIgnore](tag, "uml_ignore") with ForeignKeyUmlExercise {

    def toIgnore = column[String]("to_ignore")


    def pk = primaryKey("pk", (exerciseId, toIgnore))


    def * = (exerciseId, toIgnore) <> (UmlIgnore.tupled, UmlIgnore.unapply)

  }

  class UmlMappingsTable(tag: Tag) extends Table[UmlMapping](tag, "uml_mappings") with ForeignKeyUmlExercise {

    def mappingKey = column[String]("mapping_key")

    def mappingValue = column[String]("mapping_value")


    def pk = primaryKey("pk", (exerciseId, mappingKey))


    def * = (exerciseId, mappingKey, mappingValue) <> (UmlMapping.tupled, UmlMapping.unapply)

  }

  class UmlClassTable(tag: Tag) extends Table[UmlClass](tag, "uml_sol_classes") with ForeignKeyUmlExercise {

    def className = column[String]("class_name")

    def classType = column[UmlClassType]("class_type")


    def pk = primaryKey("pk", (exerciseId, className))


    def * = (exerciseId, className, classType) <> (UmlClass.tupled, UmlClass.unapply)

  }

  abstract class UmlClassMemberTable[M <: UmlClassMember](tag: Tag, name: String) extends Table[M](tag, name) {

    def exerciseId = column[Int]("exercise_id")

    def className = column[String]("class_name")


    def classFk = foreignKey("class_fk", (exerciseId, className), umlClasses)(c => (c.exerciseId, c.className))

  }

  class UmlClassAttributesTable(tag: Tag) extends UmlClassMemberTable[UmlClassAttribute](tag, "uml_sol_classes_attrs") {

    def attrName = column[String]("attr_name")

    def attrType = column[String]("attr_type")


    def pk = primaryKey("pk", (exerciseId, className, attrName))


    def * = (exerciseId, className, attrName, attrType) <> (UmlClassAttribute.tupled, UmlClassAttribute.unapply)

  }

  class UmlClassMethodsTable(tag: Tag) extends UmlClassMemberTable[UmlClassMethod](tag, "uml_sol_classes_methods") {

    def methodName = column[String]("method_name")

    def returns = column[String]("returns")


    def pk = primaryKey("pk", (exerciseId, className, methodName, returns))


    def * = (exerciseId, className, methodName, returns) <> (UmlClassMethod.tupled, UmlClassMethod.unapply)

  }

  class UmlImplementationsTable(tag: Tag) extends Table[UmlImplementation](tag, "uml_sol_impls") with ForeignKeyUmlExercise {

    def subClass = column[String]("sub_class")

    def superClass = column[String]("super_class")


    def pk = primaryKey("pk", (exerciseId, subClass, superClass))


    def * = (exerciseId, subClass, superClass) <> (UmlImplementation.tupled, UmlImplementation.unapply)

  }

  class UmlAssociationsTable(tag: Tag) extends Table[UmlAssociation](tag, "uml_sol_assocs") with ForeignKeyUmlExercise {

    def assocType = column[UmlAssociationType]("assoc_type")

    def name = column[String]("assoc_name")

    def firstEnd = column[String]("first_end")

    def firstMult = column[UmlMultiplicity]("first_mult")

    def secondEnd = column[String]("second_end")

    def secondMult = column[UmlMultiplicity]("second_mult")


    def pk = primaryKey("pk", (exerciseId, name, firstEnd, secondEnd))


    def * = (exerciseId, assocType, name.?, firstEnd, firstMult, secondEnd, secondMult) <> (UmlAssociation.tupled, UmlAssociation.unapply)

  }

}