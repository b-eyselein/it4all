// Boolean nodes
import {flatMapArray, randomInt, takeRandom} from '../../../helpers';


export abstract class BooleanNode {
  abstract evaluate(assignments: Map<string, boolean>): boolean | undefined;

  abstract getVariables(): BooleanVariable[];

  abstract asString(): string;
}

export class BooleanVariable extends BooleanNode {
  constructor(readonly variable: string) {
    super();
  }

  evaluate(assignments: Map<string, boolean>): boolean | undefined {
    if (assignments.has(this.variable)) {
      return assignments.get(this.variable);
    } else {
      return undefined;
    }
  }

  getVariables(): BooleanVariable[] {
    return [this];
  }

  asString(): string {
    return this.variable;
  }

}

export class BooleanConstant extends BooleanNode {
  constructor(readonly value: boolean) {
    super();
  }

  evaluate(assignments: Map<string, boolean>): boolean | undefined {
    return this.value;
  }

  getVariables(): BooleanVariable[] {
    return [];
  }

  asString(): string {
    return this.value ? '1' : '0';
  }
}

export const BooleanTrue: BooleanConstant = new BooleanConstant(true);

export const BooleanFalse: BooleanConstant = new BooleanConstant(false);

export class BooleanNot extends BooleanNode {
  constructor(readonly child: BooleanNode) {
    super();
  }

  evaluate(assignments: Map<string, boolean>): boolean | undefined {
    return !(this.child.evaluate(assignments));
  }

  getVariables(): BooleanVariable[] {
    return this.child.getVariables();
  }

  asString(): string {
    const childString = this.child instanceof BooleanBinaryNode ? '(' + this.child.asString() + ')' : this.child.asString();
    return 'not ' + childString;
  }
}

// Boolean binary nodes

export abstract class BooleanBinaryNode extends BooleanNode {

  protected abstract operator: string;
  protected abstract evalFunc: (a: boolean, b: boolean) => boolean;

  constructor(readonly left: BooleanNode, readonly right: BooleanNode) {
    super();
  }

  getVariables(): BooleanVariable[] {
    const leftVars: BooleanVariable[] = this.left.getVariables();
    const rightVars: BooleanVariable[] = this.right.getVariables();

    return Array.from(new Set(leftVars.concat(rightVars)));
  }

  asString(): string {
    // FIXME: test parentheses!
    const leftChildString: string = this.left instanceof BooleanBinaryNode ? `(${this.left.asString()})` : this.left.asString();
    const rightChildString: string = this.right instanceof BooleanBinaryNode ? `(${this.right.asString()})` : this.right.asString();

    return leftChildString + ' ' + this.operator + ' ' + rightChildString;
  }

  evaluate(assignments: Map<string, boolean>): boolean | undefined {
    return this.evalFunc(this.left.evaluate(assignments), this.right.evaluate(assignments));
  }
}

export class BooleanAnd extends BooleanBinaryNode {
  protected operator = 'and';
  protected evalFunc = (a, b) => a && b;
}

export class BooleanOr extends BooleanBinaryNode {
  protected operator = 'or';
  protected evalFunc = (a, b) => a || b;
}

export class BooleanNAnd extends BooleanBinaryNode {
  protected operator = 'nand';
  protected evalFunc = (a, b) => !(a && b);
}

export class BooleanNOr extends BooleanBinaryNode {
  protected operator = 'nor';
  protected evalFunc = (a, b) => !(a || b);
}

export class BooleanXOr extends BooleanBinaryNode {
  protected operator = 'xor';
  protected evalFunc = (a, b) => (a && !b) || (!a && b);
}

export class BooleanEquivalency extends BooleanBinaryNode {
  protected operator = 'equiv';
  protected evalFunc = (a, b) => a === b;
}

export class BooleanImplication extends BooleanBinaryNode {
  protected operator = 'impl';
  protected evalFunc = (a, b) => !a || b;
}

// Helper functions

export function and(left: BooleanNode, right: BooleanNode): BooleanAnd {
  return new BooleanAnd(left, right);
}

// Boolean Formulas

const HTML_REPLACERS: Map<RegExp, string> = new Map([
  [/impl/g, '&rArr;'],
  [/nor/g, '&#x22bd;'],
  [/nand/g, '&#x22bc;'],
  [/equiv/g, '&hArr;'],
  [/not /g, '&not;'],
  [/and/g, '&and;'],
  [/xor/g, '&oplus;'],
  [/or/g, '&or;']
]);

export class BooleanFormula {
   variables: BooleanVariable[];
   assignments: Map<string, boolean>[];

  constructor(readonly rootNode: BooleanNode) {
    this.variables = this.rootNode.getVariables().sort((v1, v2) => v1.variable.charCodeAt(0) - v2.variable.charCodeAt(0));
    this.assignments = this.calculateAssignments();
  }

   calculateAssignments(): Map<string, boolean>[] {
    let assignments: Map<string, boolean>[] = [];

    for (const variable of this.variables) {

      if (assignments.length === 0) {
        assignments = [
          new Map([[variable.variable, false]]),
          new Map([[variable.variable, true]])
        ];
      } else {
        assignments = flatMapArray(
          assignments,
          (assignment: Map<string, boolean>) => [
            new Map([...assignment, [variable.variable, false]]),
            new Map([...assignment, [variable.variable, true]])
          ]
        );
      }
    }

    return assignments;
  }

  getVariables() {
    return this.variables;
  }

  getAllAssignments(): Map<string, boolean>[] {
    return this.assignments;
  }

  asString(): string {
    return this.rootNode.asString();
  }

  asHtmlString(): string {
    let base: string = this.rootNode.asString();

    HTML_REPLACERS.forEach((replacer, replaced) => base = base.replace(replaced, replacer));

    return base;
  }
}

const varA: BooleanVariable = new BooleanVariable('a');
const varB: BooleanVariable = new BooleanVariable('b');
const varC: BooleanVariable = new BooleanVariable('c');


function generateRandomOperator(left: BooleanNode, right: BooleanNode): BooleanNode {
  const leftNegated: boolean = randomInt(0, 3) === 2;
  const rightNegated: boolean = randomInt(0, 3) === 2;

  const leftChild = leftNegated ? new BooleanNot(left) : left;
  const rightChild = rightNegated ? new BooleanNot(right) : right;

  if (randomInt(0, 2) === 1) {
    return new BooleanAnd(leftChild, rightChild);
  } else {
    return new BooleanOr(leftChild, rightChild);
  }

}

export function generateBooleanFormula(): BooleanFormula {
  const depth: number = randomInt(1, 3);

  if (depth === 1) {
    return new BooleanFormula(generateRandomOperator(varA, varB));
  } else {
    const vars: BooleanVariable[] = [varA, varB, varC];

    const leftChild = generateRandomOperator(takeRandom(vars), takeRandom(vars));
    const rightChild = generateRandomOperator(takeRandom(vars), takeRandom(vars));

    return new BooleanFormula(generateRandomOperator(leftChild, rightChild));
  }
}

// Boolean Result

export interface BooleanCreateSolution {
  formula: string;
  rows: {
    assignments: {
      variable: string;
      value: boolean;
    }[];
  }[];
}

export interface BooleanCreateResult {
  success: string;
  assignments: {
    id: string;
    learnerVar: boolean;
    correct: boolean;
  }[];
  knf: string;
  dnf: string;
}

