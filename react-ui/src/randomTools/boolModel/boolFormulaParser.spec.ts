import {and, BooleanFalse, BooleanTrue, booleanVariable, equiv, impl, nand, BooleanNode, nor, not, or, xor} from './boolNode';
import {parseBooleanFormulaFromLanguage} from './boolFormulaParser';
import {Result} from 'parsimmon';

const a = booleanVariable('a');
const b = booleanVariable('b');
const c = booleanVariable('c');
const d = booleanVariable('d');

describe('Boolean Formula Language', () => {

  const formula1 = '(a and b and not c) or (not a and b and not c)';
  const formula1Expected = or(
    and(and(a, b), not(c)),
    and(and(not(a), b), not(c))
  );

  const formula2 = '(a and b and not c) or (not a and b and not c) or (a and not b and c)';
  const formula2Expected = or(
    or(
      and(and(a, b), not(c)),
      and(and(not(a), b), not(c))
    ),
    and(and(a, not(b)), c)
  );

  const formula3 = '(a equiv (b nand c)) xor (c impl (b xor b))';
  const formula3Expected = xor(equiv(a, nand(b, c)), impl(c, xor(b, b)));

  const formula4 = '(a equiv b nand c) xor (c impl b nand c xor b)';
  const formula4Expected = xor(nand(equiv(a, b), c), xor(nand(impl(c, b), c), b));

  test.each<{ toParse: string, expected: BooleanNode }>([
    // Constants
    {toParse: 'true', expected: BooleanTrue},
    {toParse: '1', expected: BooleanTrue},
    {toParse: 'false', expected: BooleanFalse},
    {toParse: '0', expected: BooleanFalse},
    // Variables
    {toParse: 'a', expected: a},
    {toParse: 'b', expected: b},
    // Not
    {toParse: 'not a', expected: not(a)},
    // Binary ops
    {toParse: 'a and b', expected: and(a, b)},
    {toParse: 'a or b', expected: or(a, b)},
    {toParse: 'a xor b', expected: xor(a, b)},
    {toParse: 'a nor b', expected: nor(a, b)},
    {toParse: 'a nand b', expected: nand(a, b)},
    {toParse: 'a equiv b', expected: equiv(a, b)},
    {toParse: 'a impl b', expected: impl(a, b)},
    // Complex cases
    {toParse: 'a and b and c', expected: and(and(a, b), c)},
    {toParse: 'a or b or c or d', expected: or(or(or(a, b), c), d)},
    {toParse: formula1, expected: formula1Expected},
    {toParse: formula2, expected: formula2Expected},
    {toParse: formula3, expected: formula3Expected},
    {toParse: formula4, expected: formula4Expected}
  ])(
    'it should parse $toParse as $expected',
    ({toParse, expected}) => expect(parseBooleanFormulaFromLanguage(toParse)).toEqual<Result<BooleanNode>>({status: true, value: expected}));
});
