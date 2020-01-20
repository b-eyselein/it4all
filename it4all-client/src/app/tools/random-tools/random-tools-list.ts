import {RandomTool, ToolPart} from '../../_interfaces/tool';

// Boolean Algebra

export const BoolFillOutPart: ToolPart = {id: 'fillOut', name: 'Wahrheitstabellen ausfüllen'};
export const BoolCreatePart: ToolPart = {id: 'create', name: 'Boolesche Formel erstellen'};

export const BoolTool: RandomTool = new class BoolToolClass extends RandomTool {
  constructor() {
    super('bool', 'Boolesche Algebra', [BoolFillOutPart, BoolCreatePart], 'live');
  }
}();

// Nary

export const NaryAdditionToolPart = {id: 'addition', name: 'Addition'};
export const NaryConversionToolPart = {id: 'conversion', name: 'Zahlenumwandlung'};
export const NaryTwoConversionToolPart = {id: 'twoConversion', name: 'Zahlenumwandlung im Zweiersystem'};

export const NaryTool: RandomTool = new class NaryToolClass extends RandomTool {
  constructor() {
    super('nary', 'Zahlensysteme', [NaryAdditionToolPart, NaryConversionToolPart, NaryTwoConversionToolPart], 'live');
  }
}();

// All tools

export const randomTools: RandomTool[] = [BoolTool, NaryTool];
