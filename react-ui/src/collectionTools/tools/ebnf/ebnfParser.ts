import {alt, createLanguage, optWhitespace, regexp, Result, sepBy, sepBy1, seq, string, TypedLanguage, whitespace} from 'parsimmon';
import {
  alternative,
  ExtendedBackusNaurFormGrammarElement,
  optional,
  repetitionAny,
  repetitionOne,
  sequence,
  terminal,
  Terminal,
  UnaryOperator,
  variable,
  Variable
} from './ebnfElements';


interface GrammarElementLanguage {
  terminal: Terminal;
  variable: Variable;

  singleElement: Terminal | Variable;

  unaryChildElement: ExtendedBackusNaurFormGrammarElement;

  unaryOperator: UnaryOperator;
  unaryElement: ExtendedBackusNaurFormGrammarElement;

  alternativeChildElement: ExtendedBackusNaurFormGrammarElement;

  alternative: ExtendedBackusNaurFormGrammarElement;

  sequence: ExtendedBackusNaurFormGrammarElement;

  grammarElement: ExtendedBackusNaurFormGrammarElement;
}

export const ebnfGrammarLanguage: TypedLanguage<GrammarElementLanguage> = createLanguage<GrammarElementLanguage>({
  terminal: () => regexp(/'(\w+)'/, 1).map((s) => terminal(s)),
  variable: () => regexp(/[A-Z]+/).map((s) => variable(s)),
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
    .map(([first, ...rest]) => rest.length === 0 ? first : alternative(first, ...rest)),

  sequence: (r) => sepBy(r.alternative, whitespace)
    .map(([first, ...rest]) => rest.length === 0 ? first : sequence(first, ...rest)),

  grammarElement: (r) => alt(
    r.sequence,
    r.alternative,
    r.terminal,
    r.singleElement
  )
});

export function parseEbnfGrammarRight(value: string): Result<ExtendedBackusNaurFormGrammarElement> {
  return ebnfGrammarLanguage.grammarElement.parse(value.trim());
}

export function tryParseEbnfGrammarRight(input: string): ExtendedBackusNaurFormGrammarElement {
  return ebnfGrammarLanguage.grammarElement.tryParse(input.trim());
}
