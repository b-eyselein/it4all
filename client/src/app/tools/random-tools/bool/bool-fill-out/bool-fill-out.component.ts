import {Component, HostListener, OnInit} from '@angular/core';
import {Tool, ToolPart} from '../../../../_interfaces/tool';
import {BoolFillOutPart, BoolTool} from '../../random-tools-list';
import {BooleanFormula, BooleanVariable, generateBooleanFormula} from '../bool';
import {BoolComponentHelper} from '../bool-component-helper';

@Component({templateUrl: './bool-fill-out.component.html'})
export class BoolFillOutComponent extends BoolComponentHelper implements OnInit {

  readonly tool: Tool = BoolTool;
  readonly part: ToolPart = BoolFillOutPart;

  readonly sampleVariable: BooleanVariable = new BooleanVariable('s');
  readonly learnerVariable: BooleanVariable = new BooleanVariable('z');

  formula: BooleanFormula;

  corrected = false;
  completelyCorrect = false;

  constructor() {
    super();
  }

  ngOnInit(): void {
    this.update();
  }

  update(): void {
    this.completelyCorrect = false;
    this.corrected = false;
    this.formula = generateBooleanFormula();

    this.formula.getAllAssignments().forEach((assignment) => {
      assignment.set(this.sampleVariable.variable, this.formula.rootNode.evaluate(assignment));
      assignment.set(this.learnerVariable.variable, false);
    });
  }

  updateAssignment(assignment: Map<string, boolean>): void {
    for (const as of this.formula.getAllAssignments()) {
      if (as === assignment) {
        const newValue = !as.get(this.learnerVariable.variable);
        as.set(this.learnerVariable.variable, newValue);
      }
    }
  }

  @HostListener('document:keypress', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      if (this.completelyCorrect) {
        this.update();
      } else {
        this.corrected = true;
      }
    }
  }

}
