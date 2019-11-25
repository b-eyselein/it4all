import {CollectionTool, ExerciseTag} from '../../../_interfaces/tool';

export const RoseTool: CollectionTool = new (
  class RoseToolClass extends CollectionTool {
    constructor() {
      super('rose', 'ROSE', [], 'alpha');
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
