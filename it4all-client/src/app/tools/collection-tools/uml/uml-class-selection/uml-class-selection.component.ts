import {Component, Input, OnInit} from '@angular/core';
import {IExercise, IUmlClassDiagram, IUmlExerciseContent} from '../../../../_interfaces/models';
import {distinctStringArray} from '../../../../helpers';

interface SelectableClass {
  name: string;
  selected: boolean;
  isCorrect: boolean;
}

export interface UmlClassSelectionTextPart {
  text: string;
  selectableClass?: SelectableClass;
}

const capWordTextSplitRegex: RegExp = /([A-Z][\wäöü?&;]*)/g;

@Component({
  selector: 'it4all-uml-class-selection',
  templateUrl: './uml-class-selection.component.html'
})
export class UmlClassSelectionComponent implements OnInit {


  @Input() exercise: IExercise;

  exerciseContent: IUmlExerciseContent;

  selectableClasses: SelectableClass[];
  classSelectionText: UmlClassSelectionTextPart[];

  corrected = false;

  private replaceWithMapping(str: string): string {
    const maybeMapping = this.exerciseContent.mappings.find((m) => m.key === str);
    return maybeMapping ? maybeMapping.value : str;
  }

  private isSelectable(s: string): boolean {
    return s.match(capWordTextSplitRegex) && !this.exerciseContent.toIgnore.includes(s);
  }

  private getClassSelectionText(): UmlClassSelectionTextPart[] {
    const splitText: string[] = this.exercise.text
      .replace('\n', ' ')
      .split(capWordTextSplitRegex)
      .filter((s) => s.length > 0);

    const sampleSolution = this.exerciseContent.sampleSolutions[0].sample as IUmlClassDiagram;

    this.selectableClasses = distinctStringArray(splitText.filter((s) => this.isSelectable(s))
      .map((s) => this.replaceWithMapping(s))).map((name) => {
        const isCorrect = sampleSolution.classes.find((c) => c.name === name) !== undefined;

        return {name, selected: false, isCorrect};
      }
    );

    return splitText.map<UmlClassSelectionTextPart>((text) => {
      if (this.isSelectable(text)) {
        const selectableClass = this.selectableClasses.find((c) => c.name === this.replaceWithMapping(text));
        return {text, selectableClass};
      } else {
        return {text};
      }
    });
  }

  ngOnInit() {
    this.exerciseContent = this.exercise.content as IUmlExerciseContent;
    this.classSelectionText = this.getClassSelectionText();
  }

  correct(): void {
    console.info('TODO: implement!');

    this.corrected = true;
  }

}
