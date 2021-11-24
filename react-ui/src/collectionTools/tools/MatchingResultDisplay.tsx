import { useState } from 'react';
import {MatchType} from '../../graphql';
import classNames from 'classnames';
import {HiChevronDown, HiChevronRight} from 'react-icons/hi';

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
    <>
      <h3 className={classNames('subtitle', 'is-5', matchingResultSuccessful ? 'has-text-dark-success' : 'has-text-danger')}>
        <span onClick={() => setDisplayContent((value) => !value)}>{displayContent ? <HiChevronDown/> : <HiChevronRight/>}</span> &nbsp;
        Der Vergleich der {comparedItemPluralName} war {matchingResultSuccessful ? '' : ' nicht'} erfolgreich.
      </h3>

      {displayContent && <div className="content">
        <ul>

          {allMatches.map((m, index) => <li key={index} className={classNames({'has-text-dark-success': m.matchType === MatchType.SuccessfulMatch})}>
            {describeMatch(m)}
          </li>)}

          {notMatchedForUser.map((item, index) => <li className="has-text-danger" key={index}>{describeNotMatchedItem(item)} ist falsch.</li>)}

          {notMatchedForSample.map((item, index) => <li className="has-text-danger" key={index}>{describeNotMatchedItem(item)} fehlt.</li>)}

        </ul>
      </div>}
    </>
  );
}
