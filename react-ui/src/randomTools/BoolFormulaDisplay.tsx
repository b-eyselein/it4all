import {BooleanBinaryNode, NewBooleanNode} from './boolModel/bool-node';
import {BooleanFormula} from './boolModel/bool-formula';

export function BoolFormulaDisplay({formula: {left, right}}: { formula: BooleanFormula }): JSX.Element {
  return <>{left.variable} = <BooleanNodeDisplay node={right}/></>;
}

export function BooleanNodeDisplay({node}: { node: NewBooleanNode }): JSX.Element {
  switch (node._type) {
    case 'Variable':
      return <span>{node.variable}</span>;
    case 'Constant':
      return <span>{node.value ? '1' : '0'}</span>;
    case 'Not':
      return 'left' in node.child
        ? <span>&not; <BooleanNodeDisplay node={node.child}/></span>
        : <span>&not; (<BooleanNodeDisplay node={node.child}/>)</span>;
    default:
      return (
        <>
          <BooleanNodeDisplay node={node.left}/>&nbsp;{getOperatorForNodeType(node)}&nbsp;<BooleanNodeDisplay node={node.right}/>
        </>
      );
  }
}

function getOperatorForNodeType(node: BooleanBinaryNode): JSX.Element {
  switch (node._type) {
    case 'And':
      return <>&and;</>;
    case'Or':
      return <>&or;</>;
    case'NAnd':
      return <>&#x22bc;</>;
    case'NOr':
      return <>&#x22bd;</>;
    case'XOr':
      return <>&oplus;</>;
    case'Impl':
      return <>&rArr;</>;
    case'Equiv':
      return <>&hArr;</>;
  }
}
