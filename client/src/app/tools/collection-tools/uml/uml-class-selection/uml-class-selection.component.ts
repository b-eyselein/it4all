import {Component, Input, OnInit} from '@angular/core';
import {IExercise, IUmlExerciseContent} from '../../../../_interfaces/models';
import {UmlClassSelectionTextPart} from '../uml-interfaces';
import {MatchResult, StringMatcher} from '../../../../matcher';

@Component({
  selector: 'it4all-uml-class-selection',
  templateUrl: './uml-class-selection.component.html'
})
export class UmlClassSelectionComponent implements OnInit {

  private readonly exTextSplitRegex = /([A-Z][\wäöü?&;]*)/g;

  @Input() exercise: IExercise;
  @Input() exerciseContent: IUmlExerciseContent;

  classSelectionText: UmlClassSelectionTextPart[];

  selectedClasses: string[] = [];

  private getClassSelectionText(): UmlClassSelectionTextPart[] {
    return this.exercise.text
      .replace('\n', ' ')
      .split(this.exTextSplitRegex)
      .filter((s) => s.length > 0)
      .map((s) => {
        if (s.match(this.exTextSplitRegex) && !this.exerciseContent.toIgnore.includes(s)) {
          return {text: s, isSelectable: true, baseForm: this.exerciseContent.mappings[s]};
        } else {
          return {text: s, isSelectable: false};
        }
      });
  }

  selectClass(clazz: UmlClassSelectionTextPart): void {
    const className = clazz.baseForm || clazz.text;

    if (this.selectedClasses.includes(className)) {
      this.selectedClasses = this.selectedClasses.filter((cn) => cn !== className);
    } else {
      this.selectedClasses.push(className);
      this.selectedClasses.sort();
    }
  }

  classIsSelected(clazz: UmlClassSelectionTextPart): boolean {
    return this.selectedClasses.includes(clazz.baseForm || clazz.text);
  }

  correct(): void {
    const classesToSelect: string[] = this.exerciseContent.sampleSolutions[0].sample.classes.map((c) => c.name);

    const matchResult: MatchResult<string> = StringMatcher.match(this.selectedClasses, classesToSelect);

    console.error(JSON.stringify(matchResult, null, 2));
  }

  ngOnInit() {
    this.classSelectionText = this.getClassSelectionText();
  }

}
