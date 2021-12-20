export interface IExerciseFile {
  name: string;
  content: string;
  editable: boolean;
}

export interface IFilesSolution {
  files: IExerciseFile[];
}
