export interface Match<T> {
  first: T;
  second: T;
}

export interface MatchResult<T> {
  matches: Match<T> [];
  notMatchedInFirst: T[];
  notMatchedInSecond: T[];
}

export abstract class Matcher<T> {

  abstract canMatch(first: T, second: T): boolean;

  private findFirstMatchInSecond(first: T, priorInSecond: T[], remainingSecond: T[]): { match?: Match<T>, remainingSecond: T[] } {
    if (remainingSecond.length === 0) {
      return {remainingSecond: priorInSecond};
    } else {
      const [head, ...tail] = remainingSecond;

      if (this.canMatch(first, head)) {
        return {match: {first, second: head}, remainingSecond: [...priorInSecond, ...tail]};
      } else {
        return this.findFirstMatchInSecond(first, [...priorInSecond, head], tail);
      }
    }
  }

  private goInFirst(notMatchedInFirst: T[], remainingFirst: T[], notMatchedInSecond: T[], matches: Match<T>[]): MatchResult<T> {
    if (remainingFirst.length === 0) {
      return {matches, notMatchedInFirst, notMatchedInSecond};
    } else {
      const [head, ...tail] = remainingFirst;

      const {match, remainingSecond} = this.findFirstMatchInSecond(head, [], notMatchedInSecond);

      if (match) {
        return this.goInFirst(notMatchedInFirst, tail, remainingSecond, [...matches, match]);
      } else {
        return this.goInFirst([...notMatchedInFirst, head], tail, remainingSecond, matches);
      }
    }
  }

  match(firstColl: T[], secondColl: T[]): MatchResult<T> {
    return this.goInFirst([], firstColl, secondColl, []);
  }

}

export const StringMatcher: Matcher<string> = new class StringMatcherClass extends Matcher<string> {
  canMatch(first: string, second: string): boolean {
    return first === second;
  }
}();
