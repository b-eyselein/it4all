import React from 'react';
import {MatchType, SqlMatchingResultFragment} from '../../../graphql';
import classNames from 'classnames';
import {getCssClassForMatchType, getTextForMatchType} from './SqlStringMatchingResult';

interface IProps {
  matchName: string;
  matchSingularName: string;
  matchingResult: SqlMatchingResultFragment;
}

export function SqlMatchingResult({matchName, matchSingularName, matchingResult}: IProps): JSX.Element {

  const {/*allMatches,*/ notMatchedForUserString, notMatchedForSampleString} = matchingResult;

  const allMatches: any[] = matchingResult.allMatches;

  const successful = allMatches.every(({matchType}) => matchType === MatchType.SuccessfulMatch)
    && notMatchedForUserString.length === 0 && notMatchedForSampleString.length === 0;

  /*
  const describeMatch = () => <div>TODO!</div>;

  const describeNotMatchedItem = () => <div>TODO!</div>;

  const x = <MatchingResultDisplay matchingResult={matchingResult} comparedItemPluralName={matchName} describeMatch={} describeNotMatchedItem={}/>;

   */

  return (
    <>
      <div className={classNames(successful ? 'has-text-success' : 'has-text-danger')}>
        {/* ({matchingResult.points.toFixed(1)} / {matchingResult.maxPoints.toFixed(1)} P) */}
        Der Vergleich der {matchName} war {successful ? '' : 'nicht'} erfolgreich.
      </div>

      {!successful && <div className="content">
        <ul>
          {allMatches.map((match, index) => <li className={getCssClassForMatchType(match.matchType)} key={index}>
              Die Angabe {{matchSingularName}} <code>{match.userArgDescription}</code> {getTextForMatchType(match.matchType)}.
            </li>
          )}

          {notMatchedForUserString.map((notMatchedForUser, index) => <li className="has-text-danger" key={index}>
              Die Angabe {matchSingularName} <code>{notMatchedForUser}</code> ist falsch.
            </li>
          )}

          {notMatchedForSampleString.map((notMatchedForSample, index) => <li className="has-text-danger" key={index}>
            Die Angabe {matchSingularName} <code>{notMatchedForSample}</code> fehlt.
          </li>)}
        </ul>
      </div>
      }
    </>
  );

}
