interface RandomToolPart {
  id: string;
  name: string;
  disabled?: boolean;
}

export interface RandomTool {
  id: string;
  name: string;
  parts: RandomToolPart[];
  hasLessons?: boolean;
}

export const boolRandomTool: RandomTool = {
  id: 'bool',
  name: 'Boolesche Algebra',
  parts: [
    {id: 'fillOut', name: 'Wahrheitstabellen ausfüllen'},
    {id: 'create', name: 'Boolesche Formel erstellen'},
    {id: 'drawing', name: 'Schaltkreis zeichnen', disabled: true}
  ]
};

export const naryRandomTool: RandomTool = {
  id: 'nary',
  name: 'Zahlensysteme',
  parts: [
    {id: 'addition', name: 'Addition'},
    {id: 'conversion', name: 'Zahlenumwandlung'},
    {id: 'twoConversion', name: 'Zahlenumwandlung im Zweiersystem'}
  ]
};

export const randomTools: RandomTool[] = [boolRandomTool, naryRandomTool];
