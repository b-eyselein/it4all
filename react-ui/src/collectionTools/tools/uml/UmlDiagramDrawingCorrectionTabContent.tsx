import {MatchType, UmlCorrectionMutation, UmlImplementationMatchFragment} from '../../../graphql';
import {useTranslation} from 'react-i18next';
import {UmlAssocMatchResult} from './UmlAssocMatchResult';
import {MatchingResultDisplay} from '../MatchingResultDisplay';
import {UmlClassMatchResult} from './UmlClassMatchingResult';
import classNames from 'classnames';

export function UmlImplMatchResult({m}: { m: UmlImplementationMatchFragment }): JSX.Element {

  const {matchType, userArg} = m;

  const isCorrect = matchType === MatchType.SuccessfulMatch;
  const directionCorrect = m.userArg.subClass === m.sampleArg.subClass;

  return (
    <div className={classNames('my-3', isCorrect ? 'has-text-dark-success' : 'has-text-danger')}>
      <p>Die Vererbung von <code>{userArg.subClass}</code> nach <code>{userArg.superClass}</code> ist {isCorrect ? '' : 'nicht'} korrekt.</p>

      {!isCorrect && <p className={directionCorrect ? 'has-text-dark-success' : 'has-text-danger'}>
        Die Vererbungsrichtung war {directionCorrect ? '' : 'nicht'} korrekt.
      </p>}
    </div>
  );
}


interface IProps {
  corrResult: UmlCorrectionMutation;
}

export function UmlDiagramDrawingCorrectionTabContent({corrResult}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  if (!corrResult.umlExercise) {
    return <div className="notification is-danger has-text-centered">{t('errorWhileCorrecting')}</div>;
  }

  const {result/*,solutionId,proficienciesUpdated*/} = corrResult.umlExercise.correct;

  const {implResult, classResult, assocResult/*, points, maxPoints*/} = result;

  return (
    <>
      {/*<SolutionSaved solutionSaved={solutionSaved}/>*/}

      {/*<PointsNotification points={points} maxPoints={maxPoints}/>*/}

      {classResult && <MatchingResultDisplay matchingResult={classResult} comparedItemPluralName={'Klassen'}
                                             describeMatch={(m) => <UmlClassMatchResult m={m}/>}
                                             describeNotMatchedItem={({name}) => <span>Die Klasse <code>{name}</code></span>}/>}

      {assocResult && <MatchingResultDisplay matchingResult={assocResult} comparedItemPluralName={'Assoziationen'}
                                             describeMatch={(m) => <UmlAssocMatchResult m={m}/>}
                                             describeNotMatchedItem={({firstEnd, secondEnd}) =>
                                               <span>Die Assoziation von <code>{firstEnd}</code> nach <code>{secondEnd}</code></span>}/>}

      {implResult && <MatchingResultDisplay matchingResult={implResult} comparedItemPluralName={'Vererbungen'}
                                            describeMatch={(m) => <UmlImplMatchResult m={m}/>}
                                            describeNotMatchedItem={({subClass, superClass}) =>
                                              <span>Die Verbung von <code>{subClass}</code> nach <code>{superClass}</code></span>}/>}
    </>
  );
}
