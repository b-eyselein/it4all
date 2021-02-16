import {Component, Input, OnChanges, OnInit, SimpleChanges, ViewEncapsulation} from '@angular/core';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {ExerciseFileFragment} from '../../../../_services/apollo_services';

interface ActivatableExerciseFile {
  file: ExerciseFileFragment;
  active: boolean;
}

@Component({
  selector: 'it4all-exercise-files-editor',
  templateUrl: './exercise-files-editor.component.html',
  styleUrls: ['./exercise-files-editor.component.sass'],
  encapsulation: ViewEncapsulation.None // Style child component with same sass
})
export class ExerciseFilesEditorComponent implements OnChanges, OnInit {

  @Input() exerciseFileFragments: ExerciseFileFragment[];
  @Input() mode: string;

  activatableExerciseFiles: ActivatableExerciseFile[];

  currentFileName: string | undefined = undefined;

  editorOptions;

  private theContent = '';

  get content(): string {
    return this.theContent;
  }

  /**
   * called only from CordMirror editor
   */
  set content(newContent: string) {
    this.theContent = newContent;
    this.saveEditorContent();
  }

  private saveEditorContent(): void {
    if (this.currentFileName) {
      this.activatableExerciseFiles.find((f) => f.file.name === this.currentFileName).file.content = this.content;
    }
  }

  ngOnInit() {
    this.editorOptions = getDefaultEditorOptions(this.mode);
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.activatableExerciseFiles = this.exerciseFileFragments.map((file) => {
      // Delete __typename field that was added by apollo
      delete file.__typename;
      return {file, active: false};
    });

    this.updateFirstFile();
  }

  private updateFirstFile(): void {
    if (this.activatableExerciseFiles && this.activatableExerciseFiles.length > 0) {
      // console.log(this.currentFileName);

      if (!this.currentFileName) {
        const editableExerciseFile: ActivatableExerciseFile | undefined =
          this.activatableExerciseFiles.find((ef) => ef.file.editable);

        if (editableExerciseFile) {
          this.updateEditor(editableExerciseFile, false);
        } else {
          this.updateEditor(this.activatableExerciseFiles[0], false);
        }
      } else {
        const currentFile = this.activatableExerciseFiles.find((f) => f.file.name === this.currentFileName);
        this.updateEditor(currentFile, false);
      }
    }
  }

  private updateEditor(exerciseFile: ActivatableExerciseFile, saveContent: boolean = true): void {
    if (saveContent) {
      this.saveEditorContent();
    }

    exerciseFile.active = true;

    this.theContent = exerciseFile.file.content;
    this.currentFileName = exerciseFile.file.name;

    this.editorOptions.mode = exerciseFile.file.fileType;
    this.editorOptions.readOnly = !exerciseFile.file.editable;
  }

  changeFile($event: MouseEvent): void {
    const fileName: string = ($event.target as HTMLElement).textContent;

    // disable other files...
    this.activatableExerciseFiles.forEach((ef) => {
      ef.active = false;
    });

    const exerciseFile = this.activatableExerciseFiles.find((ef) => ef.file.name === fileName);

    if (exerciseFile) {
      this.updateEditor(exerciseFile);
    }
  }

}
