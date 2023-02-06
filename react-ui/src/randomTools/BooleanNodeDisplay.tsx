import {BooleanBinaryNode, BooleanNode} from './boolModel/boolNode';

interface IProps {
  node: BooleanNode;
}

export function BooleanNodeDisplay({node}: IProps): JSX.Element {
  switch (node._type) {
    case 'Variable':
      return <span>{node.variable}</span>;
    case 'Constant':
      return <span>{node.value ? '1' : '0'}</span>;
    case 'Not':
      return 'left' in node.child
        ? <span>&not; <BooleanNodeDisplay node={node.child}/></span>
        : <span>&not; <BooleanNodeDisplay node={node.child}/></span>;
    default:
      return (
        <>
          (<BooleanNodeDisplay node={node.left}/>&nbsp;{getOperatorForNodeType(node)}&nbsp;<BooleanNodeDisplay node={node.right}/>)
        </>
      );
  }
}

function getOperatorForNodeType(node: BooleanBinaryNode): JSX.Element {
  return {
    'And': <>&and;</>,
    'Or': <>&or;</>,
    'NAnd': <>&#x22bc;</>,
    'NOr': <>&#x22bd;</>,
    'XOr': <>&oplus;</>,
    'Impl': <>&rArr;</>,
    'Equiv': <>&hArr;</>
  }[node._type];
}
