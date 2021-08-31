package model

import model.grammar._

object DefaultGrammars extends TestValues {

  val englishGrammar: BaseNormalizedFormGrammar = {

    val np = Variable("NP")
    val v  = Variable("V")
    val vp = Variable("VP")

    val d  = Variable("D")
    val p  = Variable("P")
    val c  = Variable("C")
    val pp = Variable("PP")

    val ate       = Terminal("ate")
    val banana    = Terminal("banana")
    val bill      = Terminal("bill")
    val book      = Terminal("book")
    val boy       = Terminal("boy")
    val flies     = Terminal("flies")
    val flight    = Terminal("flight")
    val houston   = Terminal("houston")
    val john      = Terminal("john")
    val sleep     = Terminal("sleep")
    val telescope = Terminal("telescope")
    val time      = Terminal("time")

    BaseNormalizedFormGrammar(
      S,
      Map(
        S      -> alternatives(np + vp, vp),
        N      -> alternatives(time, flight, banana, flies, boy, telescope),
        d      -> alternatives(Terminal("the"), Terminal("a"), Terminal("an")),
        v      -> alternatives(book, ate, sleep, Terminal("saw"), Terminal("thinks")),
        p      -> alternatives(Terminal("with"), Terminal("in"), Terminal("on"), Terminal("at"), Terminal("through")),
        c      -> Terminal("that"),
        pp     -> (p + np),
        np     -> alternatives(john, bill, houston, d + N, np + pp),
        vp     -> alternatives(v + np, vp + pp, v + sSlash),
        sSlash -> (c + S)
      )
    )
  }

  val ambiguousGrammar: BaseNormalizedFormGrammar = {

    val np = Variable("NP")
    val v  = Variable("V")
    val vp = Variable("VP")

    val people = Terminal("people")
    val fish   = Terminal("fish")
    val tanks  = Terminal("tanks")

    BaseNormalizedFormGrammar(
      S,
      Map(
        S  -> alternatives(np + vp, v + np),
        vp -> alternatives(v + np, people, fish),
        np -> alternatives(np + np, people, fish, tanks),
        v  -> fish
      )
    )
  }

  val binaryPalindromesBaseNormalized: BaseNormalizedFormGrammar = BaseNormalizedFormGrammar(
    S,
    Map(
      S -> alternatives(
        termNull + S + termNull,
        termOne + S + termOne,
        termNull + termNull,
        termOne + termOne,
        termOne,
        termNull
      )
    )
  )

  val binaryPalindromesBaseNormalizedWithEmpty: BaseNormalizedFormGrammar = BaseNormalizedFormGrammar(
    S,
    Map(
      S -> alternatives(
        termNull + EmptyWord + S + EmptyWord + termNull,
        termOne + EmptyWord + S + termOne,
        termNull + termNull,
        termOne + termOne,
        termOne,
        termNull
      )
    )
  )

  val binaryPalindromesBnf: BackusNaurFormGrammar = BackusNaurFormGrammar(
    S,
    Map(
      S -> ((termOne and S and termOne) or (termNull and S and termNull) or (termOne and termOne) or (termNull and termNull) or termOne or termNull)
    )
  )

  val binaryPalindromesGrammarEbnf: ExtendedBackusNaurFormGrammar = ExtendedBackusNaurFormGrammar(
    S,
    Map(
      S -> ((termOne ~ S ~ termOne) | (termNull ~ S ~ termNull) | (termOne ~ termOne) | (termNull ~ termNull) | termOne | termNull)
    )
  )

  // Numbers

  val numbersGrammar: ExtendedBackusNaurFormGrammar = {
    val digit     = Variable("D")
    val digitZero = Variable("Z")

    ExtendedBackusNaurFormGrammar(
      S,
      Map(
        S         -> ((Terminal("-").? ~ digit ~ digitZero.*) | digitZero),
        digit     -> EbnfAlternative((1 to 9).map(d => Terminal(d.toString))),
        digitZero -> EbnfAlternative((0 to 9).map(d => Terminal(d.toString)))
      )
    )
  }

}
