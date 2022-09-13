import {
  BooleanAnd,
  BooleanBinaryNode,
  BooleanConstant,
  BooleanEquivalency,
  BooleanImplication,
  BooleanNAnd,
  BooleanNode,
  BooleanNOr,
  BooleanNot,
  BooleanOr,
  BooleanVariable,
  BooleanXNor,
  BooleanXOr
} from './boolModel/bool-node';
import {BooleanFormula} from './boolModel/bool-formula';

export function BoolFormulaDisplay({formula: {left, right}}: { formula: BooleanFormula }): JSX.Element {
  return <>{left.variable} = <BooleanNodeDisplay node={right}/></>;
}

function BooleanNodeDisplay({node}: { node: BooleanNode }): JSX.Element {

  if (node instanceof BooleanVariable) {
    return <span>{node.variable}</span>;
  }

  if (node instanceof BooleanConstant) {
    return <span>{node.value ? '1' : '0'}</span>;
  }

  if (node instanceof BooleanNot) {
    const {child} = node;

    return child instanceof BooleanBinaryNode
      ? <span>&not; <BooleanNodeDisplay node={child}/></span>
      : <span>&not; (<BooleanNodeDisplay node={child}/>)</span>;
  }

  if (node instanceof BooleanBinaryNode) {
    const {left, right} = node;

    return <>
      <BooleanNodeDisplay node={left}/>&nbsp;{getOperatorForNodeType(node)}&nbsp;<BooleanNodeDisplay node={right}/>
    </>;
  }

  return <span>TODO!</span>;
}

function getOperatorForNodeType(node: BooleanBinaryNode): JSX.Element {
  if (node instanceof BooleanAnd) {
    return <>&and;</>;
  } else if (node instanceof BooleanOr) {
    return <>&or;</>;
  } else if (node instanceof BooleanNAnd) {
    return <>&#x22bc;</>;
  } else if (node instanceof BooleanNOr) {
    return <>&#x22bd;</>;
  } else if (node instanceof BooleanXOr) {
    return <>&oplus;</>;
  } else if (node instanceof BooleanImplication) {
    return <>&rArr;</>;
  } else if (node instanceof BooleanEquivalency) {
    return <>&hArr;</>;
  } else if (node instanceof BooleanXNor) {
    return <>xnor</>;
  } else {
    throw new Error('TODO'!);
  }
}
