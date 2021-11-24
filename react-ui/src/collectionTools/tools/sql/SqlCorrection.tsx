import {MatchType, SelectAdditionalComparisonFragment, SqlBinaryExpressionMatch, SqlColumnMatch, SqlCorrectionMutationResult} from '../../../graphql';
import {isEmpty, MatchingResultDisplay} from '../MatchingResultDisplay';
import {SqlStringMatchingResult} from './SqlStringMatchingResult';
import {SolutionSaved} from '../../../helpers/SolutionSaved';
import {WithQuery} from '../../../WithQuery';
import {WithNullableNavigate} from '../../../WithNullableNavigate';

interface IProps {
  mutationResult: SqlCorrectionMutationResult;
}

function ColumnComparisonMatchDisplay({m: {matchType, userArg, sampleArg}}: { m: SqlColumnMatch }): JSX.Element {

  const isCorrect = matchType === MatchType.SuccessfulMatch;

  return (
    <>
      <span>Die Angabe der Spalte <code>{userArg}</code> ist {isCorrect ? '' : 'nicht komplett'} korrekt.</span>
      {!isCorrect && <span>&nbsp;Erwartet wurde <code>{sampleArg}</code></span>}
    </>
  );
}

function BinaryExpressionMatchDisplay({m}: { m: SqlBinaryExpressionMatch }): JSX.Element {
  return <span>Die Angabe der Bedingung <code>{m.userArg}</code> ist korrekt.</span>;
}

function SelectAdditionalComparisonDisplay({addComp}: { addComp: SelectAdditionalComparisonFragment }): JSX.Element {

  const {orderByComparison, groupByComparison, limitComparison} = addComp;

  return (
    <>
      {!isEmpty(orderByComparison) &&
      <SqlStringMatchingResult matchName="Order Bys" matchSingularName="des Order By-Statements" matchingResult={orderByComparison}/>}

      {!isEmpty(groupByComparison) &&
      <SqlStringMatchingResult matchName="Group Bys" matchSingularName="des Group By-Statements" matchingResult={groupByComparison}/>}

      {!isEmpty(limitComparison) &&
      <SqlStringMatchingResult matchName="Limits" matchSingularName="des Limit-Statements" matchingResult={limitComparison}/>}
    </>
  );
}

export function SqlCorrection({mutationResult}: IProps): JSX.Element {

  // notCalledMessage={<></>}

  return (
    <WithQuery query={mutationResult}>
      {({sqlExercise}) => <WithNullableNavigate t={sqlExercise}>
        {({
            correct: {
              solutionSaved,
              result: {
                staticComparison: {
                  columnComparison,
                  tableComparison,
                  joinExpressionComparison,
                  whereComparison,
                  additionalComparisons: {selectComparisons, insertComparison}
                }
              }
              /* TODO: , resultSaved, proficienciesUpdated */
            }
          }) => <>

          <SolutionSaved solutionSaved={solutionSaved}/>

          {/*<PointsNotification points={result.points} maxPoints={result.maxPoints}/>*/}

          <MatchingResultDisplay matchingResult={columnComparison} comparedItemPluralName="Spalten"
                                 describeMatch={(m) => <ColumnComparisonMatchDisplay m={m}/>}
                                 describeNotMatchedItem={(columnName) => <span>Die Angabe der Spalte <code>{columnName}</code></span>}/>

          <SqlStringMatchingResult matchName="Tabellen" matchSingularName="der Tabelle" matchingResult={tableComparison}/>

          <MatchingResultDisplay matchingResult={joinExpressionComparison} comparedItemPluralName={'Join-Bedingungen'}
                                 describeMatch={(m) => <BinaryExpressionMatchDisplay m={m}/>}
                                 describeNotMatchedItem={(condition) => <span>Die Angabe der Join-Bedingung <code>{condition}</code></span>}/>

          <MatchingResultDisplay matchingResult={whereComparison} comparedItemPluralName={'Bedingungen'}
                                 describeMatch={(m) => <BinaryExpressionMatchDisplay m={m}/>}
                                 describeNotMatchedItem={(condition) => <span>Die Angabe der Bedingung <code>{condition}</code></span>}/>

          {selectComparisons && <SelectAdditionalComparisonDisplay addComp={selectComparisons}/>}

          {insertComparison && <SqlStringMatchingResult matchName="Inserts" matchSingularName="Insert" matchingResult={insertComparison}/>}

        </>}
      </WithNullableNavigate>}
    </WithQuery>
  );
}
