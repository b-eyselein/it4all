import {
  and,
  booleanVariable,
  BooleanVariable,
  equiv,
  evaluate,
  getSubNodes,
  getVariables,
  impl,
  nand,
  NewBooleanNode,
  nor,
  not,
  or,
  stringify,
  xor
} from './bool-node';
import {Assignment} from './assignment';

export function randomInt(minInclusive: number, maxExclusive: number): number {
  const intMax = Math.floor(maxExclusive);
  const intMin = Math.floor(minInclusive);
  return Math.floor(Math.random() * (intMax - intMin)) + intMin;
}

function takeRandom<T>(from: T[]): T {
  return from[Math.floor(Math.random() * from.length)];
}


function generateRandomOperator(left: NewBooleanNode, right: NewBooleanNode): NewBooleanNode {
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

export class BooleanFormula {

  constructor(public left: BooleanVariable, public right: NewBooleanNode) {
  }

  getVariables(): BooleanVariable[] {
    return getVariables(this.right);
  }

  getSubFormulas(): NewBooleanNode[] {
    return getSubNodes(this.right);
  }

  asString(): string {
    return this.left.variable + ' = ' + stringify(this.right);
  }

  evaluate(assignments: Assignment): boolean {
    return evaluate(this.right, assignments);
  }
}


const varA: BooleanVariable = booleanVariable('a');
const varB: BooleanVariable = booleanVariable('b');
const varC: BooleanVariable = booleanVariable('c');

export function generateBooleanFormula(left: BooleanVariable): BooleanFormula {
  const depth: number = randomInt(1, 3);

  if (depth === 1) {
    return new BooleanFormula(left, generateRandomOperator(varA, varB));
  } else {
    const vars: BooleanVariable[] = [varA, varB, varC];

    const leftChild = generateRandomOperator(takeRandom(vars), takeRandom(vars));
    const rightChild = generateRandomOperator(takeRandom(vars), takeRandom(vars));

    return new BooleanFormula(left, generateRandomOperator(leftChild, rightChild));
  }
}
