import React from 'react';
import {SqlCorrectionMutation, SqlCorrectionMutationResult} from '../../../graphql';
import {WithQuery} from '../../../WithQuery';
import {useTranslation} from 'react-i18next';
import {SolutionSaved} from '../../../helpers/SolutionSaved';
import {SqlMatchingResult} from './SqlMatchingResult';
import {SqlStringMatchingResult} from './SqlStringMatchingResult';

interface IProps {
  mutationResult: SqlCorrectionMutationResult;
}

export function SqlCorrection({mutationResult}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  function render({me}: SqlCorrectionMutation): JSX.Element {
    if (!me || !me.sqlExercise) {
      return <div className="notification is-danger">{t('correctionError')}</div>;
    }

    const {solutionSaved, result/* TODO: , resultSaved, proficienciesUpdated */} = me.sqlExercise.correct;

    const {
      columnComparison,
      tableComparison,
      joinExpressionComparison,
      whereComparison,
      additionalComparisons: {selectComparisons, insertComparison}
    } = result.staticComparison;

    return <>

      <SolutionSaved solutionSaved={solutionSaved}/>

      {/*<PointsNotification points={result.points} maxPoints={result.maxPoints}/>*/}

      <SqlMatchingResult matchName="Spalten" matchSingularName="der Spalte" matchingResult={columnComparison}/>

      <br/>

      <SqlStringMatchingResult matchName="Tabellen" matchSingularName="der Tabelle" matchingResult={tableComparison}/>

      <br/>

      <SqlMatchingResult matchName="Join-Bedingungen" matchSingularName="der Join-Bedingung" matchingResult={joinExpressionComparison}/>

      <br/>

      <SqlMatchingResult matchName="Bedingungen" matchSingularName="der Bedingung" matchingResult={whereComparison}/>

      <br/>

      {selectComparisons && <>

        <SqlStringMatchingResult matchName="Order Bys" matchSingularName="des Order By-Statements" matchingResult={selectComparisons.orderByComparison}/>

        <br/>

        <SqlStringMatchingResult matchName="Group Bys" matchSingularName="des Group By-Statements" matchingResult={selectComparisons.groupByComparison}/>

        <br/>

        <SqlMatchingResult matchName="Limits" matchSingularName="des Limit-Statements" matchingResult={selectComparisons.limitComparison}/>
      </>}

      {insertComparison && <SqlStringMatchingResult matchName="Inserts" matchSingularName="Insert" matchingResult={insertComparison}/>}

    </>;

  }

  return mutationResult.called
    ? <WithQuery query={mutationResult} render={render}/>
    : <></>;
}
