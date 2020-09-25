import {Component, Input} from '@angular/core';
import {ExerciseSolveFieldsFragment, UmlExerciseContentFragment} from '../../../../_services/apollo_services';
import {UmlExPart} from '../../../../_interfaces/graphql-types';

@Component({
  selector: 'it4all-uml-exercise',
  template: `
    <it4all-uml-class-selection
      *ngIf="isClassSelection()"
      [exerciseFragment]="exerciseFragment"
      [exerciseContent]="contentFragment">
    </it4all-uml-class-selection>

    <it4all-uml-diagram-drawing
      *ngIf="isDiagramDrawing()"
      [exerciseFragment]="exerciseFragment"
      [contentFragment]="contentFragment">
    </it4all-uml-diagram-drawing>

    <it4all-uml-member-allocation
      *ngIf="isMemberAllocation()"
      [exerciseFragment]="exerciseFragment"
      [contentFragment]="contentFragment">
    </it4all-uml-member-allocation>
  `
})
export class UmlExerciseComponent {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: UmlExerciseContentFragment;

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
