import React from 'react';
import {GradedHtmlTaskResult, SuccessType} from '../../../graphql';
import {ElementSpecResultDisplay} from './ElementSpecResultDisplay';

interface IProps {
  htmlResult: GradedHtmlTaskResult;
}

export function HtmlTaskResultDisplay({htmlResult}: IProps): JSX.Element {

  const {id, elementSpecResult} = htmlResult;

  const {success, points, maxPoints} = elementSpecResult;

  const isSuccessful = success === SuccessType.Complete;

  return (
    <>
      <p className={isSuccessful ? 'has-text-success' : 'has-text-danger'}>
        ({points} / {maxPoints}) Teilaufgabe {id} ist {isSuccessful ? '' : 'nicht'} korrekt:
      </p>

      <ElementSpecResultDisplay elementSpecResult={elementSpecResult}/>
    </>
  );
}
