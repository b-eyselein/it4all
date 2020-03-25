export type ToolStatus = 'live' | 'alpha' | 'beta';

export type SuccessType = 'ERROR' | 'NONE' | 'PARTIALLY' | 'COMPLETE';

export interface ToolPart {
  id: string;
  name: string;
  disabled?: boolean;
}

export abstract class Tool {
  constructor(
    public id: string,
    public name: string,
    public parts: ToolPart[],
    public status: ToolStatus,
    public hasLessons: boolean = false
  ) {
  }

  hasPlayground(): boolean {
    return false;
  }
}

export abstract class RandomTool extends Tool {

}

export abstract class CollectionTool extends Tool {

  exerciseHasPart(exerciseContent: any, part: ToolPart): boolean {
    return true;
  }

}
