interface RandomToolPart {
  id: string;
  name: string;
  disabled?: boolean;
}

export interface RandomTool {
  id: string;
  name: string;
  parts: RandomToolPart[];
  hasLessons?: boolean;
}

