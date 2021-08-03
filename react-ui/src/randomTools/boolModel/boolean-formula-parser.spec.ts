import {and, BooleanBinaryNode, BooleanFalse, BooleanOr, BooleanTrue, BooleanVariable, equiv, impl, nand, nor, not, or, xnor, xor} from './bool-node';
import {tryParseBooleanFormulaFromLanguage} from './boolean-formula-parser';

const a = new BooleanVariable('a');
const b = new BooleanVariable('b');
const c = new BooleanVariable('c');
const d = new BooleanVariable('d');

describe('Boolean Formula Language', () => {

  const formula1 = '(a and b and not c) or (not a and b and not c)';
  const formula1Expected: BooleanOr = or(
    and(and(a, b), not(c)),
    and(and(not(a), b), not(c))
  );

  const formula2 = '(a and b and not c) or (not a and b and not c) or (a and not b and c)';
  const formula2Expected: BooleanOr = or(
    or(
      and(and(a, b), not(c)),
      and(and(not(a), b), not(c))
    ),
    and(and(a, not(b)), c)
  );

  const formula3 = '(a equiv (b nand c)) xor (c impl (b xor b))';
  const formula3Expected: BooleanBinaryNode = xor(equiv(a, nand(b, c)), impl(c, xor(b, b)));

  const formula4 = '(a equiv b nand c) xor (c impl b nand c xor b)';
  const formula4Expected = xor(nand(equiv(a, b), c), xor(nand(impl(c, b), c), b));

  test.each`
  toParse    | expected
  ${'true'}  | ${BooleanTrue}
  ${'1'}     | ${BooleanTrue}
  ${'false'} | ${BooleanFalse}
  ${'0'}     | ${BooleanFalse}
  ${'a'}     | ${a}
  ${'b'}     | ${b}
  ${'not a'} | ${not(a)}
  ${'a and b'} | ${and(a, b)}
  ${'a or b'}  | ${or(a, b)}
  ${'a xor b'} | ${xor(a, b)}
  ${'a nor b'} | ${nor(a, b)}
  ${'a xnor b'} | ${xnor(a, b)}
  ${'a nand b'} | ${nand(a, b)}
  ${'a equiv b'} | ${equiv(a, b)}
  ${'a impl b'} | ${impl(a, b)}
  ${'a and b and c'} | ${and(and(a, b), c)}
  ${'a or b or c or d'} | ${or(or(or(a, b), c), d)} 
  ${formula1} | ${formula1Expected}
  ${formula2} | ${formula2Expected}
  ${formula3} | ${formula3Expected}
  ${formula4} | ${formula4Expected}
  `(
    'it should parse $toParse as $expected',
    ({toParse, expected}) => expect(tryParseBooleanFormulaFromLanguage(toParse)).toEqual(expected));
});
