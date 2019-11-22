import {Exercise, ExerciseContent} from './exercise';

export type ToolStatus = 'live' | 'alpha' | 'beta';

export type SuccessType = 'ERROR' | 'NONE' | 'PARTIALLY' | 'COMPLETE';

export interface ToolPart {
  id: string;
  name: string;
  disabled?: boolean;
}

export interface ExerciseTag {
  title: string;
  label: string;
}

export abstract class Tool {
  constructor(
    public id: string,
    public name: string,
    public parts: ToolPart[],
    public status: ToolStatus,
    public hasPlayground?: boolean,
    public hasLivePreview?: boolean,
  ) {
  }
}

export abstract class RandomTool extends Tool {

}

export abstract class CollectionTool<EC extends ExerciseContent> extends Tool {

  exerciseHasPart(exercise: Exercise<EC>, part: ToolPart): boolean {
    return true;
  }

  abstract processTagString(tag: string): ExerciseTag;

}
