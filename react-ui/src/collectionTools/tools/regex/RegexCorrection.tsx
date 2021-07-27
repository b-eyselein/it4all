import React from 'react';
import {RegexCorrectionMutation, RegexCorrectionMutationResult, RegexCorrectionResultFragment} from '../../../graphql';
import {WithQuery} from '../../../WithQuery';
import {SolutionSaved} from '../../../helpers/SolutionSaved';
import {PointsNotification} from '../../../helpers/PointsNotification';
import {RegexExtractionResultDisplay} from './RegexExtractionResultDisplay';
import {RegexMatchingResultDisplay} from './RegexMatchingResultDisplay';
import {useTranslation} from 'react-i18next';

interface IProps {
  mutationResult: RegexCorrectionMutationResult;
}

export function RegexCorrection({mutationResult}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  function render({me}: RegexCorrectionMutation): JSX.Element {
    if (!me || !me.regexExercise) {
      return <div className="notification is-danger">{t('correctionError')}</div>;
    }

    const {solutionSaved, result/* TODO:, resultSaved, proficienciesUpdated*/}: RegexCorrectionResultFragment = me.regexExercise.correct;

    return <>

      <SolutionSaved solutionSaved={solutionSaved}/>

      <PointsNotification points={result.points} maxPoints={result.maxPoints}/>

      {result.__typename === 'RegexExtractionResult'
        ? <RegexExtractionResultDisplay result={result}/>
        : <RegexMatchingResultDisplay result={result}/>}

    </>;
  }

  return mutationResult.called
    ? <WithQuery query={mutationResult} render={render}/>
    : <></>;
}
