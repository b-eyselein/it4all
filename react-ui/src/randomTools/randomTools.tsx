import React from 'react';
import {BoolFillOut} from './BoolFillOut';
import {BoolCreate} from './BoolCreate';
import {NaryAddition} from './NaryAddition';
import {NaryConversion} from './NaryConversion';
import {NaryTwoConversion} from './NaryTwoConversion';

interface RandomToolPart {
  id: string;
  name: string;
  component: () => JSX.Element;
  disabled?: boolean;
}

export interface RandomTool {
  id: string;
  name: string;
  parts: RandomToolPart[];
}

export const boolRandomTool: RandomTool = {
  id: 'bool',
  name: 'Boolesche Algebra',
  parts: [
    {id: 'fillOut', name: 'Wahrheitstabellen ausfüllen', component: BoolFillOut},
    {id: 'create', name: 'Boolesche Formel erstellen', component: BoolCreate},
    {id: 'drawing', name: 'Schaltkreis zeichnen', disabled: true, component: () => <div>TODO!</div>}
  ]
};

export const naryRandomTool: RandomTool = {
  id: 'nary',
  name: 'Zahlensysteme',
  parts: [
    {id: 'addition', name: 'Addition', component: NaryAddition},
    {id: 'conversion', name: 'Zahlenumwandlung', component: NaryConversion},
    {id: 'twoConversion', name: 'Zahlenumwandlung im Zweiersystem', component: NaryTwoConversion}
  ]
};

export const randomTools: RandomTool[] = [boolRandomTool, naryRandomTool];
