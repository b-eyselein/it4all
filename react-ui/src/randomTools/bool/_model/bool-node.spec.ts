import {
  BooleanAnd,
  BooleanEquivalency,
  BooleanFalse,
  BooleanImplication,
  BooleanNAnd,
  BooleanNOr,
  BooleanNot,
  BooleanOr,
  BooleanTrue,
  BooleanVariable,
  BooleanXOr
} from './bool-node';

const variableA: BooleanVariable = new BooleanVariable('a');
const variableB: BooleanVariable = new BooleanVariable('b');

describe('BooleanVariable', () => {
  it('should construct correctly', () => {
    expect(variableA.variable).toBe('a');
  });

  it('should evaluate correctly', () => {
    // No assignment, expect false
    expect(variableA.evaluate({})).toBeFalsy();

    // Assignment to false
    expect(variableA.evaluate({[variableA.variable]: false})).toBeFalsy();

    // Assignment to true
    expect(variableA.evaluate({[variableA.variable]: true})).toBeTruthy();
  });

  it('should convert to string', () => {
    expect(variableA.asString()).toBe('a');
  });
});

describe('BooleanConstant', () => {
  it('should evaluate correctly', () => {
    expect(BooleanTrue.evaluate()).toBeTruthy();
    expect(BooleanFalse.evaluate()).toBeFalsy();
  });

  it('should convert to string', () => {
    expect(BooleanTrue.asString()).toBe('1');
    expect(BooleanFalse.asString()).toBe('0');
  });
});

describe('BooleanNot', () => {
  const booleanNot: BooleanNot = new BooleanNot(variableA);

  it('should construct correctly', () => {
    expect(booleanNot.child).toBe(variableA);
  });

  it('should evaluate correctly', () => {
    expect(booleanNot.evaluate({[variableA.variable]: false})).toBeTruthy();

    expect(booleanNot.evaluate({[variableA.variable]: true})).toBeFalsy();
  });

  it('should convert to string', () => {
    expect(booleanNot.asString()).toBe('not a');
  });
});

describe('BooleanAnd', () => {
  const boolAnd: BooleanAnd = new BooleanAnd(variableA, variableB);

  it('should construct correctly', () => {
    expect(boolAnd.left).toBe(variableA);
    expect(boolAnd.right).toBe(variableB);
  });

  test.each`
  a | b | expected
  ${false} | ${false} | ${false}
  ${false} | ${true} | ${false}
  ${true} | ${false} | ${false}
  ${true} | ${true} | ${true}
  `(
    `${boolAnd.asString()} with a = $a and b = $b should evaluate to $expected`,
    ({a, b, expected}) => expect(boolAnd.evaluate({[variableA.variable]: a, [variableB.variable]: b})).toBe(expected)
  );

  it('should convert to string', () => {
    expect(boolAnd.asString()).toBe('a and b');
  });
});

describe('BooleanOr', () => {
  const boolOr: BooleanOr = new BooleanOr(variableA, variableB);

  it('should construct correctly', () => {
    expect(boolOr.left).toBe(variableA);
    expect(boolOr.right).toBe(variableB);
  });

  test.each`
  a | b | expected
  ${false} | ${false} | ${false}
  ${false} | ${true} | ${true}
  ${true} | ${false} | ${true}
  ${true} | ${true} | ${true}
  `(
    `${boolOr.asString()} with a = $a and b = $b should evaluate to $expected`,
    ({a, b, expected}) => expect(boolOr.evaluate({[variableA.variable]: a, [variableB.variable]: b})).toBe(expected)
  );

  it('should convert to string', () => {
    expect(boolOr.asString()).toBe('a or b');
  });
});

describe('BooleanNAnd', () => {
  const boolNAnd: BooleanNAnd = new BooleanNAnd(variableA, variableB);

  it('should construct correctly', () => {
    expect(boolNAnd.left).toBe(variableA);
    expect(boolNAnd.right).toBe(variableB);
  });

  test.each`
  a | b | expected
  ${false} | ${false} | ${true}
  ${false} | ${true} | ${true}
  ${true} | ${false} | ${true}
  ${true} | ${true} | ${false}
  `(
    `${boolNAnd.asString()} with a = $a and b = $b should evaluate to $expected`,
    ({a, b, expected}) => expect(boolNAnd.evaluate({[variableA.variable]: a, [variableB.variable]: b})).toBe(expected)
  );

  it('should convert to string', () => {
    expect(boolNAnd.asString()).toBe('a nand b');
  });
});

describe('BooleanNOr', () => {
  const boolNOr: BooleanNOr = new BooleanNOr(variableA, variableB);

  it('should construct correctly', () => {
    expect(boolNOr.left).toBe(variableA);
    expect(boolNOr.right).toBe(variableB);
  });

  test.each`
  a | b | expected
  ${false} | ${false} | ${true}
  ${false} | ${true} | ${false}
  ${true} | ${false} | ${false}
  ${true} | ${true} | ${false}
  `(
    `${boolNOr.asString()} with a = $a and b = $b should evaluate to $expected`,
    ({a, b, expected}) => expect(boolNOr.evaluate({[variableA.variable]: a, [variableB.variable]: b})).toBe(expected)
  );

  it('should convert to string', () => {
    expect(boolNOr.asString()).toBe('a nor b');
  });
});

describe('BooleanXOr', () => {
  const boolXOr: BooleanXOr = new BooleanXOr(variableA, variableB);

  it('should construct correctly', () => {
    expect(boolXOr.left).toBe(variableA);
    expect(boolXOr.right).toBe(variableB);
  });

  test.each`
  a | b | expected
  ${false} | ${false} | ${false}
  ${false} | ${true} | ${true}
  ${true} | ${false} | ${true}
  ${true} | ${true} | ${false}
  `(
    `${boolXOr.asString()} with a = $a and b = $b should evaluate to $expected`,
    ({a, b, expected}) => expect(boolXOr.evaluate({[variableA.variable]: a, [variableB.variable]: b})).toBe(expected)
  );

  it('should convert to string', () => {
    expect(boolXOr.asString()).toBe('a xor b');
  });
});

describe('BooleanEquivalency', () => {
  const booleanEquivalency: BooleanEquivalency = new BooleanEquivalency(variableA, variableB);

  it('should construct correctly', () => {
    expect(booleanEquivalency.left).toBe(variableA);
    expect(booleanEquivalency.right).toBe(variableB);
  });

  test.each`
  a | b | expected
  ${false} | ${false} | ${true}
  ${false} | ${true} | ${false}
  ${true} | ${false} | ${false}
  ${true} | ${true} | ${true}
  `(
    `${booleanEquivalency.asString()} with a = $a and b = $b should evaluate to $expected`,
    ({a, b, expected}) => expect(booleanEquivalency.evaluate({[variableA.variable]: a, [variableB.variable]: b})).toBe(expected)
  );

  it('should convert to string', () => {
    expect(booleanEquivalency.asString()).toBe('a equiv b');
  });
});

describe('BooleanImplication', () => {
  const booleanImplication: BooleanImplication = new BooleanImplication(variableA, variableB);

  it('should construct correctly', () => {
    expect(booleanImplication.left).toBe(variableA);
    expect(booleanImplication.right).toBe(variableB);
  });

  test.each`
  a | b | expected
  ${false} | ${false} | ${true}
  ${false} | ${true} | ${true}
  ${true} | ${false} | ${false}
  ${true} | ${true} | ${true}
  `(
    `${booleanImplication.asString()} with a = $a and b = $b should evaluate to $expected`,
    ({a, b, expected}) => expect(booleanImplication.evaluate({[variableA.variable]: a, [variableB.variable]: b})).toBe(expected)
  );

  it('should convert to string', () => {
    expect(booleanImplication.asString()).toBe('a impl b');
  });
});
