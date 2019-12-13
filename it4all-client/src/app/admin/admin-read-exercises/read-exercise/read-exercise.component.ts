import {Component, Input} from '@angular/core';
import {ApiService} from '../../../tools/collection-tools/_services/api.service';
import {IExercise} from '../../../_interfaces/models';

@Component({
  selector: 'it4all-read-exercise',
  templateUrl: './read-exercise.component.html',
  styles: [`
    .exerciseJson {
      max-height: 400px;
      overflow: auto;
    }`]
})
export class ReadExerciseComponent {

  @Input() exercise: IExercise;

  saved = false;

  constructor(private apiService: ApiService) {
  }

  displayExercise(exercise): string {
    return JSON.stringify(exercise, null, 2);
  }

  saveExercise(): void {
    this.apiService.adminUpsertExercise(this.exercise.toolId, this.exercise.collectionId, this.exercise)
      .subscribe((saved) => this.saved = saved);
  }

}
