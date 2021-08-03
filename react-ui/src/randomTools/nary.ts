export interface NaryIState {
  max: number;
  solutionString: string;
  checked: boolean;
  correct: boolean;
}

export interface NumberingSystem {
  radix: number;
  name: string;
}

export const BINARY_SYSTEM: NumberingSystem = {radix: 2, name: 'Binärsystem'};

export const DECIMAL_SYSTEM: NumberingSystem = {radix: 10, name: 'Dezimalsystem'};

export const HEXADECIMAL_SYSTEM: NumberingSystem = {radix: 16, name: 'Hexadezimalsystem',};

export const numberingSystems: { [key: number]: NumberingSystem } = {
  2: BINARY_SYSTEM,
  4: {radix: 4, name: 'Quaternärsystem'},
  8: {radix: 8, name: 'Oktalsystem'},
  16: HEXADECIMAL_SYSTEM
};
