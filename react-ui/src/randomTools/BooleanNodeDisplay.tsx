import {BooleanBinaryNode, BooleanNode} from './boolModel/boolNode';

interface IProps {
  node: BooleanNode;
  needsParentheses?: boolean;
}

export function BooleanNodeDisplay({node, needsParentheses}: IProps): JSX.Element {
  if (node._type === 'Constant') {
    return <span>{node.value ? '1' : '0'}</span>;
  } else if (node._type === 'Variable') {
    return <span>{node.variable}</span>;
  } else if (node._type === 'Not') {
    return <span>&not; <BooleanNodeDisplay node={node.child} needsParentheses={true}/></span>;
  } else {
    const childrenRender = (
      <>
        <BooleanNodeDisplay node={node.left} needsParentheses={true}/>
        &nbsp;
        {getOperatorForNodeType(node)}
        &nbsp;
        <BooleanNodeDisplay node={node.right} needsParentheses={true}/>
      </>
    );

    return needsParentheses
      ? <>({childrenRender})</>
      : <>{childrenRender}</>;
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
