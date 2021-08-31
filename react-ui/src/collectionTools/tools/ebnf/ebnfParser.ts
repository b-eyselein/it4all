import {alt, createLanguage, optWhitespace, regexp, Result, sepBy, sepBy1, seq, string, TypedLanguage, whitespace} from 'parsimmon';
import {
  alternative,
  GrammarElement,
  optional,
  repetitionAny,
  repetitionOne,
  sequence,
  terminal,
  Terminal,
  UnaryOperator,
  variable,
  Variable
} from './ebnfGrammar';


interface GrammarElementLanguage {
  terminal: Terminal;
  variable: Variable;

  singleElement: Terminal | Variable;

  unaryChildElement: GrammarElement;

  unaryOperator: UnaryOperator;
  unaryElement: GrammarElement;

  alternativeChildElement: GrammarElement;

  alternative: GrammarElement;

  sequence: GrammarElement;

  grammarElement: GrammarElement;
}

export const ebnfGrammarLanguage: TypedLanguage<GrammarElementLanguage> = createLanguage<GrammarElementLanguage>({
  terminal: () => regexp(/'(\w+)'/, 1).map(terminal),
  variable: () => regexp(/[A-Z]+/).map(variable),
  singleElement: (r) => alt(r.terminal, r.variable),

  unaryChildElement: (r) => alt(
    r.singleElement,
    seq(string('('), optWhitespace, r.grammarElement, optWhitespace, string(')'))
      .map(([/*op*/, /*ws1*/, el, /*ws2*/, /*cp*/]) => el)
  ),

  unaryOperator: () => alt(string('?'), string('*'), string('+')),
  unaryElement: (r) => seq(r.unaryChildElement, r.unaryOperator)
    .map(([child, operator]) => {
      if (operator === '*') {
        return repetitionAny(child);
      } else if (operator === '+') {
        return repetitionOne(child);
      } else /* if (operator === '?') */{
        return optional(child);
      }
    }),

  alternativeChildElement: (r) => alt(
    r.unaryElement,
    r.singleElement,
    seq(string('('), optWhitespace, r.grammarElement, optWhitespace, string(')'))
      .map(([/*op*/, /*ws1*/, el, /*ws2*/, /*cp*/]) => el)
  ),

  alternative: (r) => sepBy1(r.alternativeChildElement, seq(optWhitespace, string('|'), optWhitespace))
    .map((children) => children.length === 1 ? children[0] : alternative(children)),

  sequence: (r) => sepBy(r.alternative, whitespace)
    .map((children) => children.length === 1 ? children[0] : sequence(children)),

  grammarElement: (r) => alt(
    r.sequence,
    r.alternative,
    r.terminal,
    r.singleElement
  )
});

export function parseEbnfGrammarRight(value: string): Result<GrammarElement> {
  return ebnfGrammarLanguage.grammarElement.parse(value.trim());
}

export function tryParseEbnfGrammarRight(input: string): GrammarElement {
  return ebnfGrammarLanguage.grammarElement.tryParse(input.trim());
}
