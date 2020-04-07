import {Component, Input, OnInit} from '@angular/core';
import {getUmlExerciseTextParts, SelectableClass, UmlDiagramDrawingHelpPart, UmlExerciseTextPart} from '../uml-tools';
import {ExerciseSolveFieldsFragment, UmlExerciseContentSolveFieldsFragment} from '../../../../_services/apollo_services';

@Component({
  selector: 'it4all-uml-class-selection',
  templateUrl: './uml-class-selection.component.html'
})
export class UmlClassSelectionComponent implements OnInit {

  readonly nextPart = UmlDiagramDrawingHelpPart;

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() exerciseContent: UmlExerciseContentSolveFieldsFragment;

  selectableClasses: SelectableClass[];
  umlExerciseTextParts: UmlExerciseTextPart[];

  corrected = false;

  ngOnInit() {
    const {selectableClasses, textParts} = getUmlExerciseTextParts(this.exerciseFragment, this.exerciseContent);

    this.selectableClasses = selectableClasses;
    this.umlExerciseTextParts = textParts;
  }

  getSelectedClasses(): SelectableClass[] {
    return this.selectableClasses.filter((sc) => sc.selected);
  }

  toggleClassSelected(selectableClass: SelectableClass): void {
    selectableClass.selected = !selectableClass.selected;
  }

  correct(): void {
    this.corrected = true;
  }

  getCorrectClasses(): SelectableClass[] {
    return this.selectableClasses.filter((sc) => sc.selected && sc.isCorrect);
  }

  getMissingClasses(): SelectableClass[] {
    return this.selectableClasses.filter((sc) => !sc.selected && sc.isCorrect);
  }

  getWrongClasses(): SelectableClass[] {
    return this.selectableClasses.filter((sc) => sc.selected && !sc.isCorrect);
  }

}
