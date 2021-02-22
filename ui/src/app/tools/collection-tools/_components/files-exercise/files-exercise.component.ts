import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {CorrectionHelpers, HasSampleSolutions} from "../../_helpers/correction-helpers";
import {TabsComponent} from "../../../../shared/tabs/tabs.component";
import {ExerciseFile, FilesSolution} from "../../../../_services/apollo_services";

@Component({
  selector: 'it4all-files-exercise',
  templateUrl: './files-exercise.component.html',
  styles: [
    `.overflow-hidden {
      max-height: 750px;
      overflow-y: auto;
    }`
  ]
})
export class FilesExerciseComponent
  extends CorrectionHelpers
  implements HasSampleSolutions<FilesSolution> {

  @Input() exerciseFiles: ExerciseFile[];

  @Input() defaultMode: string;

  @Input() sampleSolutions: FilesSolution[];

  @Input() hasLivePreview: boolean = false;

  @Input() isCorrecting = false;

  @Output() correct: EventEmitter<void> = new EventEmitter<void>();

  displaySampleSolutions = false;

  @ViewChild(TabsComponent) tabsComponent: TabsComponent;

  toggleSampleSolutions(): void {
    this.displaySampleSolutions = !this.displaySampleSolutions;
  }

  performCorrection(): void {
    this.correct.emit();
  }

  toggleCorrectionTab() {
    if (this.tabsComponent) {
      this.tabsComponent.selectTabByTitle(this.correctionTabTitle);
    }
  }

}
