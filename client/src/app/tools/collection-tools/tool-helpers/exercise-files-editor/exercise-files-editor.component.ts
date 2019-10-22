import {Component, Input, OnChanges, SimpleChanges, ViewEncapsulation} from '@angular/core';
import {ExerciseFile} from '../../../basics';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';

@Component({
  selector: 'it4all-exercise-files-editor',
  template: `
      <div class="tabs is-centered">
          <ul>
              <li *ngFor="let file of exerciseFiles" (click)="changeFile($event)" [class.is-active]="file.active">
                  <a [class.has-text-grey-light]="!file.editable" [title]="file.editable ? '' : 'Nicht editierbar'">{{file.name}}</a>
              </li>
          </ul>
      </div>
      <ngx-codemirror [options]="editorOptions" [(ngModel)]="content"></ngx-codemirror>`,
  styleUrls: ['./exercise-files.component.sass'],
  encapsulation: ViewEncapsulation.None // Style child component with same sass
})
export class ExerciseFilesEditorComponent implements OnChanges {

  @Input() exerciseFiles: ExerciseFile[];
  @Input() mode: string;

  currentFileName: string | undefined = undefined;

  // noinspection JSUnusedGlobalSymbols
  editorOptions = getDefaultEditorOptions(this.mode);

  private theContent = '';

  get content(): string {
    return this.theContent;
  }

  set content(val: string) {
    this.theContent = val;
    if (this.currentFileName) {
      this.saveFileContent();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.currentFileName) {
      this.saveFileContent();
    } else if (this.exerciseFiles && this.exerciseFiles.length > 0) {
      const editableExerciseFiles = this.exerciseFiles.filter((ef) => ef.editable);

      if (editableExerciseFiles.length > 0) {
        this.updateEditor(editableExerciseFiles[0]);
      } else {
        this.updateEditor(this.exerciseFiles[0]);
      }
    }
  }

  private saveFileContent(): void {
    this.exerciseFiles.find((f) => f.name === this.currentFileName).content = this.content;
  }

  changeFile($event: MouseEvent): void {
    if ($event.target) {
      const fileName: string = ($event.target as HTMLElement).textContent;

      // disable other files...
      this.exerciseFiles.forEach((ef) => ef.active = false);

      const exerciseFile: ExerciseFile | null = this.exerciseFiles.find((ef) => ef.name === fileName);

      if (exerciseFile) {
        this.updateEditor(exerciseFile);
      }
    }
  }

  updateEditor(exerciseFile: ExerciseFile): void {
    exerciseFile.active = true;

    this.content = exerciseFile.content;
    this.currentFileName = exerciseFile.name;

    this.editorOptions.mode = exerciseFile.fileType;
    this.editorOptions.readOnly = !exerciseFile.editable;
  }

}
