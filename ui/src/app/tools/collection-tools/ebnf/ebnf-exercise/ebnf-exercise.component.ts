import {Component, Input, OnInit} from '@angular/core';
import {EbnfExerciseContentFragment, ExerciseSolveFieldsFragment} from "../../../../_services/apollo_services";

@Component({
  selector: 'it4all-ebnf-exercise',
  templateUrl: './ebnf-exercise.component.html'
})
export class EbnfExerciseComponent implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: EbnfExerciseContentFragment;

  constructor() {
  }

  ngOnInit(): void {
    console.info(JSON.stringify(this.contentFragment, null, 2));
  }

}
