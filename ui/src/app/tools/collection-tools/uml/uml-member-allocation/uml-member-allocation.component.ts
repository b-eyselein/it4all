import {Component, Input, OnInit} from '@angular/core';
import {IExercise} from '../../../../_interfaces/models';
import {IUmlAttribute, IUmlClassDiagram, IUmlExerciseContent, IUmlMethod, UmlVisibility} from '../uml-interfaces';
import {distinctObjectArray, flatMapArray} from '../../../../helpers';

function printVisibility(v: UmlVisibility): string {
  switch (v) {
    case 'PUBLIC':
      return '+';
    case 'PACKAGE':
      return '~';
    case 'PROTECTED':
      return '#';
    case 'PRIVATE':
      return '-';
  }
}

function printAttribute(a: IUmlAttribute): string {
  return `${printVisibility(a.visibility)} ${a.memberName}: ${a.memberType}`;
}

function printMethod(m: IUmlMethod): string {
  return `${printVisibility(m.visibility)} ${m.memberName}(${m.parameters}): ${m.memberType}`;
}

interface MemberAllocMember {
  display: string;
  correct: boolean;
  selected: boolean;
}

interface MemberAllocClass {
  name: string;
  attributes: MemberAllocMember[]
  methods: MemberAllocMember[]
}

@Component({
  selector: 'it4all-uml-member-allocation',
  templateUrl: './uml-member-allocation.component.html'
})
export class UmlMemberAllocationComponent implements OnInit {

  @Input() exercise: IExercise;

  data: MemberAllocClass[];

  sample: IUmlClassDiagram;
  allAttributes: IUmlAttribute[];
  allMethods: IUmlMethod[];

  corrected = false;

  ngOnInit() {
    this.sample = (this.exercise.content as IUmlExerciseContent).sampleSolutions[0].sample as IUmlClassDiagram;

    this.allAttributes = distinctObjectArray(
      flatMapArray(this.sample.classes, (clazz) => clazz.attributes), (a) => a.memberName
    );

    this.allMethods = distinctObjectArray(
      flatMapArray(this.sample.classes, (clazz) => clazz.methods), (m) => m.memberName
    );

    this.data = this.sample.classes.map((clazz) => {
      return {
        name: clazz.name,
        attributes: this.allAttributes.map((attr) => {
          return {
            display: printAttribute(attr),
            correct: clazz.attributes.includes(attr),
            selected: false
          };
        }),
        methods: this.allMethods.map((met) => {
          return {
            display: printMethod(met),
            correct: clazz.methods.includes(met),
            selected: false
          };
        })
      };
    });
  }


  memberColor(m: MemberAllocMember): string {
    if (this.corrected) {
      return (m.correct === m.selected) ? 'has-text-dark-success' : 'has-text-danger';
    } else {
      return '';
    }
  }

  correct(): void {
    this.corrected = true;
  }

}