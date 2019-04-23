export interface ExerciseFile {
    name: string
    content: string
    fileType: string
    editable: boolean
}

export interface IdeWorkspace {
    filesNum: number
    files: ExerciseFile[]
}
