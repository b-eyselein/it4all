import {CollectionTool, ExerciseTag} from '../../../_interfaces/tool';
import {RoseExerciseContent} from './rose-interfaces';

export const RoseTool: CollectionTool<RoseExerciseContent> = new (
  class RoseToolClass extends CollectionTool<RoseExerciseContent> {
    constructor() {
      super('rose', 'ROSE', [], 'alpha');
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
