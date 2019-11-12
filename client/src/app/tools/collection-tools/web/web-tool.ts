import {ExerciseTag, Tool} from '../../../_interfaces/tool';

export const WebTool: Tool = new (
  class WebToolClass extends Tool {
    constructor() {
      super('web', 'Web', [], null, true, true);
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
