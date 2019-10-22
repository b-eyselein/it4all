export interface NumberingSystem {
  radix: number;
  name: string;
  allowedDigits: string;
}

export const BINARY_SYSTEM: NumberingSystem = {radix: 2, name: 'Binärsystem', allowedDigits: '01'};

export const DECIMAL_SYSTEM: NumberingSystem = {radix: 10, name: 'Dezimalsystem', allowedDigits: '0123456789'};

export const HEXADECIMAL_SYSTEM: NumberingSystem = {radix: 16, name: 'Hexadezimalsystem', allowedDigits: '0123456789ABCDEF'};

export const NUMBERING_SYSTEMS: NumberingSystem[] = [
  BINARY_SYSTEM,
  {radix: 4, name: 'Quaternärsystem', allowedDigits: '0123'},
  {radix: 8, name: 'Oktalsystem', allowedDigits: '01234567'},
  HEXADECIMAL_SYSTEM
];

export class NaryReadOnlyNumberInput {

  private stringValue?: string;

  constructor(
    public decimalNumber: number,
    public numberingSystem: NumberingSystem,
    public fieldId: string,
    public labelContent: string,
    public fieldPlaceholder: string | null = null,
  ) {
    this.stringValue = this.decimalNumber ? this.decimalNumber.toString(10) : undefined;
  }
}
