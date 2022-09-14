import {alt, createLanguage, optWhitespace, Parser, regexp, Result, sepBy1, seq, string, whitespace} from 'parsimmon';
import {
  and,
  BooleanBinaryNode,
  BooleanFalse,
  BooleanTrue,
  booleanVariable,
  BooleanVariable,
  equiv,
  impl,
  nand,
  NewBooleanNode,
  nor,
  not,
  or,
  xor
} from './bool-node';

type OtherOps = 'xor' | 'nor' | 'nand' | 'equiv' | 'impl';

interface BooleanFormulaParser {
  true: typeof BooleanTrue;
  false: typeof BooleanFalse;
  variable: BooleanVariable;
  operators: OtherOps;
  factor: NewBooleanNode;
  notFactor: NewBooleanNode;
  binaryTermComponent: NewBooleanNode;
  andTermComponent: NewBooleanNode;
  orTermComponent: NewBooleanNode;
  booleanFormula: NewBooleanNode;
}

export function instantiateOperator(leftOp: NewBooleanNode, opString: OtherOps, rightOp: NewBooleanNode): BooleanBinaryNode {
  switch (opString) {
    case 'xor':
      return xor(leftOp, rightOp);
    case 'nor':
      return nor(leftOp, rightOp);
    case 'nand':
      return nand(leftOp, rightOp);
    case 'equiv':
      return equiv(leftOp, rightOp);
    case 'impl':
      return impl(leftOp, rightOp);
  }
}

const boolFormulaLanguage = createLanguage<BooleanFormulaParser>({
  true: () => alt(string('1'), regexp(/true/i)).result(BooleanTrue),
  false: () => alt(string('0'), regexp(/false/i)).result(BooleanFalse),

  variable: () => regexp(/[a-z]/).map((variable) => booleanVariable(variable)),

  operators: () => alt(string('xor'), string('nor'), string('nand'), string('equiv'), string('impl')),

  factor: (r) => alt(
    r.booleanFormula.wrap(seq(string('('), optWhitespace), seq(optWhitespace, string(')'))),
    r.true, r.false,
    r.variable,
  ),

  notFactor: (r) => seq(string('not'), whitespace, r.factor).map(([, , child]) => not(child)),

  binaryTermComponent: (r) => alt(r.notFactor, r.factor),

  andTermComponent: (r) => {
    const x: Parser<[string, OtherOps, string, NewBooleanNode][]> = seq(whitespace, r.operators, whitespace, r.binaryTermComponent).many();

    return seq(r.binaryTermComponent, x)
      .map(([first, rest]) => rest.reduce((acc, [, op, , cur]) => instantiateOperator(acc, op, cur), first));
  },

  orTermComponent: (r) => sepBy1(r.andTermComponent, seq(whitespace, string('and'), whitespace))
    .map(([first, ...rest]) => rest.reduce((acc, cur) => and(acc, cur), first)),

  booleanFormula: (r) =>
    sepBy1(r.orTermComponent, seq(whitespace, string('or'), whitespace))
      .map(([first, ...rest]) => rest.reduce((acc, cur) => or(acc, cur), first))
});

export function parseBooleanFormulaFromLanguage(formula: string): Result<NewBooleanNode> {
  return boolFormulaLanguage.booleanFormula.parse(formula);
}
