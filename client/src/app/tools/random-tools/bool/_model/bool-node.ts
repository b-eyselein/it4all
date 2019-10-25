import {flatMapArray} from '../../../../helpers';

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

export function calculateAssignments(variables: BooleanVariable[]): Map<string, boolean>[] {
  let assignments: Map<string, boolean>[] = [];

  for (const variable of variables) {

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

  abstract evaluate(assignments: Map<string, boolean>): boolean | undefined;

  protected abstract calculateVariables(): BooleanVariable[];

  abstract asString(): string;

  abstract getSubFormulas(): BooleanNode[];

  asHtmlString(): string {
    let base: string = this.asString();

    HTML_REPLACERS.forEach((replacer, replaced) => base = base.replace(replaced, replacer));

    return base;
  }

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

  evaluate(assignments: Map<string, boolean>): boolean | undefined {
    return this.value;
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

  evaluate(assignments: Map<string, boolean>): boolean | undefined {
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

// Boolean binary nodes

export abstract class BooleanBinaryNode extends BooleanNode {

  protected abstract operator: string;
  protected abstract evalFunc: (a: boolean, b: boolean) => boolean;

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

  evaluate(assignments: Map<string, boolean>): boolean | undefined {
    return this.evalFunc(this.left.evaluate(assignments), this.right.evaluate(assignments));
  }

  getSubFormulas(): BooleanNode[] {
    const maybeLeftSubFormula = (this.left instanceof BooleanVariable) ? [] : [this.left];
    const maybeRightSubFormula = (this.right instanceof BooleanVariable) ? [] : [this.right];

    return maybeLeftSubFormula.concat(maybeRightSubFormula);

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
