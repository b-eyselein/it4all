import {Assignment} from './assignment';

export type BooleanNode = BooleanConstant | BooleanVariable | BooleanNot | BooleanBinaryNode;

export function getSubNodes(node: BooleanNode): BooleanNode[] {
  switch (node._type) {
    case 'Constant':
    case 'Variable':
      return [];
    case 'Not':
      return [node.child];
    default:
      return [node.left, node.right];
  }
}

export function evaluate(node: BooleanNode, assignments: Assignment): boolean {
  switch (node._type) {
    case 'Constant':
      return node.value;
    case 'Variable':
      return assignments[node.variable] || false;
    case 'Not':
      return !evaluate(node.child, assignments);
    default:
      return evaluateBinaryNode(node, assignments);
  }
}

function evaluateBinaryNode({_type, left, right}: BooleanBinaryNode, assignment: Assignment): boolean {
  const leftVal = evaluate(left, assignment);
  const rightVal = evaluate(right, assignment);

  return {
    'And': leftVal && rightVal,
    'Or': leftVal || rightVal,
    'NAnd': !(leftVal && rightVal),
    'NOr': !(leftVal || rightVal),
    'XOr': (leftVal && !rightVal) || (!leftVal && rightVal),
    'Equiv': leftVal === rightVal,
    'Impl': !leftVal || rightVal,
  }[_type];
}

export function stringifyNode(node: BooleanNode, needsParentheses = false): string {
  if (node._type === 'Constant') {
    return node.value ? '1' : '0';
  } else if (node._type === 'Variable') {
    return node.variable;
  } else if (node._type === 'Not') {
    return 'not ' + stringifyNode(node.child, true);
  } else {
    const child = stringifyNode(node.left, true) + ' ' + node._type.toLowerCase() + ' ' + stringifyNode(node.right, true);

    return needsParentheses
      ? '(' + child + ')'
      : child;
  }
}

export function getVariables(node: BooleanNode): BooleanVariable[] {
  // FIXME: test!
  switch (node._type) {
    case 'Constant':
      return [];
    case 'Variable':
      return [node];
    case 'Not':
      return getVariables(node.child);
    default:
      return Array.from(new Set([...getVariables(node.left), ...getVariables(node.right)]));
  }
}

export interface BooleanVariable {
  _type: 'Variable';
  variable: string;
}

export function booleanVariable(variable: string): BooleanVariable {
  return {_type: 'Variable', variable};
}


export interface BooleanConstant {
  _type: 'Constant';
  value: boolean;
}

export const BooleanTrue: BooleanConstant = {_type: 'Constant', value: true};

export const BooleanFalse: BooleanConstant = {_type: 'Constant', value: false};

export interface BooleanNot {
  _type: 'Not';
  child: BooleanNode;
}

export function not(child: BooleanNode): BooleanNot {
  return {_type: 'Not', child};
}

// Boolean binary nodes

type BooleanBinaryNodeType = 'And' | 'Or' | 'NAnd' | 'NOr' | 'XOr' | 'Equiv' | 'Impl';

export interface BooleanBinaryNode {
  _type: 'And' | 'Or' | 'NAnd' | 'NOr' | 'XOr' | 'Equiv' | 'Impl';
  left: BooleanNode;
  right: BooleanNode;
}

type BooleanBinaryNodeConstructor = (left: BooleanNode, right: BooleanNode) => BooleanBinaryNode;

function createBinaryNodeFunc(_type: BooleanBinaryNodeType): BooleanBinaryNodeConstructor {
  return (left, right) => ({_type, left, right});
}

export const and = createBinaryNodeFunc('And');
export const or = createBinaryNodeFunc('Or');
export const nand = createBinaryNodeFunc('NAnd');
export const nor = createBinaryNodeFunc('NOr');
export const xor = createBinaryNodeFunc('XOr');
export const equiv = createBinaryNodeFunc('Equiv');
export const impl = createBinaryNodeFunc('Impl');
