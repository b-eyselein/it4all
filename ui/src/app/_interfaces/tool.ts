export type ToolStatus = 'live' | 'alpha' | 'beta';

export type SuccessType = 'ERROR' | 'NONE' | 'PARTIALLY' | 'COMPLETE';

export interface ToolPart {
  id: string;
  name: string;
  disabled?: boolean;
}

export abstract class RandomTool {
  protected constructor(
    public id: string,
    public name: string,
    public parts: ToolPart[],
    public status: ToolStatus,
    public hasLessons: boolean = false
  ) {
  }
}

