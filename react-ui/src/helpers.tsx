import {ReactElement} from 'react';

function checkNever<T>(x: never, value: T): T {
  return value;
}

export function neverRender(x: never): ReactElement {
  return checkNever(x, <div/>);
}
