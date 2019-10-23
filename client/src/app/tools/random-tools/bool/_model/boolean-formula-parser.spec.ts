import {parseBooleanFormula} from './boolean-formula-parser';
import {BooleanAnd, BooleanFalse, BooleanNAnd, BooleanNOr, BooleanOr, BooleanTrue, BooleanVariable, BooleanXOr} from './bool-node';

describe('BooleanFormulaParser', () => {
  const a: BooleanVariable = new BooleanVariable('a');
  const b: BooleanVariable = new BooleanVariable('b');

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
    expect(parseBooleanFormula(aAndB.asString())).toEqual(aAndB);
    expect(parseBooleanFormula(aOrB.asString())).toEqual(aOrB);
    expect(parseBooleanFormula(aXOrB.asString())).toEqual(aXOrB);
    expect(parseBooleanFormula(aNOrB.asString())).toEqual(aNOrB);
    expect(parseBooleanFormula(aNAndB.asString())).toEqual(aNAndB);

  });
});
