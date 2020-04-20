import {Component, Input, OnInit} from '@angular/core';
import {distinctObjectArray, flatMapArray} from '../../../../helpers';
import {
  ExerciseSolveFieldsFragment,
  UmlExerciseContentSolveFieldsFragment,
  UmlSampleSolutionFragment
} from '../../../../_services/apollo_services';
import {UmlAttribute, UmlClassDiagram, UmlMethod, UmlVisibility} from '../../../../_interfaces/graphql-types';

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

function printAttribute(a: UmlAttribute): string {
  return `${printVisibility(a.visibility)} ${a.memberName}: ${a.memberType}`;
}

function printMethod(m: UmlMethod): string {
  return `${printVisibility(m.visibility)} ${m.memberName}(${m.parameters}): ${m.memberType}`;
}

interface MemberAllocMember {
  display: string;
  correct: boolean;
  selected: boolean;
}

interface MemberAllocClass {
  name: string;
  attributes: MemberAllocMember[];
  methods: MemberAllocMember[];
}

@Component({
  selector: 'it4all-uml-member-allocation',
  templateUrl: './uml-member-allocation.component.html'
})
export class UmlMemberAllocationComponent implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: UmlExerciseContentSolveFieldsFragment;

  data: MemberAllocClass[];

  sample: UmlClassDiagram;
  allAttributes: UmlAttribute[];
  allMethods: UmlMethod[];

  corrected = false;

  ngOnInit() {
    this.sample = this.contentFragment.umlSampleSolutions[0].umlSampleSolution;

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
