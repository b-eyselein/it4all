import {RandomTool} from './random-tool';
import {ToolState} from "../../_interfaces/graphql-types";

export const randomTools: RandomTool[] = [
  {
    id: 'bool',
    name: 'Boolesche Algebra',
    parts: [
      {id: 'fillOut', name: 'Wahrheitstabellen ausfüllen'},
      {id: 'create', name: 'Boolesche Formel erstellen'},
      {id: 'drawing', name: 'Schaltkreis zeichnen', disabled: true}
    ]
  },
  {
    id: 'nary',
    name: 'Zahlensysteme',
    parts: [
      {id: 'addition', name: 'Addition'},
      {id: 'conversion', name: 'Zahlenumwandlung'},
      {id: 'twoConversion', name: 'Zahlenumwandlung im Zweiersystem'}
    ]
  }
];
