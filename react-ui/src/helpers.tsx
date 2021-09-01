import React from 'react';

export function checkNever<T>(x: never, value: T): T {
  return value;
}

export function neverRender(x: never): JSX.Element {
  return checkNever(x, <div/>);
}
