import {MatchResult, StringMatcher} from './matcher';

describe('StringMatcher', () => {
  it('should match empty lists', () => {
    const expected: MatchResult<string> = {
      matches: [], notMatchedInFirst: [], notMatchedInSecond: []
    };
    expect(StringMatcher.match([], [])).toEqual(expected);
  });

  it('should match strings only in first list', () => {
    const first: string[] = ['first', 'second', 'third'];
    const expected: MatchResult<string> = {
      matches: [], notMatchedInFirst: first, notMatchedInSecond: []
    };

    expect(StringMatcher.match(first, [])).toEqual(expected);
  });

  it('should match strings only in first list', () => {
    const second: string[] = ['first', 'second', 'third'];
    const expected: MatchResult<string> = {
      matches: [],
      notMatchedInFirst: [],
      notMatchedInSecond: second
    };

    expect(StringMatcher.match([], second)).toEqual(expected);
  });

  it('should perform matching', () => {
    const first: string[ ] = ['first', 'second', 'third', 'fourth'];
    const second: string [] = ['fourth', 'second', 'fifth', 'tenth'];

    const expected: MatchResult<string> = {
      matches: ['second', 'fourth'].map((s) => {
        return {first: s, second: s};
      }),
      notMatchedInFirst: ['first', 'third'],
      notMatchedInSecond: ['fifth', 'tenth']
    };

    expect(StringMatcher.match(first, second)).toEqual(expected);
  });

});
