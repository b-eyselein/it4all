export function randomInt(minInclusive: number, maxExclusive: number): number {
  const intMax = Math.floor(maxExclusive);
  const intMin = Math.floor(minInclusive);
  return Math.floor(Math.random() * (intMax - intMin)) + intMin;
}

export function takeRandom<T>(from: T[]): T {
  return from[Math.floor(Math.random() * from.length)];
}

export function flatMapArray<T, U>(ts: T[], f: (t: T) => U[]): U[] {
  return ts.reduce((acc, t) => acc.concat(f(t)), []);
}

export function distinctStringArray(ts: string[]): string[] {
  return [...new Set(ts)];
}

export function distinctObjectArray<T, K>(ts: T[], key: (t: T) => K): T[] {
  const helperMap: Map<K, T> = new Map<K, T>();

  for (const t of ts) {
    const tKey = key(t);
    if (!helperMap.has(tKey)) {
      helperMap.set(tKey, t);
    }
  }

  return Array.from(helperMap.values());
}
