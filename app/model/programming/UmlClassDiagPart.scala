package model.programming

import model.uml._

case class UmlClassDiagPart(exerciseId: Int, className: String, umlClassDiagram: UmlClassDiagram) {

  private val classSize = 150

  private val margin = 50

  private val displacement = classSize + margin

  def printToJavascript: String = {
    val toupling = Math.ceil(Math.sqrt(umlClassDiagram.classes.size)).toInt

    s"""const CLASS_WIDTH = $classSize, CLASS_HEIGHT = $classSize;
       |
       |const classDiagClasses = [
       |${displayClasses(umlClassDiagram.classes, toupling)}
       |];
       |
       |const classDiagImplementations = [
       |${displayImplementations(umlClassDiagram.implementations)}
       |];
       |
       |const classDiagAssociations = [
       |${displayAssociations(umlClassDiagram.associations)}
       |];""".stripMargin

  }

  private def displayClasses(classes: Seq[UmlClass], toupling: Int): String = if (classes.isEmpty) "" else
    classes.grouped(toupling).zipWithIndex map { case (classQuad, rowIndex) =>
      classQuad.zipWithIndex map { case (clazz, index) =>
        // FIXME: position!
        s"""{
           |    id: '${clazz.className}',
           |    position: {x: ${index * displacement + margin}, y: ${rowIndex * displacement + margin}},
           |    size: {width: CLASS_WIDTH, height: CLASS_HEIGHT},
           |    name: '${clazz.className}',
           |    classType: 'CLASS',
           |    attributes: [${displayMembers(clazz, _.attributes.map(a => "'" + a.memberName + ": " + a.memberType + "'"))}],
           |    methods: [${displayMembers(clazz, _.methods.map(m => "'" + m.memberName + ": " + m.memberType + "'"))}]
           |}"""
      } mkString ",\n"
    } mkString ",\n"

  private def displayMembers(clazz: UmlClass, member: UmlClass => Seq[String]): String = member(clazz) mkString ","

  private def displayImplementations(implementations: Seq[UmlImplementation]): String = implementations map { impl =>
    s"""{
       |  sourceId: "${impl.subClass}", targetId: "${impl.superClass}",
       |  type: "Implementation", sourceMultiplicity: "", targetMultiplicity: "",
       |}""".stripMargin
  } mkString ",\n"

  private def displayAssociations(associations: Seq[UmlAssociation]): String = associations map { assoc =>
    s"""{
       |  sourceId: "${assoc.firstEnd}", targetId: "${assoc.secondEnd}",
       |  type: "${assoc.assocType.entryName}", sourceMultiplicity: "${assoc.firstMult.representant}", targetMultiplicity: "${assoc.secondMult.representant}",
       |}""".stripMargin
  } mkString ",\n"

}

