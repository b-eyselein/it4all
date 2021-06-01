import React from "react";
import {RegexCorrectionMutation, RegexCorrectionResultFragment} from "../../../generated/graphql";
import {MutationResult} from "@apollo/client";
import {WithQuery} from "../../../WithQuery";
import {SolutionSaved} from "../../../helpers/SolutionSaved";
import {PointsNotification} from "../../../helpers/PointsNotification";
import {RegexExtractionResultDisplay} from './RegexExtractionResultDisplay';
import {RegexMatchingResultDisplay} from './RegexMatchingResultDisplay';

interface IProps {
  mutationResult: MutationResult<RegexCorrectionMutation>;
}

export function RegexCorrection({mutationResult}: IProps): JSX.Element {

  function render({me}: RegexCorrectionMutation): JSX.Element {
    if (!me || !me.regexExercise) {
      return <div className="notification is-danger">There has been a correction error!</div>;
    }

    const {result, resultSaved, solutionSaved, proficienciesUpdated}: RegexCorrectionResultFragment = me.regexExercise.correct;

    return <>

      <SolutionSaved solutionSaved={solutionSaved}/>

      <PointsNotification points={result.points} maxPoints={result.maxPoints}/>

      {result.__typename === 'RegexExtractionResult'
        ? <RegexExtractionResultDisplay result={result}/>
        : <RegexMatchingResultDisplay result={result}/>}

    </>;
  }

  return !mutationResult.called
    ? <></>
    : <WithQuery query={mutationResult} children={render}/>;
}
