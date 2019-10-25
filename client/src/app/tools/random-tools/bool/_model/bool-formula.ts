import {randomInt, takeRandom} from '../../../../helpers';
import {
  BooleanAnd,
  BooleanEquivalency,
  BooleanImplication,
  BooleanNAnd,
  BooleanNode,
  BooleanNOr,
  BooleanNot,
  BooleanOr,
  BooleanVariable,
  BooleanXOr
} from './bool-node';

const varA: BooleanVariable = new BooleanVariable('a');
const varB: BooleanVariable = new BooleanVariable('b');
const varC: BooleanVariable = new BooleanVariable('c');


function generateRandomOperator(left: BooleanNode, right: BooleanNode): BooleanNode {
  const leftNegated: boolean = randomInt(0, 3) === 2;
  const rightNegated: boolean = randomInt(0, 3) === 2;

  const leftChild = leftNegated ? new BooleanNot(left) : left;
  const rightChild = rightNegated ? new BooleanNot(right) : right;

  const operatorInt: number = randomInt(0, 20);

  if (0 <= operatorInt && operatorInt < 8) {
    return new BooleanAnd(leftChild, rightChild);
  } else if (8 <= operatorInt && operatorInt < 16) {
    return new BooleanOr(leftChild, rightChild);
  } else if (operatorInt === 16) {
    return new BooleanXOr(leftChild, rightChild);
  } else if (operatorInt === 17) {
    return new BooleanNAnd(leftChild, rightChild);
  } else if (operatorInt === 18) {
    return new BooleanNOr(leftChild, rightChild);
  } else if (operatorInt === 19) {
    return new BooleanEquivalency(leftChild, rightChild);
  } else {
    return new BooleanImplication(leftChild, rightChild);
  }
}

export function generateBooleanFormula(): BooleanNode {
  const depth: number = randomInt(1, 3);

  if (depth === 1) {
    return generateRandomOperator(varA, varB);
  } else {
    const vars: BooleanVariable[] = [varA, varB, varC];

    const leftChild = generateRandomOperator(takeRandom(vars), takeRandom(vars));
    const rightChild = generateRandomOperator(takeRandom(vars), takeRandom(vars));

    return generateRandomOperator(leftChild, rightChild);
  }
}
