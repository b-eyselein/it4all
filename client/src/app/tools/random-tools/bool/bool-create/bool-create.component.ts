import {Component, HostListener, OnInit} from '@angular/core';
import {Tool, ToolPart} from '../../../../_interfaces/tool';
import {BoolCreatePart, BoolTool} from '../../random-tools-list';
import {BooleanFormula, BooleanNode, BooleanVariable, generateBooleanFormula} from '../bool';
import {parseBooleanFormula} from '../boolean-formula-parser';
import {stringArraysEqual} from '../../../../helpers';
import {BoolComponentHelper} from '../bool-component-helper';

@Component({templateUrl: './bool-create.component.html'})
export class BoolCreateComponent extends BoolComponentHelper implements OnInit {

  readonly tool: Tool = BoolTool;
  readonly part: ToolPart = BoolCreatePart;

  readonly sampleVariable: BooleanVariable = new BooleanVariable('z');
  readonly learnerVariable: BooleanVariable = new BooleanVariable('y');

  formula: BooleanFormula;

  solution = '';

  corrected = false;
  completelyCorrect = false;

  showInstructions = false;

  constructor() {
    super();
  }

  ngOnInit(): void {
    this.update();
  }

  update(): void {
    this.completelyCorrect = false;
    this.corrected = false;

    this.solution = '';

    this.formula = generateBooleanFormula();
    this.formula.getAllAssignments().forEach((assignment) =>
      assignment.set(this.sampleVariable.variable, this.formula.rootNode.evaluate(assignment)));
  }

  correct(): void {
    this.corrected = true;

    const booleanFormula: BooleanNode | undefined = parseBooleanFormula(this.solution);

    if (!booleanFormula) {
      alert('Konnte Formel >>' + this.solution + '<< nicht parsen!');
      return;
    }

    // TODO: check contained variables!
    const variablesAllowed: string[] = this.formula.getVariables().map((v) => v.variable);
    const variablesUsed: string[] = booleanFormula.getVariables().map((v) => v.variable);

    const illegalVariables = variablesUsed.filter((v) => !variablesAllowed.includes(v));


    if (illegalVariables.length > 0) {
      alert('Sie haben die falschen Variablen ' + illegalVariables + ' benutzt!');
      return;
    }

    this.completelyCorrect = this.formula.getAllAssignments()
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
