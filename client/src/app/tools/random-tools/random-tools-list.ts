import {Tool, ToolPart} from '../../_interfaces/tool';

// Boolean Algebra

export const BoolFillOutPart: ToolPart = {id: 'fillOut', name: 'Wahrheitstabellen ausfüllen'};
export const BoolCreatePart: ToolPart = {id: 'create', name: 'Boolesche Formel erstellen'};

export const BoolTool: Tool = new Tool('bool', 'Boolesche Algebra', [BoolFillOutPart, BoolCreatePart]);

// Nary

export const NaryAdditionToolPart = {id: 'addition', name: 'Addition'};
export const NaryConversionToolPart = {id: 'conversion', name: 'Zahlenumwandlung'};
export const NaryTwoConversionToolPart = {id: 'twoConversion', name: 'Zahlenumwandlung im Zweiersystem'};

export const NaryTool: Tool = new Tool('nary', 'Zahlensysteme', [NaryAdditionToolPart, NaryConversionToolPart, NaryTwoConversionToolPart]);

// All tools

export const randomTools: Tool[] = [BoolTool, NaryTool];
