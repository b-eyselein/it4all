package initialData.uml.coll_1

import model.tools.uml.UmlTool.UmlExercise
import model.tools.uml._
import model.{Exercise, SampleSolution}

object UmlColl1Ex1 {

  private val sample: UmlClassDiagram = UmlClassDiagram(
    classes = Seq(
      UmlClass(
        name = "Kunde",
        attributes = Seq(
          UmlAttribute(memberName = "name", memberType = "string"),
          UmlAttribute(memberName = "kundennummer", memberType = "int")
        ),
        methods = Seq(
          UmlMethod(memberName = "gesamtGuthaben", parameters = "", memberType = "float")
        )
      ),
      UmlClass(
        name = "Konto",
        classType = UmlClassType.ABSTRACT,
        attributes = Seq(
          UmlAttribute(memberName = "kontonummer", memberType = "int"),
          UmlAttribute(memberName = "vertragsdauerInJahren", memberType = "int"),
          UmlAttribute(memberName = "zinssatz", memberType = "float"),
          UmlAttribute(memberName = "saldo", memberType = "float")
        ),
        methods = Seq(
          UmlMethod(memberName = "einzahlen", parameters = "double", memberType = "void")
        )
      ),
      UmlClass(name = "Sparkonto"),
      UmlClass(name = "Girokonto"),
      UmlClass(name = "Kreditkonto")
    ),
    associations = Seq(
      UmlAssociation(
        firstEnd = "Kunde",
        firstMult = UmlMultiplicity.SINGLE,
        secondEnd = "Konto",
        secondMult = UmlMultiplicity.UNBOUND
      )
    ),
    implementations = Seq(
      UmlImplementation(subClass = "Girokonto", superClass = "Konto"),
      UmlImplementation(subClass = "Sparkonto", superClass = "Konto"),
      UmlImplementation(subClass = "Kreditkonto", superClass = "Konto")
    )
  )

  val umlColl1Ex1: UmlExercise = Exercise(
    exerciseId = 1,
    collectionId = 1,
    toolId = "uml",
    title = "Tutorial Konto",
    authors = Seq("bje40dc"),
    text = """Eine Bank verwaltet die Konten ihrer Kunden.
             |Jedes Konto wird durch die Kontonummer identifiziert und speichert das momentane Saldo sowie die
             |Vertragsdauer (in Jahren).
             |Diese Konten können entweder ein Girokonto, ein Sparkonto oder ein Kreditkonto sein.
             |Für jeden Typ von Konto gilt ein anderer Zinssatz: Für ein Girokonto gibt es keine Zinsen, für ein Sparkonto
             |1 Prozent und für ein Kreditkonto 3 Prozent.
             |Die Kunden der Bank werden durch ihre Kundennummer identifiziert.
             |Es soll außerdem jeweils der komplette Name gespeichert werden.
             |Jeder Kunde kann mehrere Konten besitzen und hat die Möglichkeit, sich das Gesamtsaldo aller Konten anzeigen
             |zu lassen und Geld auf ein Konto einzuzahlen.""".stripMargin.replace("\n", " "),
    difficulty = 1,
    topicAbbreviations = Seq.empty,
    content = UmlExerciseContent(
      mappings = Map(
        "Konten" -> "Konto",
        "Kunden" -> "Kunde",
        "Jahren" -> "Jahr",
        "Zinsen" -> "Zins"
      ),
      toIgnore = Seq("Eine", "Jedes", "Diese", "Für", "Jeder", "Es", "Zudem", "Die"),
      sampleSolutions = Seq(
        SampleSolution(id = 1, sample = sample)
      )
    )
  )
}
