import {
  BooleanAnd,
  BooleanEquivalency, BooleanFalse,
  BooleanImplication,
  BooleanNAnd,
  BooleanNOr,
  BooleanNot,
  BooleanOr, BooleanTrue,
  BooleanVariable,
  BooleanXOr
} from './bool-node';

describe('BooleanVariable', () => {
  const varStr = 'a';
  const booleanVar: BooleanVariable = new BooleanVariable(varStr);

  it('should construct correctly', () => {
    expect(booleanVar.variable).toBe(varStr);
  });

  it('should evaluate correctly', () => {
    const assignments: Map<string, boolean> = new Map<string, boolean>();

    // No assignment, expect false
    expect(booleanVar.evaluate(assignments)).toBeFalsy();

    // Assignment to false
    assignments.set(booleanVar.variable, false);
    expect(booleanVar.evaluate(assignments)).toBeFalsy();

    // Assignment to true
    assignments.set(booleanVar.variable, true);
    expect(booleanVar.evaluate(assignments)).toBeTruthy();
  });

  it('should convert to string', () => {
    expect(booleanVar.asString()).toBe(varStr);
  });
});

describe('BooleanConstant', () => {
  it('should evaluate correclty', () => {
    expect(BooleanTrue.evaluate(new Map())).toBeTruthy();
    expect(BooleanFalse.evaluate(new Map())).toBeFalsy();
  });

  it('should convert to strng', () => {
    expect(BooleanTrue.asString()).toBe('1');
    expect(BooleanFalse.asString()).toBe('0');
  });
});

describe('BooleanNot', () => {
  const child: BooleanVariable = new BooleanVariable('a');
  const booleanNot: BooleanNot = new BooleanNot(child);

  it('should construct correctly', () => {
    expect(booleanNot.child).toBe(child);
  });

  it('should evaluate correctly', () => {
    const assignments: Map<string, boolean> = new Map<string, boolean>();

    assignments.set(child.variable, false);
    expect(booleanNot.evaluate(assignments)).toBeTruthy();

    assignments.set(child.variable, true);
    expect(booleanNot.evaluate(assignments)).toBeFalsy();
  });

  it('should convert to string', () => {
    expect(booleanNot.asString()).toBe('not a');
  });
});

describe('BooleanAnd', () => {
  const left: BooleanVariable = new BooleanVariable('a');
  const right: BooleanVariable = new BooleanVariable('b');
  const boolAnd: BooleanAnd = new BooleanAnd(left, right);

  it('should construct correctly', () => {
    expect(boolAnd.left).toBe(left);
    expect(boolAnd.right).toBe(right);
  });

  it('should evaluate correctly', () => {
    const assignments: Map<string, boolean> = new Map<string, boolean>();

    assignments.set(left.variable, false);
    assignments.set(right.variable, false);
    expect(boolAnd.evaluate(assignments)).toBeFalsy();

    assignments.set(left.variable, false);
    assignments.set(right.variable, true);
    expect(boolAnd.evaluate(assignments)).toBeFalsy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, false);
    expect(boolAnd.evaluate(assignments)).toBeFalsy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, true);
    expect(boolAnd.evaluate(assignments)).toBeTruthy();
  });

  it('should convert to string', () => {
    expect(boolAnd.asString()).toBe('a and b');
  });
});

describe('BooleanOr', () => {
  const left: BooleanVariable = new BooleanVariable('a');
  const right: BooleanVariable = new BooleanVariable('b');
  const booleanOr: BooleanOr = new BooleanOr(left, right);

  it('should construct correctly', () => {
    expect(booleanOr.left).toBe(left);
    expect(booleanOr.right).toBe(right);
  });

  it('should evaluate correctly', () => {
    const assignments: Map<string, boolean> = new Map<string, boolean>();

    assignments.set(left.variable, false);
    assignments.set(right.variable, false);
    expect(booleanOr.evaluate(assignments)).toBeFalsy();

    assignments.set(left.variable, false);
    assignments.set(right.variable, true);
    expect(booleanOr.evaluate(assignments)).toBeTruthy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, false);
    expect(booleanOr.evaluate(assignments)).toBeTruthy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, true);
    expect(booleanOr.evaluate(assignments)).toBeTruthy();
  });

  it('should convert to string', () => {
    expect(booleanOr.asString()).toBe('a or b');
  });
});

describe('BooleanNAnd', () => {
  const left: BooleanVariable = new BooleanVariable('a');
  const right: BooleanVariable = new BooleanVariable('b');
  const booleanNAnd: BooleanNAnd = new BooleanNAnd(left, right);

  it('should construct correctly', () => {
    expect(booleanNAnd.left).toBe(left);
    expect(booleanNAnd.right).toBe(right);
  });

  it('should evaluate correctly', () => {
    const assignments: Map<string, boolean> = new Map<string, boolean>();

    assignments.set(left.variable, false);
    assignments.set(right.variable, false);
    expect(booleanNAnd.evaluate(assignments)).toBeTruthy();

    assignments.set(left.variable, false);
    assignments.set(right.variable, true);
    expect(booleanNAnd.evaluate(assignments)).toBeTruthy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, false);
    expect(booleanNAnd.evaluate(assignments)).toBeTruthy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, true);
    expect(booleanNAnd.evaluate(assignments)).toBeFalsy();
  });

  it('should convert to string', () => {
    expect(booleanNAnd.asString()).toBe('a nand b');
  });
});

describe('BooleanNOr', () => {
  const left: BooleanVariable = new BooleanVariable('a');
  const right: BooleanVariable = new BooleanVariable('b');
  const booleanNOr: BooleanNOr = new BooleanNOr(left, right);

  it('should construct correctly', () => {
    expect(booleanNOr.left).toBe(left);
    expect(booleanNOr.right).toBe(right);
  });

  it('should evaluate correctly', () => {
    const assignments: Map<string, boolean> = new Map<string, boolean>();

    assignments.set(left.variable, false);
    assignments.set(right.variable, false);
    expect(booleanNOr.evaluate(assignments)).toBeTruthy();

    assignments.set(left.variable, false);
    assignments.set(right.variable, true);
    expect(booleanNOr.evaluate(assignments)).toBeFalsy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, false);
    expect(booleanNOr.evaluate(assignments)).toBeFalsy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, true);
    expect(booleanNOr.evaluate(assignments)).toBeFalsy();
  });

  it('should convert to string', () => {
    expect(booleanNOr.asString()).toBe('a nor b');
  });
});

describe('BooleanXOr', () => {
  const left: BooleanVariable = new BooleanVariable('a');
  const right: BooleanVariable = new BooleanVariable('b');
  const booleanXOr: BooleanXOr = new BooleanXOr(left, right);

  it('should construct correctly', () => {
    expect(booleanXOr.left).toBe(left);
    expect(booleanXOr.right).toBe(right);
  });

  it('should evaluate correctly', () => {
    const assignments: Map<string, boolean> = new Map<string, boolean>();

    assignments.set(left.variable, false);
    assignments.set(right.variable, false);
    expect(booleanXOr.evaluate(assignments)).toBeFalsy();

    assignments.set(left.variable, false);
    assignments.set(right.variable, true);
    expect(booleanXOr.evaluate(assignments)).toBeTruthy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, false);
    expect(booleanXOr.evaluate(assignments)).toBeTruthy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, true);
    expect(booleanXOr.evaluate(assignments)).toBeFalsy();
  });

  it('should convert to string', () => {
    expect(booleanXOr.asString()).toBe('a xor b');
  });
});

describe('BooleanEquivalency', () => {
  const left: BooleanVariable = new BooleanVariable('a');
  const right: BooleanVariable = new BooleanVariable('b');
  const booleanEquivalency: BooleanEquivalency = new BooleanEquivalency(left, right);

  it('should construct correctly', () => {
    expect(booleanEquivalency.left).toBe(left);
    expect(booleanEquivalency.right).toBe(right);
  });

  it('should evaluate correctly', () => {
    const assignments: Map<string, boolean> = new Map<string, boolean>();

    assignments.set(left.variable, false);
    assignments.set(right.variable, false);
    expect(booleanEquivalency.evaluate(assignments)).toBeTruthy();

    assignments.set(left.variable, false);
    assignments.set(right.variable, true);
    expect(booleanEquivalency.evaluate(assignments)).toBeFalsy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, false);
    expect(booleanEquivalency.evaluate(assignments)).toBeFalsy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, true);
    expect(booleanEquivalency.evaluate(assignments)).toBeTruthy();
  });

  it('should convert to string', () => {
    expect(booleanEquivalency.asString()).toBe('a equiv b');
  });
});


describe('BooleanImplication', () => {
  const left: BooleanVariable = new BooleanVariable('a');
  const right: BooleanVariable = new BooleanVariable('b');
  const booleanImplication: BooleanImplication = new BooleanImplication(left, right);

  it('should construct correctly', () => {
    expect(booleanImplication.left).toBe(left);
    expect(booleanImplication.right).toBe(right);
  });

  it('should evaluate correctly', () => {
    const assignments: Map<string, boolean> = new Map<string, boolean>();

    assignments.set(left.variable, false);
    assignments.set(right.variable, false);
    expect(booleanImplication.evaluate(assignments)).toBeTruthy();

    assignments.set(left.variable, false);
    assignments.set(right.variable, true);
    expect(booleanImplication.evaluate(assignments)).toBeTruthy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, false);
    expect(booleanImplication.evaluate(assignments)).toBeFalsy();

    assignments.set(left.variable, true);
    assignments.set(right.variable, true);
    expect(booleanImplication.evaluate(assignments)).toBeTruthy();
  });

  it('should convert to string', () => {
    expect(booleanImplication.asString()).toBe('a impl b');
  });
});
