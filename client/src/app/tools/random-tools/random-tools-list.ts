import {ExerciseTag, Tool, ToolPart} from '../../_interfaces/tool';

// Boolean Algebra

export const BoolFillOutPart: ToolPart = {id: 'fillOut', name: 'Wahrheitstabellen ausfüllen'};
export const BoolCreatePart: ToolPart = {id: 'create', name: 'Boolesche Formel erstellen'};

export const BoolTool: Tool = new (
  class BoolToolClass extends Tool {
    constructor() {
      super('bool', 'Boolesche Algebra', [BoolFillOutPart, BoolCreatePart]);
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();

// Nary

export const NaryAdditionToolPart = {id: 'addition', name: 'Addition'};
export const NaryConversionToolPart = {id: 'conversion', name: 'Zahlenumwandlung'};
export const NaryTwoConversionToolPart = {id: 'twoConversion', name: 'Zahlenumwandlung im Zweiersystem'};

export const NaryTool: Tool = new (
  class NaryToolClass extends Tool {
    constructor() {
      super('nary', 'Zahlensysteme', [NaryAdditionToolPart, NaryConversionToolPart, NaryTwoConversionToolPart]);
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();

// All tools

export const randomTools: Tool[] = [BoolTool, NaryTool];
