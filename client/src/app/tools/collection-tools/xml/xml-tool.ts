import {ExerciseTag, Tool} from '../../../_interfaces/tool';

export const XmlTool: Tool = new (
  class XmlToolClass extends Tool {
    constructor() {
      super('xml', 'XML', []);
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
