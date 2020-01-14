import {Component, Input, OnInit} from '@angular/core';
import {IExercise} from '../../../../_interfaces/models';
import {getUmlExerciseTextParts, SelectableClass, UmlExerciseTextPart} from '../uml-tools';
import {IUmlExerciseContent} from '../uml-interfaces';

@Component({
  selector: 'it4all-uml-class-selection',
  templateUrl: './uml-class-selection.component.html'
})
export class UmlClassSelectionComponent implements OnInit {

  @Input() exercise: IExercise;

  exerciseContent: IUmlExerciseContent;

  selectableClasses: SelectableClass[];
  umlExerciseTextParts: UmlExerciseTextPart[];

  corrected = false;

  ngOnInit() {
    this.exerciseContent = this.exercise.content as IUmlExerciseContent;

    const {selectableClasses, textParts} = getUmlExerciseTextParts(this.exercise);

    this.selectableClasses = selectableClasses;
    this.umlExerciseTextParts = textParts;
  }

  toggleClassSelected(selectableClass: SelectableClass): void {
    selectableClass.selected = !selectableClass.selected;
  }

  correct(): void {
    console.info('TODO: implement!');

    this.corrected = true;
  }

}
