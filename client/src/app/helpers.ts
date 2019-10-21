export function randomInt(minInclusive: number, maxExclusive: number): number {
  const intMax = Math.floor(maxExclusive);
  const intMin = Math.floor(minInclusive);
  return Math.floor(Math.random() * (intMax - intMin)) + intMin;
}

export function takeRandom<T>(from: T[]): T {
  return from[Math.floor(Math.random() * from.length)];
}

export function flatMapArray<T>(ts: T[], f: (T) => T[]): T[] {
  return ts.reduce((acc, t) => acc.concat(f(t)), []);
}

export function stringArraysEqual(first: string[], second: string[]): boolean {
  return first.length === second.length && first.every((value, index) => value === second[index]);
}
