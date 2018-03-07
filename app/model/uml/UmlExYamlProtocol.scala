package model.uml

import model.Enums.ExerciseState
import model.MyYamlProtocol._
import model.uml.UmlConsts._
import model.uml.UmlEnums.{UmlAssociationType, UmlClassType, UmlMultiplicity}
import model.{MyYamlProtocol, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object UmlExYamlProtocol extends MyYamlProtocol {

  implicit object UmlExYamlFormat extends HasBaseValuesYamlFormat[UmlCompleteEx] {

    override def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[UmlCompleteEx] = for {
      mappingTries <- yamlObject.arrayField(MAPPINGS_NAME, UmlMappingYamlFormat(baseValues._1).read)
      ignoreWordTries <- yamlObject.arrayField(IGNORE_WORDS_NAME, _ asStr)

      classes <- yamlObject.arrayField(CLASSES_NAME, UmlCompleteClassYamlFormat(baseValues._1).read)
      associations <- yamlObject.arrayField(ASSOCS_NAME, UmlAssocYamlFormat(baseValues._1).read)
      implementations <- yamlObject.arrayField(IMPLS_NAME, UmlImplYamlFormat(baseValues._1).read)

    } yield {
      for (mappingFailure <- mappingTries._2)
      // FIXME: return...
        Logger.error("Could not read uml mapping", mappingFailure.exception)

      for (ignoreWordFailure <- ignoreWordTries._2)
      // FIXME: return...
        Logger.error("Could not read ignore word", ignoreWordFailure.exception)

      for (classFailure <- classes._2)
      // FIXME: return...
        Logger.error("Could not read uml class", classFailure.exception)

      for (associationFailure <- associations._2)
      // FIXME: return...
        Logger.error("Could not read uml association", associationFailure.exception)

      for (implementationFailure <- implementations._2)
      // FIXME: return...
        Logger.error("Could not read uml implementation", implementationFailure.exception)


      val mappings = mappingTries._1
      val ignoreWords = ignoreWordTries._1

      val mappingsForTextParser: Map[String, String] = mappings map (mapping => (mapping.key, mapping.value)) toMap
      val textParser = new UmlExTextParser(baseValues._4, mappingsForTextParser, ignoreWords)

      UmlCompleteEx(
        new UmlExercise(baseValues, textParser.parseTextForClassSel, textParser.parseTextForDiagDrawing, ignoreWords mkString TagJoinChar),
        mappings, classes._1, associations._1, implementations._1
      )
    }

    override protected def writeRest(completeEx: UmlCompleteEx): Map[YamlValue, YamlValue] = Map(
      YamlString(MAPPINGS_NAME) -> YamlArr(completeEx.mappings map UmlMappingYamlFormat(completeEx.ex.id).write),
      YamlString(IGNORE_WORDS_NAME) -> YamlArr(completeEx.ex.splitToIgnore map YamlString),


      YamlString(CLASSES_NAME) -> YamlArr(completeEx.classes map UmlCompleteClassYamlFormat(completeEx.ex.id).write),
      YamlString(IMPLS_NAME) -> YamlArr(completeEx.implementations map UmlImplYamlFormat(completeEx.ex.id).write),
      YamlString(ASSOCS_NAME) -> YamlArr(completeEx.associations map UmlAssocYamlFormat(completeEx.ex.id).write)
    )
  }

  case class UmlMappingYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[UmlMapping] {

    override def write(mapping: UmlMapping): YamlValue = YamlObj(
      keyName -> mapping.key,
      VALUE_NAME -> mapping.value
    )

    override def readObject(yamlObject: YamlObject): Try[UmlMapping] = for {
      key <- yamlObject.stringField(keyName)
      value <- yamlObject.stringField(VALUE_NAME)
    } yield UmlMapping(exerciseId, key, value)

  }

  case class UmlCompleteClassYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[UmlCompleteClass] {

    override def write(completeClazz: UmlCompleteClass): YamlValue = YamlObj(
      CLASSTYPE_NAME -> completeClazz.clazz.classType.name,
      nameName -> completeClazz.clazz.className,
      attrsName -> YamlArr(completeClazz.attributes map UmlClassAttributeYamlFormat(exerciseId, completeClazz.clazz.className).write),
      METHODS_NAME -> YamlArr(completeClazz.methods map UmlClassMethodYamlFormat(exerciseId, completeClazz.clazz.className).write)
    )

    override def readObject(yamlObject: YamlObject): Try[UmlCompleteClass] = for {
      className <- yamlObject.stringField(nameName)
      classType <- yamlObject.enumField(CLASSTYPE_NAME, UmlClassType.valueOf)
      attributTries <- yamlObject.optArrayField(attrsName, UmlClassAttributeYamlFormat(exerciseId, className).read)
      methodTries <- yamlObject.optArrayField(METHODS_NAME, UmlClassMethodYamlFormat(exerciseId, className).read)
    } yield {
      for (attributeFailure <- attributTries._2)
      // FIXME: return...
        Logger.error("Could not read uml class attribute", attributeFailure.exception)

      for (methodFailure <- methodTries._2)
      // FIXME: return...
        Logger.error("Could not read uml class method", methodFailure.exception)

      UmlCompleteClass(UmlClass(exerciseId, className, classType), attributTries._1, methodTries._1)
    }
  }

  case class UmlClassAttributeYamlFormat(exerciseId: Int, className: String) extends MyYamlObjectFormat[UmlClassAttribute] {

    override def write(attr: UmlClassAttribute): YamlValue = YamlObj(nameName -> attr.name, TYPE_NAME -> attr.umlType)

    override def readObject(yamlObject: YamlObject): Try[UmlClassAttribute] = for {
      name <- yamlObject.stringField(nameName)
      attrtype <- yamlObject.stringField(TYPE_NAME)
    } yield UmlClassAttribute(exerciseId, className, name, attrtype)
  }

  case class UmlClassMethodYamlFormat(exerciseId: Int, className: String) extends MyYamlObjectFormat[UmlClassMethod] {

    override def write(method: UmlClassMethod): YamlValue = YamlObj(nameName -> method.name, TYPE_NAME -> method.umlType)

    override def readObject(yamlObject: YamlObject): Try[UmlClassMethod] = for {
      name <- yamlObject.stringField(nameName)
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
      ) ++ (assoc.assocName map (ac => YamlString(nameName) -> YamlString(ac)))
    )

    override def readObject(yamlObject: YamlObject): Try[UmlAssociation] = for {
      assocType <- yamlObject.enumField(ASSOCTYPE_NAME, UmlAssociationType.valueOf)
      assocName <- yamlObject.optStringField(nameName)
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