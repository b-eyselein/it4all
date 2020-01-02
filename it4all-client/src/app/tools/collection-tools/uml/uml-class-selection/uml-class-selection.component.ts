import {Component, Input, OnInit} from '@angular/core';
import {IExercise, ISampleSolution, IUmlExerciseContent} from '../../../../_interfaces/models';
import {UmlClassSelectionTextPart} from '../uml-interfaces';
import {MatchResult, StringMatcher} from '../../../../matcher';

@Component({
  selector: 'it4all-uml-class-selection',
  templateUrl: './uml-class-selection.component.html'
})
export class UmlClassSelectionComponent implements OnInit {

  private readonly capWordTextSplitRegex: RegExp = /([A-Z][\wäöü?&;]*)/g;

  @Input() exercise: IExercise;
  @Input() exerciseContent: IUmlExerciseContent;

  allSelectableClassBaseForms: string[];

  classSelectionText: UmlClassSelectionTextPart[];

  selectedClasses: string[] = [];

  private getClassSelectionText(): UmlClassSelectionTextPart[] {
    const splitText: string[] = this.exercise.text
      .replace('\n', ' ')
      .split(this.capWordTextSplitRegex)
      .filter((s) => s.length > 0);

    this.allSelectableClassBaseForms = [
      ...new Set(
        splitText
          .filter((s) => s.match(this.capWordTextSplitRegex) && !this.exerciseContent.toIgnore.includes(s))
          .map((s) => this.exerciseContent.mappings[s] || s)
      )
    ];

    console.info(this.allSelectableClassBaseForms.join(' - '));

    return splitText.map((s) => {
      if (s.match(this.capWordTextSplitRegex) && !this.exerciseContent.toIgnore.includes(s)) {
        return {text: s, isSelectable: true, baseForm: this.exerciseContent.mappings[s]};
      } else {
        return {text: s, isSelectable: false};
      }
    });
  }

  ngOnInit() {
    this.classSelectionText = this.getClassSelectionText();
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
    const sampleSolution: ISampleSolution = this.exerciseContent.sampleSolutions[0];

    const classesToSelect: string[] = (sampleSolution.sample as any).classes.map((c) => c.name);

    const matchResult: MatchResult<string> = StringMatcher.match(this.selectedClasses, classesToSelect);

    console.error(JSON.stringify(matchResult, null, 2));
  }


}
