package initialData.uml.coll_1

import initialData.InitialExercise
import model.Level
import model.tools.uml._

object umlColl1Ex2 {

  private val sample: UmlClassDiagram = UmlClassDiagram(
    classes = Seq(
      UmlClass(
        classType = UmlClassType.ABSTRACT,
        name = "Person",
        attributes = Seq(
          UmlAttribute(memberName = "id", memberType = "int"),
          UmlAttribute(memberName = "name", memberType = "string")
        )
      ),
      UmlClass(
        name = "Doktor",
        methods = Seq(
          UmlMethod(memberName = "verschreiben", parameters = "Patient", memberType = "void")
        )
      ),
      UmlClass(
        name = "Rezept",
        attributes = Seq(
          UmlAttribute(memberName = "id", memberType = "int")
        )
      ),
      UmlClass(
        name = "Patient",
        methods = Seq(
          UmlMethod(memberName = "entlassen", parameters = "Station", memberType = "void"),
          UmlMethod(memberName = "aufnehmen", parameters = "Station", memberType = "void")
        )
      ),
      UmlClass(name = "Krankenschwester"),
      UmlClass(
        name = "Station",
        attributes = Seq(
          UmlAttribute(memberName = "nummer", memberType = "int")
        )
      ),
      UmlClass(
        name = "Medikament",
        attributes = Seq(
          UmlAttribute(memberName = "id", memberType = "int"),
          UmlAttribute(memberName = "name", memberType = "string")
        )
      ),
      UmlClass(
        name = "Krankenhaus",
        attributes = Seq(
          UmlAttribute(memberName = "name", memberType = "string")
        )
      )
    ),
    implementations = Seq(
      UmlImplementation(subClass = "Patient", superClass = "Person"),
      UmlImplementation(subClass = "Doktor", superClass = "Person"),
      UmlImplementation(subClass = "Krankenschwester", superClass = "Person")
    ),
    associations = Seq(
      UmlAssociation(
        firstEnd = "Doktor",
        firstMult = UmlMultiplicity.SINGLE,
        secondEnd = "Rezept",
        secondMult = UmlMultiplicity.UNBOUND
      ),
      UmlAssociation(
        firstEnd = "Station",
        firstMult = UmlMultiplicity.SINGLE,
        secondEnd = "Patient",
        secondMult = UmlMultiplicity.UNBOUND
      ),
      UmlAssociation(
        firstEnd = "Station",
        firstMult = UmlMultiplicity.SINGLE,
        secondEnd = "Krankenschwester",
        secondMult = UmlMultiplicity.UNBOUND
      ),
      UmlAssociation(
        firstEnd = "Station",
        firstMult = UmlMultiplicity.SINGLE,
        secondEnd = "Doktor",
        secondMult = UmlMultiplicity.UNBOUND
      ),
      UmlAssociation(
        firstEnd = "Patient",
        firstMult = UmlMultiplicity.SINGLE,
        secondEnd = "Rezept",
        secondMult = UmlMultiplicity.UNBOUND
      ),
      UmlAssociation(
        firstEnd = "Rezept",
        firstMult = UmlMultiplicity.UNBOUND,
        secondEnd = "Medikament",
        secondMult = UmlMultiplicity.UNBOUND
      ),
      UmlAssociation(
        firstEnd = "Station",
        firstMult = UmlMultiplicity.UNBOUND,
        secondEnd = "Krankenhaus",
        secondMult = UmlMultiplicity.SINGLE
      )
    )
  )

  val umlColl1Ex2: InitialExercise[UmlExerciseContent] = InitialExercise(
    title = "Krankenhaus",
    authors = Seq("bje40dc"),
    text = """Ein Krankenhaus hat einen Namen und besteht aus Stationen mit einer eindeutigen Nummer.
             |Jede Station hat einen oder mehrere Doktoren, Krankenschwestern und Patienten.
             |Jede Person hat jeweils eine eindeutige Id und einen Namen.
             |Ein Doktor kann dem Patienten ein Rezept mit einer eindeutigen Id verschreiben, das ein oder mehrere
             |Medikamente beinhalten kann.
             |Ein Medikament hat eine eindeutige Id und einen Namen und kann Patienten auf verschiedenen Rezepten
             |verschrieben werden.
             |Ein Patient wiederum kann in einer Station aufgenommen bzw. entlassen werden.""".stripMargin
      .replace("\n", " "),
    difficulty = Level.Intermediate,
    content = UmlExerciseContent(
      mappings = Map(
        "Stationen"         -> "Station",
        "Doktoren"          -> "Doktor",
        "Krankenschwestern" -> "Krankenschwester",
        "Patienten"         -> "Patient",
        "Namen"             -> "Name",
        "Medikamente"       -> "Medikament",
        "Rezepten"          -> "Rezept"
      ),
      toIgnore = Seq("Ein", "Jede"),
      sampleSolutions = Seq(sample)
    )
  )
}
