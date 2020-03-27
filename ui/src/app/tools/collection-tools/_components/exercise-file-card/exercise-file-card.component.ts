import {Component, Input, OnInit} from '@angular/core';
import {ExerciseFile} from '../../../../_services/apollo_services';

@Component({
  selector: 'it4all-exercise-file-card',
  templateUrl: './exercise-file-card.component.html'
})
export class ExerciseFileCardComponent implements OnInit {

  @Input() exerciseFile: ExerciseFile;

  constructor() {
  }

  ngOnInit() {
  }

}
