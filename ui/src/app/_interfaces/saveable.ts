export interface Saveable<T> {
  stringified: string;
  value: T;
  title: string;
  saved: boolean;
}

