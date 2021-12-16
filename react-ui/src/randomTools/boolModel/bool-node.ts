const HTML_REPLACERS: { [key: string]: RegExp } = {
  '&#x22bc;': /nand/g, '&#x22bd;': /nor/g, '&oplus;': /xor/g, '&not;': /not/g,
  '&and;': /and/g, '&or;': /or/g, '&rArr;': /impl/g, '&hArr;': /equiv/g
};

export type Assignment = { [key: string]: boolean };

export function calculateAssignments(variables: BooleanVariable[]): Assignment[] {
  let assignments: Assignment[] = [];

  for (const variable of variables) {
    if (assignments.length === 0) {
      assignments = [
        {[variable.variable]: false},
        {[variable.variable]: true}
      ];
    } else {
      assignments = assignments.flatMap(
        (assignment: Assignment) => [
          {...assignment, [variable.variable]: false},
          {...assignment, [variable.variable]: true}
        ]
      );
    }
  }

  return assignments;
}

// Boolean nodes
export abstract class BooleanNode {

  private variables: BooleanVariable[] | undefined;

  getVariables(): BooleanVariable[] {
    if (!this.variables) {
      this.variables = this.calculateVariables()
        .sort((v1, v2) => v1.variable.charCodeAt(0) - v2.variable.charCodeAt(0));
    }

    return this.variables;
  }

  protected abstract calculateVariables(): BooleanVariable[];

  abstract getSubFormulas(): BooleanNode[];

  abstract evaluate(assignments: Assignment): boolean ;

  abstract asString(): string;

  asHtmlString(): string {
    return Object.entries(HTML_REPLACERS).reduce((acc, [replacement, toReplace]) => acc.replace(toReplace, replacement), this.asString());
  }

}

export class BooleanVariable extends BooleanNode {
  constructor(readonly variable: string) {
    super();
  }

  evaluate(assignments: Assignment): boolean {
    return assignments[this.variable] || false;
  }

  protected calculateVariables(): BooleanVariable[] {
    return [this];
  }

  asString(): string {
    return this.variable;
  }

  getSubFormulas(): BooleanNode[] {
    return [];
  }

}

export class BooleanConstant extends BooleanNode {
  constructor(readonly value: boolean) {
    super();
  }

  evaluate(/*assignments: Assignment*/): boolean {
    return this.value || false;
  }

  protected calculateVariables(): BooleanVariable[] {
    return [];
  }

  asString(): string {
    return this.value ? '1' : '0';
  }

  getSubFormulas(): BooleanNode[] {
    return [];
  }

}

export const BooleanTrue: BooleanConstant = new BooleanConstant(true);

export const BooleanFalse: BooleanConstant = new BooleanConstant(false);

export class BooleanNot extends BooleanNode {
  constructor(readonly child: BooleanNode) {
    super();
  }

  evaluate(assignments: Assignment): boolean {
    return !(this.child.evaluate(assignments));
  }

  protected calculateVariables(): BooleanVariable[] {
    return this.child.getVariables();
  }

  asString(): string {
    const childString = this.child instanceof BooleanBinaryNode ? '(' + this.child.asString() + ')' : this.child.asString();
    return 'not ' + childString;
  }

  getSubFormulas(): BooleanNode[] {
    return [this.child];
  }

}

export function not(c: BooleanNode): BooleanNot {
  return new BooleanNot(c);
}


// Boolean binary nodes

export abstract class BooleanBinaryNode extends BooleanNode {

  protected abstract operator: string;


  constructor(readonly left: BooleanNode, readonly right: BooleanNode) {
    super();
  }

  protected calculateVariables(): BooleanVariable[] {
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

  evaluate(assignments: Assignment): boolean {
    return this.evalFunc(this.left.evaluate(assignments), this.right.evaluate(assignments));
  }

  getSubFormulas(): BooleanNode[] {
    const maybeLeftSubFormula = (this.left instanceof BooleanVariable) ? [] : [this.left];
    const maybeRightSubFormula = (this.right instanceof BooleanVariable) ? [] : [this.right];

    return [...maybeLeftSubFormula, ...maybeRightSubFormula];
  }

  protected abstract evalFunc(a: boolean, b: boolean): boolean;
}

export class BooleanAnd extends BooleanBinaryNode {
  protected operator = 'and';

  protected evalFunc(a: boolean, b: boolean): boolean {
    return a && b;
  }
}

export function and(a: BooleanNode, b: BooleanNode): BooleanAnd {
  return new BooleanAnd(a, b);
}


export class BooleanOr extends BooleanBinaryNode {
  protected operator = 'or';

  protected evalFunc(a: boolean, b: boolean): boolean {
    return a || b;
  }
}

export function or(a: BooleanNode, b: BooleanNode): BooleanOr {
  return new BooleanOr(a, b);
}


export class BooleanNAnd extends BooleanBinaryNode {
  protected operator = 'nand';

  protected evalFunc(a: boolean, b: boolean): boolean {
    return !(a && b);
  }
}

export function nand(a: BooleanNode, b: BooleanNode): BooleanNAnd {
  return new BooleanNAnd(a, b);
}


export class BooleanNOr extends BooleanBinaryNode {
  protected operator = 'nor';

  protected evalFunc(a: boolean, b: boolean): boolean {
    return !(a || b);
  }
}

export function nor(a: BooleanNode, b: BooleanNode): BooleanNOr {
  return new BooleanNOr(a, b);
}


export class BooleanXOr extends BooleanBinaryNode {
  protected operator = 'xor';

  protected evalFunc(a: boolean, b: boolean): boolean {
    return (a && !b) || (!a && b);
  }
}

export function xor(a: BooleanNode, b: BooleanNode): BooleanXOr {
  return new BooleanXOr(a, b);
}


export class BooleanXNor extends BooleanBinaryNode {
  protected operator = 'xnor';

  protected evalFunc(a: boolean, b: boolean): boolean {
    return (!a || b) && (a || !b);
  }
}

export function xnor(a: BooleanNode, b: BooleanNode): BooleanXNor {
  return new BooleanXNor(a, b);
}


export class BooleanEquivalency extends BooleanBinaryNode {
  protected operator = 'equiv';

  protected evalFunc(a: boolean, b: boolean): boolean {
    return a === b;
  }
}

export function equiv(a: BooleanNode, b: BooleanNode): BooleanEquivalency {
  return new BooleanEquivalency(a, b);
}


export class BooleanImplication extends BooleanBinaryNode {
  protected operator = 'impl';

  protected evalFunc(a: boolean, b: boolean): boolean {
    return !a || b;
  }
}

export function impl(a: BooleanNode, b: BooleanNode): BooleanImplication {
  return new BooleanImplication(a, b);
}


export function instantiateOperator(leftOp: BooleanNode, opString: string, rightOp: BooleanNode): BooleanBinaryNode {
  switch (opString) {
    case 'or':
      return new BooleanOr(leftOp, rightOp);
    case 'xor':
      return new BooleanXOr(leftOp, rightOp);
    case 'nor':
      return new BooleanNOr(leftOp, rightOp);
    case 'xnor':
      return new BooleanXNor(leftOp, rightOp);
    case 'nand':
      return new BooleanNAnd(leftOp, rightOp);
    case 'equiv':
      return new BooleanEquivalency(leftOp, rightOp);
    case 'impl':
      return new BooleanImplication(leftOp, rightOp);
    case 'and':
    default:
      return new BooleanAnd(leftOp, rightOp);
  }
}