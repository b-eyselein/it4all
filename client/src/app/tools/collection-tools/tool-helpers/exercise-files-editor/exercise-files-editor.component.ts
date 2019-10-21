import {Component, Input, OnChanges, SimpleChanges, ViewEncapsulation} from '@angular/core';
import {ExerciseFile} from '../../../basics';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';

@Component({
  selector: 'app-exercise-files-editor',
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

  // noinspection JSUnusedGlobalSymbols
   editorOptions = getDefaultEditorOptions(this.mode);

   content = '';

  ngOnChanges(changes: SimpleChanges): void {
    if (this.exerciseFiles && this.exerciseFiles.length > 0) {
      const editableExerciseFiles = this.exerciseFiles.filter((ef) => ef.editable);

      if (editableExerciseFiles.length > 0) {
        this.updateEditor(editableExerciseFiles[0]);
      } else {
        this.updateEditor(this.exerciseFiles[0]);
      }
    }
  }

  changeFile($event: MouseEvent): void {
    if ($event.target) {
      const clickedElement: HTMLElement = ($event.target as HTMLElement);

      const fileName: string = clickedElement.textContent;

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

    this.editorOptions.mode = exerciseFile.fileType;
    this.editorOptions.readOnly = !exerciseFile.editable;
  }

}
