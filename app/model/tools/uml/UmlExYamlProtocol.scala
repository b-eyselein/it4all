package model.tools.uml

import model.MyYamlProtocol._
import model.core.CoreConsts.{authorName, idName, semanticVersionName, statusName, textName, titleName}
import model.tools.uml.UmlConsts._
import model.{MyYamlProtocol, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object UmlExYamlProtocol extends MyYamlProtocol {

  private val logger = Logger("model.tools.uml.UmlExYamlProtocol")

  object UmlExYamlFormat extends MyYamlObjectFormat[UmlExercise] {

    override def readObject(yamlObject: YamlObject): Try[UmlExercise] = for {
      baseValues <- readBaseValues(yamlObject)

      mappingTries <- yamlObject.arrayField(mappingsName, UmlMappingYamlFormat.read)
      ignoreWordTries <- yamlObject.arrayField(ignoreWordsName, _ asStr)
      sampleSolutions <- yamlObject.arrayField(samplesName, UmlSampleSolutionYamlFormat.read)
    } yield {
      for (mappingFailure <- mappingTries._2)
      // FIXME: return...
        logger.error("Could not read uml mapping", mappingFailure.exception)

      for (ignoreWordFailure <- ignoreWordTries._2)
      // FIXME: return...
        logger.error("Could not read ignore word", ignoreWordFailure.exception)

      for (sampleSolFailure <- sampleSolutions._2)
      // FIXME: return...
        logger.error("Could not read uml sample solution", sampleSolFailure.exception)

      val mappings    = mappingTries._1 toMap
      val ignoreWords = ignoreWordTries._1

      //      val mappingsForTextParser: Map[String, String] = mappings map (mapping => (mapping._1, mapping._2)) toMap
      val textParser = new UmlExTextParser(baseValues.text, mappings, ignoreWords)

      UmlExercise(
        baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state, textParser.parseText,
        ignoreWords, mappings, sampleSolutions._1
      )
    }

    override def write(exercise: UmlExercise): YamlValue = YamlObject(
      YamlString(idName) -> exercise.id,
      YamlString(titleName) -> exercise.title,
      YamlString(authorName) -> exercise.author,
      YamlString(textName) -> exercise.text,
      YamlString(statusName) -> exercise.state.entryName,
      YamlString(semanticVersionName) -> exercise.semanticVersion.asString,
      YamlString(mappingsName) -> YamlArr(exercise.mappings.toSeq.map(UmlMappingYamlFormat.write)),
      YamlString(ignoreWordsName) -> YamlArr(exercise.toIgnore.map(YamlString))
    )

  }

  private object UmlMappingYamlFormat extends MyYamlObjectFormat[(String, String)] {

    override def write(mapping: (String, String)): YamlValue = YamlObj(keyName -> mapping._1, valueName -> mapping._2)

    override def readObject(yamlObject: YamlObject): Try[(String, String)] = for {
      key <- yamlObject.stringField(keyName)
      value <- yamlObject.stringField(valueName)
    } yield (key, value)

  }

  object UmlSampleSolutionYamlFormat extends MyYamlObjectFormat[UmlSampleSolution] {

    override protected def readObject(yamlObject: YamlObject): Try[UmlSampleSolution] = for {
      id <- yamlObject.intField(idName)
      classes <- yamlObject.arrayField(classesName, UmlCompleteClassYamlFormat.read)
      associations <- yamlObject.arrayField(associationsName, UmlAssocYamlFormat.read)
      implementations <- yamlObject.arrayField(implementationsName, UmlImplYamlFormat.read)
    } yield {

      for (classFailure <- classes._2)
      // FIXME: return...
        logger.error("Could not read uml class", classFailure.exception)

      for (associationFailure <- associations._2)
      // FIXME: return...
        logger.error("Could not read uml association", associationFailure.exception)

      for (implementationFailure <- implementations._2)
      // FIXME: return...
        logger.error("Could not read uml implementation", implementationFailure.exception)

      UmlSampleSolution(id, UmlClassDiagram(classes._1, associations._1, implementations._1))
    }

    override def write(obj: UmlSampleSolution): YamlValue = YamlObj(
      idName -> YamlNumber(obj.id),
      classesName -> YamlArr(obj.sample.classes.map(UmlCompleteClassYamlFormat.write)),
      implementationsName -> YamlArr(obj.sample.implementations.map(UmlImplYamlFormat.write)),
      associationsName -> YamlArr(obj.sample.associations.map(UmlAssocYamlFormat.write))
    )

  }

  private object UmlCompleteClassYamlFormat extends MyYamlObjectFormat[UmlClass] {

    override def write(completeClazz: UmlClass): YamlValue = YamlObj(
      classTypeName -> completeClazz.classType.entryName,
      nameName -> completeClazz.name,
      attributesName -> YamlArr(completeClazz.attributes.map(UmlAttributeYamlFormat.write)),
      methodsName -> YamlArr(completeClazz.methods.map(UmlMethodYamlFormat.write))
    )

    override def readObject(yamlObject: YamlObject): Try[UmlClass] = for {
      className <- yamlObject.stringField(nameName)
      classType <- yamlObject.enumField(classTypeName, UmlClassType.withNameInsensitiveOption).map(_ getOrElse UmlClassType.CLASS)
      attributeTries <- yamlObject.optArrayField(attributesName, UmlAttributeYamlFormat.read)
      methodTries <- yamlObject.optArrayField(methodsName, UmlMethodYamlFormat.read)
    } yield {
      for (attributeFailure <- attributeTries._2)
      // FIXME: return...
        logger.error("Could not read uml class attribute", attributeFailure.exception)

      for (methodFailure <- methodTries._2)
      // FIXME: return...
        logger.error("Could not read uml class method", methodFailure.exception)

      UmlClass(classType, className, attributeTries._1, methodTries._1, None)
    }

  }

  private object UmlAttributeYamlFormat extends MyYamlObjectFormat[UmlAttribute] {

    override protected def readObject(yamlObject: YamlObject): Try[UmlAttribute] = for {
      visibility <- yamlObject.enumField(visibilityName, UmlVisibility.withNameInsensitiveOption).map(_ getOrElse UmlVisibility.PUBLIC)
      memberName <- yamlObject.stringField(nameName)
      memberType <- yamlObject.stringField(typeName)
      isAbstract <- yamlObject.optBoolField("isAbstract").map(_ getOrElse false)
      isDerived <- yamlObject.optBoolField("isDerived").map(_ getOrElse false)
      isStatic <- yamlObject.optBoolField("isStatic").map(_ getOrElse false)
    } yield UmlAttribute(visibility, memberName, memberType, isStatic, isDerived, isAbstract)

    override def write(obj: UmlAttribute): YamlValue = ???

  }

  private object UmlMethodYamlFormat extends MyYamlObjectFormat[UmlMethod] {

    override protected def readObject(yamlObject: YamlObject): Try[UmlMethod] = for {
      visibility <- yamlObject.enumField(visibilityName, UmlVisibility.withNameInsensitiveOption).map(_ getOrElse UmlVisibility.PUBLIC)
      memberName <- yamlObject.stringField(nameName)
      memberType <- yamlObject.stringField(typeName)
      parameters <- yamlObject.stringField("parameters")
      isAbstract <- yamlObject.optBoolField("isAbstract").map(_ getOrElse false)
      isStatic <- yamlObject.optBoolField("isStatic").map(_ getOrElse false)
    } yield UmlMethod(visibility, memberName, memberType, parameters, isAbstract, isStatic)

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
      assocType <- yamlObject.enumField(associationTypeName, UmlAssociationType.withNameInsensitiveOption).map(_ getOrElse UmlAssociationType.ASSOCIATION)
      maybeAssocName <- yamlObject.optStringField(nameName)
      firstEnd <- yamlObject.stringField(firstEndName)
      firstMult <- yamlObject.enumField(firstMultName, UmlMultiplicity.withNameInsensitiveOption).map(_ getOrElse UmlMultiplicity.UNBOUND)
      secondEnd <- yamlObject.stringField(secondEndName)
      secondMult <- yamlObject.enumField(secondMultName, UmlMultiplicity.withNameInsensitiveOption).map(_ getOrElse UmlMultiplicity.UNBOUND)
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
