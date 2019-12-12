import {Component, Input} from '@angular/core';
import {CollectionTool} from '../../../_interfaces/tool';
import {ApiService} from '../../../tools/collection-tools/_services/api.service';
import {IExercise, IExerciseCollection} from '../../../_interfaces/models';

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

  @Input() tool: CollectionTool;
  @Input() collection: IExerciseCollection;
  @Input() exercise: IExercise;

  saved = false;

  constructor(private apiService: ApiService) {
  }

  displayExercise(exercise): string {
    return JSON.stringify(exercise, null, 2);
  }

  saveExercise(): void {
    this.apiService.adminUpsertExercise(this.tool.id, this.collection.id, this.exercise)
      .subscribe((saved) => this.saved = saved);
  }

}
