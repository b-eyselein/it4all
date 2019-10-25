import {Component, HostListener, OnInit} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {BoolCreatePart} from '../../random-tools-list';
import {parseBooleanFormula} from '../_model/boolean-formula-parser';
import {BoolComponentHelper} from '../_model/bool-component-helper';
import {BooleanNode, calculateAssignments} from '../_model/bool-node';
import {generateBooleanFormula} from '../_model/bool-formula';
import {Router} from '@angular/router';

@Component({templateUrl: './bool-create.component.html'})
export class BoolCreateComponent extends BoolComponentHelper implements OnInit {

  readonly part: ToolPart = BoolCreatePart;

  solution = '';

  oldSolution: BooleanNode | undefined;

  formulaParsed = false;

  showInstructions = false;

  constructor(protected router: Router) {
    super(router);
  }


  ngOnInit(): void {
    this.update();
  }

  update(): void {
    this.completelyCorrect = false;
    this.formulaParsed = false;
    this.corrected = false;

    this.solution = '';

    this.formula = generateBooleanFormula();

    this.assignments = calculateAssignments(this.formula.getVariables());
    this.assignments.forEach((assignment) => assignment.set(this.sampleVariable.variable, this.formula.evaluate(assignment)));
  }

  correct(): void {
    this.corrected = true;

    const booleanFormula: BooleanNode | undefined = parseBooleanFormula(this.solution);

    if (!booleanFormula) {
      alert('Konnte Formel >>' + this.solution + '<< nicht parsen!');
      return;
    }

    this.oldSolution = booleanFormula;

    console.info(booleanFormula);

    this.formulaParsed = true;

    // check contained variables!
    const variablesAllowed: string[] = this.formula.getVariables().map((v) => v.variable);
    const variablesUsed: string[] = booleanFormula.getVariables().map((v) => v.variable);

    const illegalVariables = variablesUsed.filter((v) => !variablesAllowed.includes(v));


    if (illegalVariables.length > 0) {
      alert('Sie haben die falschen Variablen ' + illegalVariables + ' benutzt!');
      return;
    }

    this.completelyCorrect = this.assignments
      .map((assignment) => {
        const value: boolean = booleanFormula.evaluate(assignment);
        assignment.set(this.learnerVariable.variable, value);
        return assignment.get(this.sampleVariable.variable) === value;
      })
      .every((a) => a);
  }

  @HostListener('document:keypress', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      if (this.completelyCorrect) {
        this.update();
      } else {
        this.correct();
      }
    }
  }

}
