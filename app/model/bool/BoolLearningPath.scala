package model.bool

import model._
import play.twirl.api.Html

object CurHelper {

  private val formulas: Map[String, Int] = Map("0 &vee; 0" -> 0, "0 &vee; 1" -> 1, "1 &vee; 0" -> 1, "1 &vee; 1" -> 1,
    "0 &wedge; 0" -> 0, "0 &wedge; 1" -> 0, "1 &wedge; 0" -> 0, "1 &wedge; 1" -> 1,
    "&not; 0" -> 1, "&not; 1" -> 0)

  val formulaExercise: String = scala.util.Random.shuffle(formulas) grouped 4 map { formulaTuple =>
    val row = formulaTuple map { case (formula, solution) =>
      s"""<div class="col-md-3">
         |  <div class="form-group">
         |      <div class="input-group">
         |          <button class="form-control" onclick="correctBool(this)">0</button>
         |          <span class="input-group-addon"><code data-solution="$solution">${Html(formula)}</code></span>
         |          <button class="form-control" onclick="correctBool(this)">1</button>
         |      </div>
         |  </div>
         |</div>""".stripMargin
    } mkString "\n"

    """<div class="row">""" + row + "</div>"
  } mkString "\n"

}

object BoolLearningPath extends CompleteLearningPath(
  LearningPathBase(id = 1, title = "Boolesche Algebra"),

  sections = Seq(
    TextSection(1, 1, "Grundlagen Boolescher Algebra",
      """<p>
        |  Die <strong>Boolesche Algebra</strong> stellt die Grundlage für den Entwurf von elektronischen Schaltungen
        |  bis hin zu Computern dar. Sie ist nach George Bool (1815 - 1864) benannt, der als erster eine "Algebra der Logik" entwickelt hat.
        |</p>
        |
        |<p>
        |  Diese kennt nur die beiden Zustände <code>wahr</code> und <code>falsch</code>, die in einem Schaltkreis den
        |  grundlegenden Zuständen "Strom fließt" und "Strom fließt nicht" entsprechen. Diese Zustände werden normalerweise
        |  durch die Zahlen <code>1</code> und <code>0</code> modelliert. Als Variablen in Booleschen Ausdrücken werden
        |  normalerweise Kleinbuchstaben (vom Anfang des Alphabets) wie <code>a</code>, <code>b</code>, ..., verwendet.
        |</p>""".stripMargin),

    TextSection(2, 1, "Operationen in der Booleschen Algebra",
      """<p>
        |  Die Boolesche Algebra kennt die drei grundlegenden Operationen <strong>Konjunktion</strong>,
        |  <strong>Disjunktion</strong> und <strong>Negation</strong>. Die Konjuntion und Diskunktion sind binäre
        |  Verknüpfungen (lat. <i>bina</i>, "doppelt"), die jeweils zwei Argumente miteinander verknüpfen, die Negation
        |  arbeitet nur auf einem Argument.
        |</p>
        |
        |<ul>
        |  <li>
        |    Das Ergebnis einer Konjunktion (<strong>Und</strong>-Verknüpfung) ist genau dann 1, wenn <i>beide</i>
        |    Argumente 1 sind (Merke: Argument 1 <strong>und</strong> Argument 2 sind 1). Ansonsten ist das Ergebnis 0.
        |    Eine Konjunktion wird mit dem Operator <code>&wedge;</code> dargestellt: <code>a &wedge; b</code> (sprich: "a und b").
        |  </li>
        |
        |  <li>
        |    Das Ergebnis einer Disjunktion (<strong>Oder</strong>-Verknüpfung ist 1, falls <i>eines</i> der beiden
        |    Argumente (oder auch beide) 1 sind (Merke: Argument 1 <strong>oder</strong> Argument 2 ist 1). Das Ergebnis ist
        |    nur 0, falls beide Argument 0 sind. Eine Disjunktion wird mit dem Operator <code>&vee;</code> dargestellt:
        |    <code>a &vee; b</code> (sprich: "a oder b").
        |  </li>
        |
        |  <li>
        |    Das Ergebnis einer Negation (<strong>Verneinung</strong>) ist das <i>Gegenteil</i> des Arguments, also 0
        |    bei einer 1 und eine 1 bei einer Null. Die Negation wird mit dem unären Operator <code>&not;</code> dargestellt:
        |    <code>&not; a</code> (sprich: "nicht a")
        |  </li>
        |</ul>
        |
        |<p>Ein Ausdruck, der Boolesche Variablen und eine beliebige Anzahl von Boolesche Operatoren beinhaltet, wird <strong>
        |  Boolesche Funktion</strong> genannt. Beispiele für boolesche Funktionen sind: <code>a</code>,
        |  <code>a &wedge; b</code>, <code>a &vee; b &wedge; (c &vee; d)</code>
        |</p>""".stripMargin),

    ExerciseSection(3, 1, "Übung: Boolesche Operationen", "<p>Was ist das Ergebnis folgender Ausdrücke?</p>" + CurHelper.formulaExercise),

    TextSection(4, 1, "Wahrheitstabellen",
      """<p>
        |  Eine Wahrheitstabelle einer Booleschen Funktion gibt das Resultat der Funktion für jede mögliche Kombination
        |  der Argumente an. Sie hat immer 2^[Anzahl der Funktionsargumente] Spalten. Die Wahrheitstabellen für die drei
        |  grundlegenden Operationen Konjunktion, Negation und Disjunktion sind:
        |</p>
        |
        |<div class="row">
        |  <div class="col-md-4">
        |    <h3 class="text-center">Konjunktion: <code>a &wedge; b</code></h3>
        |
        |    <table class="table table-bordered text-center">
        |      <thead>
        |        <tr><td>a</td><td>b</td><td>a &wedge; b</td></tr>
        |      </thead>
        |      <tbody>
        |        <tr><td>0</td><td>0</td><td>0</td></tr>
        |        <tr><td>0</td><td>1</td><td>0</td></tr>
        |        <tr><td>1</td><td>0</td><td>0</td></tr>
        |        <tr><td>1</td><td>1</td><td>1</td></tr>
        |      </tbody>
        |    </table>
        |  </div>
        |
        |  <div class="col-md-4">
        |    <h3 class="text-center">Negation: <code>&not; a</code></h3>
        |    <table class="table table-bordered text-center">
        |      <thead>
        |        <tr><td>a</td><td>&not; a</td></tr>
        |      </thead>
        |      <tbody>
        |        <tr><td>0</td><td>1</td></tr>
        |        <tr><td>1</td><td>0</td></tr>
        |      </tbody>
        |    </table>
        |  </div>
        |
        |  <div class="col-md-4">
        |    <h3 class="text-center">Disjunktion: <code>a &vee; b</code></h3>
        |    <table class="table table-bordered text-center">
        |      <thead>
        |        <tr><td>a</td><td>b</td><td>a &vee; b</td></tr>
        |      </thead>
        |      <tbody>
        |        <tr><td>0</td><td>0</td><td>0</td></tr>
        |        <tr><td>0</td><td>1</td><td>1</td></tr>
        |        <tr><td>1</td><td>0</td><td>1</td></tr>
        |        <tr><td>1</td><td>1</td><td>1</td></tr>
        |      </tbody>
        |    </table>
        |  </div>
        |</div>""".stripMargin)
  )
)
