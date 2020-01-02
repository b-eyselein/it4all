import {Component, Input, OnInit} from '@angular/core';
import {IExercise, ISampleSolution, IUmlClassDiagram, IUmlExerciseContent} from '../../../../_interfaces/models';
import {UmlClassSelectionTextPart} from '../uml-interfaces';
import {MatchResult, StringMatcher} from '../../../../matcher';
import {distinctStringArray} from '../../../../helpers';

interface SelectableClass {
  name: string;
  selected: boolean;
  isCorrect: boolean;
}

@Component({
  selector: 'it4all-uml-class-selection',
  templateUrl: './uml-class-selection.component.html'
})
export class UmlClassSelectionComponent implements OnInit {

  private readonly capWordTextSplitRegex: RegExp = /([A-Z][\wäöü?&;]*)/g;

  @Input() exercise: IExercise;
  @Input() exerciseContent: IUmlExerciseContent;

  selectableClasses: SelectableClass[];

  classSelectionText: UmlClassSelectionTextPart[];

  selectedClasses: string[] = [];

  corrected = false;

  private getMapping(str: string): string | undefined {
    const maybeMapping = this.exerciseContent.mappings.find((m) => m.key === str);
    return maybeMapping ? maybeMapping.value : undefined;
  }

  private getClassSelectionText(): UmlClassSelectionTextPart[] {
    const splitText: string[] = this.exercise.text
      .replace('\n', ' ')
      .split(this.capWordTextSplitRegex)
      .filter((s) => s.length > 0);

    const sampleSolution = this.exerciseContent.sampleSolutions[0].sample as IUmlClassDiagram;

    this.selectableClasses = distinctStringArray(
      splitText
        .filter((s) => s.match(this.capWordTextSplitRegex) && !this.exerciseContent.toIgnore.includes(s))
        .map((s) => this.getMapping(s) || s)
    ).map((name) => {
        const isCorrect = sampleSolution.classes.find((c) => c.name === name) !== undefined;

        return {name, selected: false, isCorrect};
      }
    );

    return splitText.map((s) => {
      if (s.match(this.capWordTextSplitRegex) && !this.exerciseContent.toIgnore.includes(s)) {
        return {text: s, isSelectable: true, baseForm: this.getMapping(s)};
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

    const selectableClass = this.selectableClasses.find((sc) => sc.name === (clazz.baseForm || clazz.text));
    selectableClass.selected = !selectableClass.selected;

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
    this.corrected = true;

    const sampleSolution: ISampleSolution = this.exerciseContent.sampleSolutions[0];

    const classesToSelect: string[] = (sampleSolution.sample as any).classes.map((c) => c.name);

    const matchResult: MatchResult<string> = StringMatcher.match(this.selectedClasses, classesToSelect);

    console.error(JSON.stringify(matchResult, null, 2));
  }

}
