package model.uml

import model.ExerciseState
import model.MyYamlProtocol._
import model.uml.UmlConsts._
import model.{MyYamlProtocol, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}
import scala.util.{Failure, Try}

object UmlExYamlProtocol extends MyYamlProtocol {

  implicit object UmlExYamlFormat extends HasBaseValuesYamlFormat[UmlCompleteEx] {

    override def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[UmlCompleteEx] = for {
      mappingTries <- yamlObject.arrayField(mappingsName, UmlMappingYamlFormat(baseValues._1).read)
      ignoreWordTries <- yamlObject.arrayField(ignoreWordsName, _ asStr)
      classDiagram <- yamlObject.someField(solutionName).flatMap(UmlSolutionYamlFormat.read)
    } yield {
      for (mappingFailure <- mappingTries._2)
      // FIXME: return...
        Logger.error("Could not read uml mapping", mappingFailure.exception)

      for (ignoreWordFailure <- ignoreWordTries._2)
      // FIXME: return...
        Logger.error("Could not read ignore word", ignoreWordFailure.exception)


      val mappings = mappingTries._1
      val ignoreWords = ignoreWordTries._1

      val mappingsForTextParser: Map[String, String] = mappings map (mapping => (mapping.key, mapping.value)) toMap
      val textParser = new UmlExTextParser(baseValues._4, mappingsForTextParser, ignoreWords)

      UmlCompleteEx(
        UmlExercise(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, classDiagram,
          textParser.parseTextForClassSel, textParser.parseTextForDiagDrawing, ignoreWords mkString tagJoinChar),
        mappings
      )
    }

    override protected def writeRest(completeEx: UmlCompleteEx): Map[YamlValue, YamlValue] = Map(
      YamlString(mappingsName) -> YamlArr(completeEx.mappings map UmlMappingYamlFormat(completeEx.ex.id).write),
      YamlString(ignoreWordsName) -> YamlArr(completeEx.ex.splitToIgnore map YamlString),
    )
  }

  case class UmlMappingYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[UmlMapping] {

    override def write(mapping: UmlMapping): YamlValue = YamlObj(keyName -> mapping.key, valueName -> mapping.value)

    override def readObject(yamlObject: YamlObject): Try[UmlMapping] = for {
      key <- yamlObject.stringField(keyName)
      value <- yamlObject.stringField(valueName)
    } yield UmlMapping(exerciseId, key, value)

  }

  implicit object UmlSolutionYamlFormat extends MyYamlObjectFormat[UmlClassDiagram] {

    override protected def readObject(yamlObject: YamlObject): Try[UmlClassDiagram] = for {
      classes: (Seq[UmlClass], Seq[Failure[UmlClass]]) <- yamlObject.arrayField(classesName, UmlCompleteClassYamlFormat.read)
      associations <- yamlObject.arrayField(associationsName, UmlAssocYamlFormat.read)
      implementations <- yamlObject.arrayField(implementationsName, UmlImplYamlFormat.read)
    } yield {

      for (classFailure <- classes._2)
      // FIXME: return...
        Logger.error("Could not read uml class", classFailure.exception)

      for (associationFailure <- associations._2)
      // FIXME: return...
        Logger.error("Could not read uml association", associationFailure.exception)

      for (implementationFailure <- implementations._2)
      // FIXME: return...
        Logger.error("Could not read uml implementation", implementationFailure.exception)

      UmlClassDiagram(classes._1, associations._1, implementations._1)
    }

    override def write(ucd: UmlClassDiagram): YamlValue = YamlObj(
      classesName -> YamlArr(ucd.classes map UmlCompleteClassYamlFormat.write),
      implementationsName -> YamlArr(ucd.implementations map UmlImplYamlFormat.write),
      associationsName -> YamlArr(ucd.associations map UmlAssocYamlFormat.write)
    )

  }

  private object UmlCompleteClassYamlFormat extends MyYamlObjectFormat[UmlClass] {

    override def write(completeClazz: UmlClass): YamlValue = YamlObj(
      classTypeName -> completeClazz.classType.entryName,
      nameName -> completeClazz.className,
      attributesName -> YamlArr(completeClazz.attributes map UmlAttributeYamlFormat.write),
      methodsName -> YamlArr(completeClazz.methods map UmlMethodYamlFormat.write)
    )

    override def readObject(yamlObject: YamlObject): Try[UmlClass] = for {
      className <- yamlObject.stringField(nameName)
      classType <- yamlObject.enumField(classTypeName, UmlClassType.withNameInsensitiveOption) map (_ getOrElse UmlClassType.CLASS)
      attributeTries <- yamlObject.optArrayField(attributesName, UmlAttributeYamlFormat.read)
      methodTries <- yamlObject.optArrayField(methodsName, UmlMethodYamlFormat.read)
    } yield {
      for (attributeFailure <- attributeTries._2)
      // FIXME: return...
        Logger.error("Could not read uml class attribute", attributeFailure.exception)

      for (methodFailure <- methodTries._2)
      // FIXME: return...
        Logger.error("Could not read uml class method", methodFailure.exception)

      UmlClass(classType, className, attributeTries._1, methodTries._1, None)
    }

  }

  private object UmlAttributeYamlFormat extends MyYamlObjectFormat[UmlAttribute] {

    override protected def readObject(yamlObject: YamlObject): Try[UmlAttribute] = for {
      visibility <- yamlObject.enumField(visibilityName, UmlVisibility.withNameInsensitiveOption) map (_ getOrElse UmlVisibility.PUBLIC)
      memberName <- yamlObject.stringField(nameName)
      memberType <- yamlObject.stringField(typeName)
    } yield UmlAttribute(visibility, memberName, memberType)

    override def write(obj: UmlAttribute): YamlValue = ???

  }

  private object UmlMethodYamlFormat extends MyYamlObjectFormat[UmlMethod] {

    override protected def readObject(yamlObject: YamlObject): Try[UmlMethod] = for {
      visibility <- yamlObject.enumField(visibilityName, UmlVisibility.withNameInsensitiveOption) map (_ getOrElse UmlVisibility.PUBLIC)
      memberName <- yamlObject.stringField(nameName)
      memberType <- yamlObject.stringField(typeName)
    } yield UmlMethod(visibility, memberName, memberType)

    override def write(obj: UmlMethod): YamlValue = ???

  }

  private object UmlAssocYamlFormat extends MyYamlObjectFormat[UmlAssociation] {

    override def write(assoc: UmlAssociation): YamlValue = YamlObj(
      associationTypeName -> assoc.assocType.entryName,
      firstEndName -> assoc.firstEnd,
      firstMultName -> assoc.firstMult.entryName,
      secondEndName -> assoc.secondEnd,
      secondMultName -> assoc.secondMult.entryName
    )

    override def readObject(yamlObject: YamlObject): Try[UmlAssociation] = for {
      assocType <- yamlObject.enumField(associationTypeName, UmlAssociationType.withNameInsensitiveOption) map (_ getOrElse UmlAssociationType.ASSOCIATION)
      maybeAssocName <- yamlObject.optStringField(nameName)
      firstEnd <- yamlObject.stringField(firstEndName)
      firstMult <- yamlObject.enumField(firstMultName, UmlMultiplicity.withNameInsensitiveOption) map (_ getOrElse UmlMultiplicity.UNBOUND)
      secondEnd <- yamlObject.stringField(secondEndName)
      secondMult <- yamlObject.enumField(secondMultName, UmlMultiplicity.withNameInsensitiveOption) map (_ getOrElse UmlMultiplicity.UNBOUND)
    } yield UmlAssociation(assocType, maybeAssocName, firstEnd, firstMult, secondEnd, secondMult)

  }

  private object UmlImplYamlFormat extends MyYamlObjectFormat[UmlImplementation] {

    override def write(impl: UmlImplementation): YamlValue = YamlObj(
      subClassName -> impl.subClass,
      superClassName -> impl.superClass
    )

    override def readObject(yamlObject: YamlObject): Try[UmlImplementation] = for {
      subClass <- yamlObject.stringField(subClassName)
      superClass <- yamlObject.stringField(superClassName)
    } yield UmlImplementation(subClass, superClass)

  }

}