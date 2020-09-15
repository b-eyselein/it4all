import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ExerciseFile, FilesSolution} from "../../../../_interfaces/graphql-types";
import {CorrectionHelpers, HasSampleSolutions} from "../../_helpers/correction-helpers";

@Component({
  selector: 'it4all-files-exercise',
  templateUrl: './files-exercise.component.html'
})
export class FilesExerciseComponent extends CorrectionHelpers implements HasSampleSolutions<FilesSolution> {

  @Input() exerciseFiles: ExerciseFile[];

  @Input() defaultMode: string;

  @Input() sampleSolutions: FilesSolution[];

  @Input() hasLivePreview: boolean = false;

  @Input() isCorrecting = false;

  @Output() correct: EventEmitter<void> = new EventEmitter<void>();

  displaySampleSolutions = false;

  toggleSampleSolutions(): void {
    this.displaySampleSolutions = !this.displaySampleSolutions;
  }

}
