import {IExercise} from './models';

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
  ) {
  }
}

export abstract class RandomTool extends Tool {

}

export abstract class CollectionTool extends Tool {

  exerciseHasPart(exercise: IExercise, part: ToolPart): boolean {
    return true;
  }

  abstract processTagString(tag: string): ExerciseTag;

}
