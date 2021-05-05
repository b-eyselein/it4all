export const minimalMax = 16;
export const maximalMax = 4294967296; // Math.pow(2, 32)


export interface NumberingSystem {
  radix: number;
  name: string;
  allowedDigits: string;
}

export const BINARY_SYSTEM: NumberingSystem = {radix: 2, name: 'Binärsystem', allowedDigits: '01'};

export const DECIMAL_SYSTEM: NumberingSystem = {radix: 10, name: 'Dezimalsystem', allowedDigits: '0123456789'};

export const HEXADECIMAL_SYSTEM: NumberingSystem = {
  radix: 16,
  name: 'Hexadezimalsystem',
  allowedDigits: '0123456789ABCDEF'
};

export const allNumberingSystems: NumberingSystem[] = [
  BINARY_SYSTEM,
  {radix: 4, name: 'Quaternärsystem', allowedDigits: '0123'},
  {radix: 8, name: 'Oktalsystem', allowedDigits: '01234567'},
  HEXADECIMAL_SYSTEM
];

export interface NaryReadOnlyNumberInput {
  decimalNumber: number;
  numberingSystem: NumberingSystem;
  fieldId: string;
  labelContent: string;
  maxValueForDigits: number;
}

export abstract class NaryComponentBase {

  max: number;

  protected constructor(defaultMax: number = 256) {
    this.max = defaultMax;
  }

  abstract update(): void;

  halveMax(): void {
    this.max = Math.max(minimalMax, this.max / 2);
    this.update();
  }

  doubleMax(): void {
    this.max = Math.min(maximalMax, this.max * 2);
    this.update();
  }

}
