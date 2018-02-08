package model.uml

import model.MyYamlProtocol._
import model.uml.UmlConsts._
import model.uml.UmlEnums.{UmlAssociationType, UmlClassType, UmlMultiplicity}
import model.{BaseValues, MyYamlProtocol, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object UmlExYamlProtocol extends MyYamlProtocol {

  implicit object UmlExYamlFormat extends HasBaseValuesYamlFormat[UmlCompleteEx] {

    override def readRest(yamlObject: YamlObject, baseValues: BaseValues): Try[UmlCompleteEx] = for {

      mappings: Seq[UmlMapping] <- yamlObject.arrayField(MAPPINGS_NAME, UmlMappingYamlFormat(baseValues.id).read)
      ignoreWords <- yamlObject.arrayField(IGNORE_WORDS_NAME, _ asStr)

      solution <- yamlObject.someField(SOLUTION_NAME) flatMap UmlSolutionYamlFormat(baseValues.id).read

      mappingsForTextParser: Map[String, String] = mappings map (mapping => (mapping.key, mapping.value)) toMap;
      textParser = new UmlExTextParser(baseValues.text, mappingsForTextParser, ignoreWords)

    } yield UmlCompleteEx(
      UmlExercise(baseValues, textParser.parseTextForClassSel, textParser.parseTextForDiagDrawing, ignoreWords mkString TagJoinChar),
      mappings,
      solution
    )

    override protected def writeRest(completeEx: UmlCompleteEx): Map[YamlValue, YamlValue] = Map(
      YamlString(MAPPINGS_NAME) -> YamlArr(completeEx.mappings map UmlMappingYamlFormat(completeEx.ex.id).write),
      YamlString(IGNORE_WORDS_NAME) -> YamlArr(completeEx.ex.splitToIgnore map YamlString),
      YamlString(SOLUTION_NAME) -> UmlSolutionYamlFormat(completeEx.ex.id).write(completeEx.solution)
    )
  }

  case class UmlMappingYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[UmlMapping] {

    override def write(mapping: UmlMapping): YamlValue = YamlObj(
      KEY_NAME -> mapping.key,
      VALUE_NAME -> mapping.value
    )

    override def readObject(yamlObject: YamlObject): Try[UmlMapping] = for {
      key <- yamlObject.stringField(KEY_NAME)
      value <- yamlObject.stringField(VALUE_NAME)
    } yield UmlMapping(exerciseId, key, value)

  }

  case class UmlSolutionYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[UmlSolution] {

    override def write(sol: UmlSolution): YamlValue = YamlObj(
      CLASSES_NAME -> YamlArr(sol.classes map UmlCompleteClassYamlFormat(exerciseId).write),
      IMPLS_NAME -> YamlArr(sol.implementations map UmlImplYamlFormat(exerciseId).write),
      ASSOCS_NAME -> YamlArr(sol.associations map UmlAssocYamlFormat(exerciseId).write)
    )

    override def readObject(yamlObject: YamlObject): Try[UmlSolution] = for {
      classes <- yamlObject.arrayField(CLASSES_NAME, UmlCompleteClassYamlFormat(exerciseId).read)
      associations <- yamlObject.arrayField(ASSOCS_NAME, UmlAssocYamlFormat(exerciseId).read)
      implementations <- yamlObject.arrayField(IMPLS_NAME, UmlImplYamlFormat(exerciseId).read)
    } yield UmlSolution(classes, associations, implementations)

  }

  case class UmlCompleteClassYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[UmlCompleteClass] {

    override def write(completeClazz: UmlCompleteClass): YamlValue = YamlObj(
      CLASSTYPE_NAME -> completeClazz.clazz.classType.name,
      NAME_NAME -> completeClazz.clazz.className,
      ATTRS_NAME -> YamlArr(completeClazz.attributes map UmlClassAttributeYamlFormat(exerciseId, completeClazz.clazz.className).write),
      METHODS_NAME -> YamlArr(completeClazz.methods map UmlClassMethodYamlFormat(exerciseId, completeClazz.clazz.className).write)
    )

    override def readObject(yamlObject: YamlObject): Try[UmlCompleteClass] = for {
      className <- yamlObject.stringField(NAME_NAME)
      classType <- yamlObject.enumField(CLASSTYPE_NAME, UmlClassType.valueOf)
      attributes: Seq[UmlClassAttribute] <- yamlObject.optArrayField(ATTRS_NAME, UmlClassAttributeYamlFormat(exerciseId, className).read)
      methods: Seq[UmlClassMethod] <- yamlObject.optArrayField(METHODS_NAME, UmlClassMethodYamlFormat(exerciseId, className).read)
    } yield UmlCompleteClass(UmlClass(exerciseId, className, classType), attributes, methods)
  }

  case class UmlClassAttributeYamlFormat(exerciseId: Int, className: String) extends MyYamlObjectFormat[UmlClassAttribute] {

    override def write(attr: UmlClassAttribute): YamlValue = YamlObj(NAME_NAME -> attr.name, TYPE_NAME -> attr.umlType)

    override def readObject(yamlObject: YamlObject): Try[UmlClassAttribute] = for {
      name <- yamlObject.stringField(NAME_NAME)
      attrtype <- yamlObject.stringField(TYPE_NAME)
    } yield UmlClassAttribute(exerciseId, className, name, attrtype)
  }

  case class UmlClassMethodYamlFormat(exerciseId: Int, className: String) extends MyYamlObjectFormat[UmlClassMethod] {

    override def write(method: UmlClassMethod): YamlValue = YamlObj(NAME_NAME -> method.name, TYPE_NAME -> method.umlType)

    override def readObject(yamlObject: YamlObject): Try[UmlClassMethod] = for {
      name <- yamlObject.stringField(NAME_NAME)
      methodType <- yamlObject.stringField(TYPE_NAME)
    } yield UmlClassMethod(exerciseId, className, name, methodType)
  }

  case class UmlAssocYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[UmlAssociation] {

    override def write(assoc: UmlAssociation): YamlValue = YamlObject(
      Map[YamlValue, YamlValue](
        YamlString(ASSOCTYPE_NAME) -> assoc.assocType.name,
        YamlString(FIRST_END_NAME) -> assoc.firstEnd,
        YamlString(FIRST_MULT_NAME) -> assoc.firstMult.representant,
        YamlString(SECOND_END_NAME) -> assoc.secondEnd,
        YamlString(SECOND_MULT_NAME) -> assoc.secondMult.representant
      ) ++ (assoc.assocName map (ac => YamlString(NAME_NAME) -> YamlString(ac)))
    )

    override def readObject(yamlObject: YamlObject): Try[UmlAssociation] = for {
      assocType <- yamlObject.enumField(ASSOCTYPE_NAME, UmlAssociationType.valueOf)
      assocName <- yamlObject.optStringField(NAME_NAME)
      firstEnd <- yamlObject.stringField(FIRST_END_NAME)
      firstMult <- yamlObject.enumField(FIRST_MULT_NAME, UmlMultiplicity.valueOf)
      secondEnd <- yamlObject.stringField(SECOND_END_NAME)
      secondMult <- yamlObject.enumField(SECOND_MULT_NAME, UmlMultiplicity.valueOf)
    } yield UmlAssociation(exerciseId, assocType, assocName, firstEnd, firstMult, secondEnd, secondMult)
  }

  case class UmlImplYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[UmlImplementation] {

    override def write(impl: UmlImplementation): YamlValue = YamlObj(
      SUBCLASS_NAME -> impl.subClass,
      SUPERCLASS_NAME -> impl.superClass
    )

    override def readObject(yamlObject: YamlObject): Try[UmlImplementation] = for {
      subClass <- yamlObject.stringField(SUBCLASS_NAME)
      superClass <- yamlObject.stringField(SUPERCLASS_NAME)
    } yield UmlImplementation(exerciseId, subClass, superClass)
  }

}