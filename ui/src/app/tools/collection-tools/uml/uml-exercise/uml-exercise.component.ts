import {Component, Input} from '@angular/core';
import {IExercise} from '../../../../_interfaces/models';
import {ToolPart} from '../../../../_interfaces/tool';
import {UmlExerciseContentSolveFieldsFragment} from "../../../../_services/apollo_services";

@Component({
  selector: 'it4all-uml-exercise',
  templateUrl: './uml-exercise.component.html'
})
export class UmlExerciseComponent {

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  @Input() umlExerciseContent: UmlExerciseContentSolveFieldsFragment;

}
