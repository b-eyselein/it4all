import {Tool, ToolPart} from '../../_interfaces/tool';

export const BoolFillOutPart: ToolPart = {id: 'fillOut', name: 'Wahrheitstabellen ausfüllen'};
export const BoolCreatePart: ToolPart = {id: 'create', name: 'Boolesche Formel erstellen'};

export const BoolTool: Tool = {id: 'bool', name: 'Boolesche Algebra', parts: [BoolFillOutPart, BoolCreatePart]};

export const NaryAdditionToolPart = {id: 'addition', name: 'Addition'};
export const NaryConversionToolPart = {id: 'conversion', name: 'Zahlenumwandlung'};
export const NaryTwoConversionToolPart = {id: 'twoConversion', name: 'Zahlenumwandlung im Zweiersystem', disabled: true};

export const NaryTool: Tool = {
  id: 'nary',
  name: 'Zahlensysteme',
  parts: [NaryAdditionToolPart, NaryConversionToolPart, NaryTwoConversionToolPart],
};

export const randomTools: Tool[] = [BoolTool, NaryTool];
