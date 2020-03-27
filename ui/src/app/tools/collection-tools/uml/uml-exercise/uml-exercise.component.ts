import {Component, Input} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {
  ExerciseSolveFieldsFragment,
  UmlExerciseContentSolveFieldsFragment
} from "../../../../_services/apollo_services";

@Component({
  selector: 'it4all-uml-exercise',
  templateUrl: './uml-exercise.component.html'
})
export class UmlExerciseComponent {

  @Input() part: ToolPart;
  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() umlExerciseContent: UmlExerciseContentSolveFieldsFragment;

}
