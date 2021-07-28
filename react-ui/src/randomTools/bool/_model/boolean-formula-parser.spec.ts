import {parseBooleanFormula} from './boolean-formula-parser';
import {
  BooleanAnd,
  BooleanBinaryNode,
  BooleanEquivalency,
  BooleanFalse,
  BooleanImplication,
  BooleanNAnd,
  BooleanNode,
  BooleanNOr,
  BooleanNot,
  BooleanOr,
  BooleanTrue,
  BooleanVariable,
  BooleanXOr
} from './bool-node';

function not(n: BooleanNode): BooleanNot {
  return new BooleanNot(n);
}

function and(l: BooleanNode, r: BooleanNode): BooleanAnd {
  return new BooleanAnd(l, r);
}

function or(l: BooleanNode, r: BooleanNode): BooleanOr {
  return new BooleanOr(l, r);
}

function testParse(formula: BooleanNode) {
  testParseString(formula.asString(), formula);
}

function testParseString(formulaString: string, awaited: BooleanNode) {
  const parsed: BooleanNode | undefined = parseBooleanFormula(formulaString);
  expect(parsed).toEqual(awaited);
}

describe('BooleanFormulaParser', () => {
  const a: BooleanVariable = new BooleanVariable('a');
  const b: BooleanVariable = new BooleanVariable('b');
  const c: BooleanVariable = new BooleanVariable('c');

  const aAndB: BooleanAnd = new BooleanAnd(a, b);
  const aOrB: BooleanOr = new BooleanOr(a, b);

  const aXOrB: BooleanXOr = new BooleanXOr(a, b);
  const aNOrB: BooleanNOr = new BooleanNOr(a, b);
  const aNAndB: BooleanNAnd = new BooleanNAnd(a, b);

  it('should parse simple values', () => {
    expect(parseBooleanFormula(BooleanTrue.asString())).toBe(BooleanTrue);
    expect(parseBooleanFormula(BooleanFalse.asString())).toBe(BooleanFalse);

    expect(parseBooleanFormula(a.asString())).toEqual(a);
    expect(parseBooleanFormula(b.asString())).toEqual(b);
  });

  it('should parse simple binary formulas', () => {
    testParse(aAndB);
    testParse(aOrB);
    testParse(aXOrB);
    testParse(aNOrB);
    testParse(aNAndB);
  });

  it('should parse advanced binary formulas', () => {
    testParseString('a and b and c', and(a, and(b, c)));

    const f1: BooleanOr = or(
      and(a, and(b, not(c))),
      and(not(a), and(b, not(c)))
    );
    testParse(f1);
    testParseString('(a and b and not c) or (not a and b and not c)', f1);

    const f2: BooleanOr = or(
      and(a, and(b, not(c))),
      or(
        and(not(a), and(b, not(c))),
        and(a, and(not(b), c))
      )
    );
    testParse(f2);
    testParseString('(a and b and not c) or (not a and b and not c) or (a and not b and c)', f2);

    const f3: BooleanBinaryNode = new BooleanXOr(
      new BooleanEquivalency(a, new BooleanNAnd(b, c)),
      new BooleanImplication(c, new BooleanXOr(b, b))
    );
    testParse(f3);
    testParseString('(a equiv (b nand c)) xor (c impl (b xor b))', f3);


    testParseString(
      '(a equiv b nand c) xor (c impl b nand c xor b)', new BooleanXOr(
        new BooleanNAnd(
          new BooleanEquivalency(a, b), c
        ),
        new BooleanXOr(
          new BooleanNAnd(
            new BooleanImplication(c, b), c
          ), b
        )
      ));
  });

});
