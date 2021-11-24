import {MatchType, StringMatchFragment, StringMatchingResultFragment} from '../../../graphql';
import {MatchingResultDisplay} from '../MatchingResultDisplay';

interface IProps {
  matchName: string;
  matchSingularName: string;
  matchingResult: StringMatchingResultFragment;
}

export function getTextForMatchType(matchType: MatchType): string {
  return matchType === MatchType.SuccessfulMatch ? 'ist korrekt' : 'ist nicht komplett richtig.';
}

export function SqlStringMatchingResult({matchName, matchSingularName, matchingResult}: IProps): JSX.Element {

  const describeMatch = ({matchType, userArg}: StringMatchFragment) =>
    <span>Die Angabe {matchSingularName} <code>{userArg}</code> {getTextForMatchType(matchType)}.</span>;

  const describeNotMatchedItem = (s: string) => <span>Die Angabe {matchSingularName} <code>{s}</code></span>;

  return <MatchingResultDisplay matchingResult={matchingResult} comparedItemPluralName={matchName}
                                describeMatch={describeMatch} describeNotMatchedItem={describeNotMatchedItem}/>;

}
