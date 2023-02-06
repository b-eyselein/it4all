import {Assignment} from './assignment';

export type BooleanNode = BooleanConstant | BooleanVariable | BooleanNot | BooleanBinaryNode;

export function getSubNodes(node: BooleanNode): BooleanNode[] {
  // FIXME: test!
  switch (node._type) {
    case 'Constant':
    case 'Variable':
      return [];
    case 'Not':
      return [node.child];
    case 'And':
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
  } [_type];
}

export function stringifyNode(node: BooleanNode): string {
  switch (node._type) {
    case 'Constant':
      return node.value ? '1' : '0';
    case 'Variable':
      return node.variable;
    case 'Not':
      return 'not ' + stringifyNode(node.child);
    default:
      return '(' + stringifyNode(node.left) + ') ' + node._type.toLowerCase() + ' (' + stringifyNode(node.right) + ')';
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
  _type: BooleanBinaryNodeType;
  left: BooleanNode;
  right: BooleanNode;
}

export function and(left: BooleanNode, right: BooleanNode): BooleanBinaryNode {
  return {_type: 'And', left, right};
}

export function or(left: BooleanNode, right: BooleanNode): BooleanBinaryNode {
  return {_type: 'Or', left, right};
}

export function nand(left: BooleanNode, right: BooleanNode): BooleanBinaryNode {
  return {_type: 'NAnd', left, right};
}

export function nor(left: BooleanNode, right: BooleanNode): BooleanBinaryNode {
  return {_type: 'NOr', left, right};
}

export function xor(left: BooleanNode, right: BooleanNode): BooleanBinaryNode {
  return {_type: 'XOr', left, right};
}

export function equiv(left: BooleanNode, right: BooleanNode): BooleanBinaryNode {
  return {_type: 'Equiv', left, right};
}

export function impl(left: BooleanNode, right: BooleanNode): BooleanBinaryNode {
  return {_type: 'Impl', left, right};
}

