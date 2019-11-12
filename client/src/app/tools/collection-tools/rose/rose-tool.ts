import {ExerciseTag, Tool} from '../../../_interfaces/tool';

export const RoseTool: Tool = new (
  class RoseToolClass extends Tool {
    constructor() {
      super('rose', 'ROSE', [], 'alpha', false, false, true);
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
