package model.uml

import model.MyYamlProtocol._
import model.uml.UmlConsts._
import model.uml.UmlEnums.{UmlAssociationType, UmlClassType, UmlMultiplicity}
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

import scala.language.{implicitConversions, postfixOps}

object UmlExYamlProtocol extends MyYamlProtocol {

  implicit object UmlExYamlFormat extends HasBaseValuesYamlFormat[UmlCompleteEx] {

    override def readRest(yamlObject: YamlObject, baseValues: BaseValues): UmlCompleteEx = {

      val mappings = yamlObject.arrayField(MAPPINGS_NAME, _ convertTo[UmlMapping] UmlMappingYamlFormat(baseValues.id))
      val ignoreWords = yamlObject.arrayField(IGNORE_WORDS_NAME, _ asStr) flatten

      val solution = yamlObject.objectField[UmlSolution](SOLUTION_NAME, UmlSolutionYamlFormat(baseValues.id))

      val mappingsForTextParser: Map[String, String] = mappings map (mapping => (mapping.key, mapping.value)) toMap
      val textParser = new UmlExTextParser(baseValues.text, mappingsForTextParser, ignoreWords)

      UmlCompleteEx(
        UmlExercise(baseValues, textParser.parseTextForClassSel, textParser.parseTextForDiagDrawing, ignoreWords mkString TagJoinChar),
        mappings,
        solution
      )
    }

    override protected def writeRest(completeEx: UmlCompleteEx): Map[YamlValue, YamlValue] = Map(
      YamlString(MAPPINGS_NAME) -> YamlArray(completeEx.mappings map (_.toYaml(UmlMappingYamlFormat(completeEx.ex.id))) toVector),
      YamlString(IGNORE_WORDS_NAME) -> YamlArray(completeEx.ex.getToIgnore map YamlString toVector),
      YamlString(SOLUTION_NAME) -> completeEx.solution.toYaml(UmlSolutionYamlFormat(completeEx.ex.id))
    )
  }

  case class UmlMappingYamlFormat(exerciseId: Int) extends MyYamlFormat[UmlMapping] {

    override def write(mapping: UmlMapping): YamlValue = YamlObject(
      YamlString(KEY_NAME) -> mapping.key,
      YamlString(VALUE_NAME) -> mapping.value
    )

    override def readObject(yamlObject: YamlObject): UmlMapping = UmlMapping(
      exerciseId,
      yamlObject.stringField(KEY_NAME),
      yamlObject.stringField(VALUE_NAME)
    )

  }

  case class UmlSolutionYamlFormat(exerciseId: Int) extends MyYamlFormat[UmlSolution] {

    override def write(sol: UmlSolution): YamlValue = YamlObject(
      YamlString(CLASSES_NAME) -> YamlArray(sol.classes map (_.toYaml(UmlCompleteClassYamlFormat(exerciseId))) toVector),
      YamlString(IMPLS_NAME) -> YamlArray(sol.implementations map (_.toYaml(UmlImplYamlFormat(exerciseId))) toVector),
      YamlString(ASSOCS_NAME) -> YamlArray(sol.associations map (_.toYaml(UmlAssocYamlFormat(exerciseId))) toVector)
    )

    override def readObject(yamlObject: YamlObject): UmlSolution = UmlSolution(
      yamlObject.arrayField(CLASSES_NAME, _ convertTo[UmlCompleteClass] UmlCompleteClassYamlFormat(exerciseId)),
      yamlObject.arrayField(ASSOCS_NAME, _ convertTo[UmlAssociation] UmlAssocYamlFormat(exerciseId)),
      yamlObject.arrayField(IMPLS_NAME, _ convertTo[UmlImplementation] UmlImplYamlFormat(exerciseId))

    )

  }

  case class UmlCompleteClassYamlFormat(exerciseId: Int) extends MyYamlFormat[UmlCompleteClass] {

    override def write(completeClazz: UmlCompleteClass): YamlValue = {
      val clazz = completeClazz.clazz
      YamlObject(
        YamlString(CLASSTYPE_NAME) -> clazz.classType.name,
        YamlString(NAME_NAME) -> clazz.className,
        YamlString(ATTRS_NAME) -> YamlArray(completeClazz.attributes map (_.toYaml(UmlClassAttributeYamlFormat(exerciseId, clazz.className))) toVector),
        YamlString(METHODS_NAME) -> YamlArray(completeClazz.methods map (_.toYaml(UmlClassMethodYamlFormat(exerciseId, clazz.className))) toVector)
      )
    }

    override def readObject(yamlObject: YamlObject): UmlCompleteClass = {
      val className = yamlObject.stringField(NAME_NAME)
      UmlCompleteClass(
        UmlClass(
          exerciseId,
          className,
          yamlObject.enumField(CLASSTYPE_NAME, UmlClassType.valueOf, UmlClassType.CLASS)),
        yamlObject.optArrayField(ATTRS_NAME, _ convertTo[UmlClassAttribute] UmlClassAttributeYamlFormat(exerciseId, className)),
        yamlObject.optArrayField(METHODS_NAME, _ convertTo[UmlClassMethod] UmlClassMethodYamlFormat(exerciseId, className))
      )
    }
  }

  case class UmlClassAttributeYamlFormat(exerciseId: Int, className: String) extends MyYamlFormat[UmlClassAttribute] {

    override def write(attr: UmlClassAttribute): YamlValue = YamlObject(
      YamlString(NAME_NAME) -> attr.attrName,
      YamlString(TYPE_NAME) -> attr.attrType
    )

    override def readObject(yamlObject: YamlObject): UmlClassAttribute = UmlClassAttribute(
      exerciseId, className, yamlObject.stringField(NAME_NAME), yamlObject.stringField(TYPE_NAME)
    )
  }

  case class UmlClassMethodYamlFormat(exerciseId: Int, className: String) extends MyYamlFormat[UmlClassMethod] {

    override def write(method: UmlClassMethod): YamlValue = YamlObject(
      YamlString(NAME_NAME) -> method.methodName,
      YamlString(ReturnTypeName) -> method.returns
    )

    override def readObject(yamlObject: YamlObject): UmlClassMethod = UmlClassMethod(
      exerciseId, className, yamlObject.stringField(NAME_NAME), yamlObject.stringField(ReturnTypeName)
    )
  }

  case class UmlAssocYamlFormat(exerciseId: Int) extends MyYamlFormat[UmlAssociation] {

    override def write(assoc: UmlAssociation): YamlValue = YamlObject(
      Map[YamlValue, YamlValue](
        YamlString(ASSOCTYPE_NAME) -> assoc.assocType.name,
        YamlString(FIRST_END_NAME) -> assoc.firstEnd,
        YamlString(FIRST_MULT_NAME) -> assoc.firstMult.representant,
        YamlString(SECOND_END_NAME) -> assoc.secondEnd,
        YamlString(SECOND_MULT_NAME) -> assoc.secondMult.representant
      ) ++ (assoc.assocName map (ac => YamlString(NAME_NAME) -> YamlString(ac)))
    )

    override def readObject(yamlObject: YamlObject): UmlAssociation = UmlAssociation(
      exerciseId,
      yamlObject.enumField(ASSOCTYPE_NAME, UmlAssociationType.valueOf, UmlAssociationType.ASSOCIATION),
      yamlObject.optStringField(NAME_NAME),
      yamlObject.stringField(FIRST_END_NAME), yamlObject.enumField(FIRST_MULT_NAME, UmlMultiplicity.valueOf, UmlMultiplicity.UNBOUND),
      yamlObject.stringField(SECOND_END_NAME), yamlObject.enumField(SECOND_MULT_NAME, UmlMultiplicity.valueOf, UmlMultiplicity.UNBOUND)
    )
  }

  case class UmlImplYamlFormat(exerciseId: Int) extends MyYamlFormat[UmlImplementation] {

    override def write(impl: UmlImplementation): YamlValue = YamlObject(
      YamlString(SUBCLASS_NAME) -> impl.subClass,
      YamlString(SUPERCLASS_NAME) -> impl.superClass
    )

    override def readObject(yamlObject: YamlObject): UmlImplementation = UmlImplementation(
      exerciseId, yamlObject.stringField(SUBCLASS_NAME), yamlObject.stringField(SUPERCLASS_NAME)
    )
  }

}