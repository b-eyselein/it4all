import {and, booleanVariable, BooleanVariable, equiv, impl, nand, BooleanNode, nor, not, or, xor} from './boolNode';

const varA: BooleanVariable = booleanVariable('a');
const varB: BooleanVariable = booleanVariable('b');
const varC: BooleanVariable = booleanVariable('c');

const vars: BooleanVariable[] = [varA, varB, varC];

export function randomInt(minInclusive: number, maxExclusive: number): number {
  const intMax = Math.floor(maxExclusive);
  const intMin = Math.floor(minInclusive);
  return Math.floor(Math.random() * (intMax - intMin)) + intMin;
}

function takeRandom<T>(from: T[]): T {
  return from[Math.floor(Math.random() * from.length)];
}


function generateRandomOperator(left: BooleanNode, right: BooleanNode): BooleanNode {
  const leftNegated: boolean = randomInt(0, 3) === 2;
  const rightNegated: boolean = randomInt(0, 3) === 2;

  const leftChild = leftNegated ? not(left) : left;
  const rightChild = rightNegated ? not(right) : right;

  const operatorInt: number = randomInt(0, 20);

  if (0 <= operatorInt && operatorInt < 8) {
    return and(leftChild, rightChild);
  } else if (8 <= operatorInt && operatorInt < 16) {
    return or(leftChild, rightChild);
  } else if (operatorInt === 16) {
    return xor(leftChild, rightChild);
  } else if (operatorInt === 17) {
    return nand(leftChild, rightChild);
  } else if (operatorInt === 18) {
    return nor(leftChild, rightChild);
  } else if (operatorInt === 19) {
    return equiv(leftChild, rightChild);
  } else {
    return impl(leftChild, rightChild);
  }
}


export function generateBooleanFormula(): BooleanNode {
  const depth: number = randomInt(1, 3);

  if (depth === 1) {
    return generateRandomOperator(varA, varB);
  } else {
    return generateRandomOperator(
      generateRandomOperator(takeRandom(vars), takeRandom(vars)),
      generateRandomOperator(takeRandom(vars), takeRandom(vars))
    );
  }
}
