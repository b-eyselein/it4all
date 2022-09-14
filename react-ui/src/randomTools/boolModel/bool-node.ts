import {Assignment} from './assignment';

export type NewBooleanNode = BooleanConstant | BooleanVariable | BooleanNot | BooleanBinaryNode;

export function getSubNodes(node: NewBooleanNode): NewBooleanNode[] {
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

export function evaluate(node: NewBooleanNode, assignments: Assignment): boolean {
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

  switch (_type) {
    case 'And':
      return leftVal && rightVal;
    case 'Or':
      return leftVal || rightVal;
    case 'NAnd':
      return !(leftVal && rightVal);
    case 'NOr':
      return !(leftVal || rightVal);
    case 'XOr':
      return (leftVal && !rightVal) || (!leftVal && rightVal);
    case 'Equiv':
      return leftVal === rightVal;
    case 'Impl':
      return !leftVal || rightVal;
  }
}

export function stringify(node: NewBooleanNode): string {
  switch (node._type) {
    case 'Constant':
      return node.value ? '1' : '0';
    case 'Variable':
      return node.variable;
    case 'Not':
      return 'not ' + stringify(node.child);
    default:
      return stringify(node.left) + ' ' + node._type.toLowerCase() + ' ' + stringify(node.right);
  }
}

export function getVariables(node: NewBooleanNode): BooleanVariable[] {
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
  child: NewBooleanNode;
}

export function not(child: NewBooleanNode): BooleanNot {
  return {_type: 'Not', child};
}


// Boolean binary nodes

export interface IBooleanBinaryNode {
  left: NewBooleanNode;
  right: NewBooleanNode;
}

export type BooleanBinaryNode = BooleanAnd | BooleanOr | BooleanNAnd | BooleanNOr | BooleanXOr | BooleanEquivalency | BooleanImplication;


export interface BooleanAnd extends IBooleanBinaryNode {
  _type: 'And';
}

export function and(left: NewBooleanNode, right: NewBooleanNode): BooleanAnd {
  return {_type: 'And', left, right};
}


export interface BooleanOr extends IBooleanBinaryNode {
  _type: 'Or';
}

export function or(left: NewBooleanNode, right: NewBooleanNode): BooleanOr {
  return {_type: 'Or', left, right};
}


export interface BooleanNAnd extends IBooleanBinaryNode {
  _type: 'NAnd';
}

export function nand(left: NewBooleanNode, right: NewBooleanNode): BooleanNAnd {
  return {_type: 'NAnd', left, right};
}


export interface BooleanNOr extends IBooleanBinaryNode {
  _type: 'NOr';
}

export function nor(left: NewBooleanNode, right: NewBooleanNode): BooleanNOr {
  return {_type: 'NOr', left, right};
}


export interface BooleanXOr extends IBooleanBinaryNode {
  _type: 'XOr';
}

export function xor(left: NewBooleanNode, right: NewBooleanNode): BooleanXOr {
  return {_type: 'XOr', left, right};
}


export interface BooleanEquivalency extends IBooleanBinaryNode {
  _type: 'Equiv';
}

export function equiv(left: NewBooleanNode, right: NewBooleanNode): BooleanEquivalency {
  return {_type: 'Equiv', left, right};
}


export interface BooleanImplication extends IBooleanBinaryNode {
  _type: 'Impl';
}

export function impl(left: NewBooleanNode, right: NewBooleanNode): BooleanImplication {
  return {_type: 'Impl', left, right};
}

