import {Component, Input} from '@angular/core';
import {ExerciseSolveFieldsFragment, UmlExerciseContentSolveFieldsFragment} from '../../../../_services/apollo_services';
import {UmlExPart} from '../../../../_interfaces/graphql-types';

@Component({
  selector: 'it4all-uml-exercise',
  templateUrl: './uml-exercise.component.html'
})
export class UmlExerciseComponent {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: UmlExerciseContentSolveFieldsFragment;

  isClassSelection(): boolean {
    return this.contentFragment.part === UmlExPart.ClassSelection;
  }

  isDiagramDrawing(): boolean {
    return [UmlExPart.DiagramDrawingHelp, UmlExPart.DiagramDrawing].includes(this.contentFragment.part);
  }

  isMemberAllocation(): boolean {
    return this.contentFragment.part === UmlExPart.MemberAllocation;
  }

}
