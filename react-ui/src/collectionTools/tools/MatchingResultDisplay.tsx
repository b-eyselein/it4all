import {useState} from 'react';
import {MatchType} from '../../graphql';
import classNames from 'classnames';
import {textColors} from '../../consts';

export interface GenericMatch<T> {
  matchType: MatchType;
  userArg: T;
  sampleArg: T;
}

export interface GenericMatchingResult<T, M extends GenericMatch<T>> {
  allMatches: M[];
  notMatchedForUser: T[];
  notMatchedForSample: T[];
}

export function isEmpty<T, M extends GenericMatch<T>>({allMatches, notMatchedForUser, notMatchedForSample}: GenericMatchingResult<T, M>): boolean {
  return allMatches.length === 0 && notMatchedForUser.length === 0 && notMatchedForSample.length === 0;
}

interface IProps<T, M extends GenericMatch<T>> {
  matchingResult: GenericMatchingResult<T, M>;
  comparedItemPluralName: string;
  describeMatch: (m: M) => JSX.Element;
  describeNotMatchedItem: (t: T) => JSX.Element;
}

export function MatchingResultDisplay<T, M extends GenericMatch<T>>(iprops: IProps<T, M>): JSX.Element {

  const {matchingResult, comparedItemPluralName, describeMatch, describeNotMatchedItem} = iprops;

  const {allMatches, notMatchedForUser, notMatchedForSample} = matchingResult;

  const matchingResultSuccessful = allMatches.every(({matchType}) => matchType === MatchType.SuccessfulMatch)
    && notMatchedForUser.length === 0 && notMatchedForSample.length === 0;

  const [displayContent, setDisplayContent] = useState(!matchingResultSuccessful);

  return (
    <div className="my-4">
      <h3 className={classNames('text-xl', matchingResultSuccessful ? textColors.correct : textColors.inCorrect)}
          onClick={() => setDisplayContent((value) => !value)}>
        {displayContent ? <span>&#x2228;</span> : <span>&#xff1e;</span>}
        &nbsp;
        Der Vergleich der {comparedItemPluralName} war {matchingResultSuccessful ? '' : ' nicht'} erfolgreich.
      </h3>

      {displayContent && <ul className="ml-4 list-disc list-inside">
        {allMatches.map((m, index) => <li key={index} className={classNames('my-2', {[textColors.correct]: m.matchType === MatchType.SuccessfulMatch})}>
          {describeMatch(m)}
        </li>)}

        {notMatchedForUser.map((item, index) => <li className={classNames('my-2', textColors.inCorrect)} key={index}>{describeNotMatchedItem(item)} ist falsch.</li>)}

        {notMatchedForSample.map((item, index) => <li className={classNames('my-2', textColors.inCorrect)} key={index}>{describeNotMatchedItem(item)} fehlt.</li>)}
      </ul>}
    </div>
  );
}
