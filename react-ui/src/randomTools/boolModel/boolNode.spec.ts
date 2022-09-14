import {
  and,
  BooleanFalse,
  BooleanTrue,
  booleanVariable,
  equiv,
  evaluate,
  getSubNodes,
  getVariables,
  impl,
  nand,
  nor,
  not,
  or,
  stringifyNode,
  xor
} from './boolNode';

const variableA = booleanVariable('a');
const variableB = booleanVariable('b');

type Row = {
  a: boolean;
  b: boolean;
  z: boolean;
};

describe('BooleanVariable', () => {
  it('should construct correctly', () => {
    expect(variableA.variable).toBe('a');

    expect(getVariables(variableA)).toEqual([variableA]);
    expect(getSubNodes(variableA)).toEqual([]);
  });

  it('should evaluate correctly', () => {
    // No assignment, expect false
    expect(evaluate(variableA, {})).toBeFalsy();

    // Assignment to false
    expect(evaluate(variableA, {[variableA.variable]: false})).toBeFalsy();

    // Assignment to true
    expect(evaluate(variableA, {[variableA.variable]: true})).toBeTruthy();
  });

  it('should convert to string', () => {
    expect(stringifyNode(variableA)).toBe('a');
  });
});

describe('BooleanConstant', () => {
  it('should evaluate correctly', () => {
    expect(evaluate(BooleanTrue, {})).toBeTruthy();
    expect(evaluate(BooleanFalse, {})).toBeFalsy();

    expect(getVariables(BooleanTrue)).toEqual([]);
    expect(getVariables(BooleanFalse)).toEqual([]);

    expect(getSubNodes(BooleanTrue)).toEqual([]);
    expect(getSubNodes(BooleanFalse)).toEqual([]);
  });

  it('should convert to string', () => {
    expect(stringifyNode(BooleanTrue)).toBe('1');
    expect(stringifyNode(BooleanFalse)).toBe('0');
  });
});

describe('BooleanNot', () => {
  const boolNot = not(variableA);

  it('should construct correctly', () => {
    expect(boolNot.child).toBe(variableA);

    expect(getVariables(boolNot)).toEqual([variableA]);
    expect(getSubNodes(boolNot)).toEqual([variableA]);
  });

  it('should evaluate correctly', () => {
    expect(evaluate(boolNot, {[variableA.variable]: false})).toBeTruthy();

    expect(evaluate(boolNot, {[variableA.variable]: true})).toBeFalsy();
  });

  it('should convert to string', () => {
    expect(stringifyNode(boolNot)).toBe('not a');
  });
});

describe('BooleanAnd', () => {
  const boolAnd = and(variableA, variableB);

  it('should construct correctly', () => {
    expect(boolAnd.left).toBe(variableA);
    expect(boolAnd.right).toBe(variableB);

    expect(getVariables(boolAnd)).toEqual([variableA, variableB]);
    expect(getSubNodes(boolAnd)).toEqual([variableA, variableB]);
  });

  test.each<Row>([
    {a: false, b: false, z: false},
    {a: false, b: true, z: false},
    {a: true, b: false, z: false},
    {a: true, b: true, z: true}
  ])(
    `${boolAnd} with a = $a and b = $b should evaluate to $expected`,
    ({a, b, z}) => expect(evaluate(boolAnd, {[variableA.variable]: a, [variableB.variable]: b})).toBe(z)
  );

  it('should convert to string', () => {
    expect(stringifyNode(boolAnd)).toBe('a and b');
  });
});

describe('BooleanOr', () => {
  const boolOr = or(variableA, variableB);

  it('should construct correctly', () => {
    expect(boolOr.left).toBe(variableA);
    expect(boolOr.right).toBe(variableB);

    expect(getVariables(boolOr)).toEqual([variableA, variableB]);
    expect(getSubNodes(boolOr)).toEqual([variableA, variableB]);
  });

  test.each<Row>([
    {a: false, b: false, z: false},
    {a: false, b: true, z: true},
    {a: true, b: false, z: true},
    {a: true, b: true, z: true}
  ])(
    `${boolOr} with a = $a and b = $b should evaluate to $expected`,
    ({a, b, z}) => expect(evaluate(boolOr, {[variableA.variable]: a, [variableB.variable]: b})).toBe(z)
  );

  it('should convert to string', () => {
    expect(stringifyNode(boolOr)).toBe('a or b');
  });
});

describe('BooleanNAnd', () => {
  const boolNAnd = nand(variableA, variableB);

  it('should construct correctly', () => {
    expect(boolNAnd.left).toBe(variableA);
    expect(boolNAnd.right).toBe(variableB);

    expect(getVariables(boolNAnd)).toEqual([variableA, variableB]);
    expect(getSubNodes(boolNAnd)).toEqual([variableA, variableB]);
  });

  test.each<Row>([
    {a: false, b: false, z: true},
    {a: false, b: true, z: true},
    {a: true, b: false, z: true},
    {a: true, b: true, z: false}
  ])(
    `${boolNAnd} with a = $a and b = $b should evaluate to $z`,
    ({a, b, z}) => expect(evaluate(boolNAnd, {[variableA.variable]: a, [variableB.variable]: b})).toBe(z)
  );

  it('should convert to string', () => {
    expect(stringifyNode(boolNAnd)).toBe('a nand b');
  });
});

describe('BooleanNOr', () => {
  const boolNOr = nor(variableA, variableB);

  it('should construct correctly', () => {
    expect(boolNOr.left).toBe(variableA);
    expect(boolNOr.right).toBe(variableB);

    expect(getVariables(boolNOr)).toEqual([variableA, variableB]);
    expect(getSubNodes(boolNOr)).toEqual([variableA, variableB]);
  });

  test.each<Row>([
    {a: false, b: false, z: true},
    {a: false, b: true, z: false},
    {a: true, b: false, z: false},
    {a: true, b: true, z: false}
  ])(
    `${boolNOr} with a = $a and b = $b should evaluate to $z`,
    ({a, b, z}) => expect(evaluate(boolNOr, {[variableA.variable]: a, [variableB.variable]: b})).toBe(z)
  );

  it('should convert to string', () => {
    expect(stringifyNode(boolNOr)).toBe('a nor b');
  });
});

describe('BooleanXOr', () => {
  const boolXOr = xor(variableA, variableB);

  it('should construct correctly', () => {
    expect(boolXOr.left).toBe(variableA);
    expect(boolXOr.right).toBe(variableB);

    expect(getVariables(boolXOr)).toEqual([variableA, variableB]);
    expect(getSubNodes(boolXOr)).toEqual([variableA, variableB]);
  });

  test.each<Row>([
    {a: false, b: false, z: false},
    {a: false, b: true, z: true},
    {a: true, b: false, z: true},
    {a: true, b: true, z: false}
  ])(
    `${boolXOr} with a = $a and b = $b should evaluate to $z`,
    ({a, b, z}) => expect(evaluate(boolXOr, {[variableA.variable]: a, [variableB.variable]: b})).toBe(z)
  );

  it('should convert to string', () => {
    expect(stringifyNode(boolXOr)).toBe('a xor b');
  });
});

describe('BooleanEquivalency', () => {
  const boolEquiv = equiv(variableA, variableB);

  it('should construct correctly', () => {
    expect(boolEquiv.left).toBe(variableA);
    expect(boolEquiv.right).toBe(variableB);

    expect(getVariables(boolEquiv)).toEqual([variableA, variableB]);
    expect(getSubNodes(boolEquiv)).toEqual([variableA, variableB]);
  });

  test.each<Row>([
    {a: false, b: false, z: true},
    {a: false, b: true, z: false},
    {a: true, b: false, z: false},
    {a: true, b: true, z: true}
  ])(
    `${boolEquiv} with a = $a and b = $b should evaluate to $z`,
    ({a, b, z}) => expect(evaluate(boolEquiv, {[variableA.variable]: a, [variableB.variable]: b})).toBe(z)
  );

  it('should convert to string', () => {
    expect(stringifyNode(boolEquiv)).toBe('a equiv b');
  });
});

describe('BooleanImplication', () => {
  const boolImpl = impl(variableA, variableB);

  it('should construct correctly', () => {
    expect(boolImpl.left).toBe(variableA);
    expect(boolImpl.right).toBe(variableB);

    expect(getVariables(boolImpl)).toEqual([variableA, variableB]);
    expect(getSubNodes(boolImpl)).toEqual([variableA, variableB]);
  });

  test.each<Row>([
    {a: false, b: false, z: true},
    {a: false, b: true, z: true},
    {a: true, b: false, z: false},
    {a: true, b: true, z: true}
  ])(
    `${boolImpl} with a = $a and b = $b should evaluate to $z`,
    ({a, b, z}) => expect(evaluate(boolImpl, {[variableA.variable]: a, [variableB.variable]: b})).toBe(z)
  );

  it('should convert to string', () => {
    expect(stringifyNode(boolImpl)).toBe('a impl b');
  });
});
