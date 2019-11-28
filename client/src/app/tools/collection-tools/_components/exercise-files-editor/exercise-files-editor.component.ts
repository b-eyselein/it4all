import {Component, Input, OnChanges, SimpleChanges, ViewEncapsulation} from '@angular/core';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {IExerciseFile} from '../../../../_interfaces/models';

@Component({
  selector: 'it4all-exercise-files-editor',
  templateUrl: './exercise-files-editor.component.html',
  styleUrls: ['./exercise-files-editor.component.sass'],
  // Style child component with same sass
  encapsulation: ViewEncapsulation.None
})
export class ExerciseFilesEditorComponent implements OnChanges {

  @Input() exerciseFiles: IExerciseFile[];
  @Input() mode: string;

  currentFileName: string | undefined = undefined;

  // noinspection JSUnusedGlobalSymbols
  editorOptions = getDefaultEditorOptions(this.mode);

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
      this.exerciseFiles.find((f) => f.name === this.currentFileName).content = this.content;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.currentFileName && this.exerciseFiles && this.exerciseFiles.length > 0) {
      const editableExerciseFiles = this.exerciseFiles.filter((ef) => ef.editable);

      if (editableExerciseFiles.length > 0) {
        this.updateEditor(editableExerciseFiles[0]);
      } else {
        this.updateEditor(this.exerciseFiles[0]);
      }
    }
  }

  private updateEditor(exerciseFile: IExerciseFile): void {
    this.saveEditorContent();

    exerciseFile.active = true;

    this.theContent = exerciseFile.content;
    this.currentFileName = exerciseFile.name;

    this.editorOptions.mode = exerciseFile.fileType;
    this.editorOptions.readOnly = !exerciseFile.editable;
  }

  changeFile($event: MouseEvent): void {
    const fileName: string = ($event.target as HTMLElement).textContent;

    // disable other files...
    this.exerciseFiles.forEach((ef) => ef.active = false);

    const exerciseFile: IExerciseFile | null = this.exerciseFiles.find((ef) => ef.name === fileName);

    if (exerciseFile) {
      this.updateEditor(exerciseFile);
    }
  }

}
