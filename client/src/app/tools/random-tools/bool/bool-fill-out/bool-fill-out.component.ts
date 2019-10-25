import {Component, HostListener, OnInit} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {BoolFillOutPart} from '../../random-tools-list';
import {BoolComponentHelper} from '../_model/bool-component-helper';
import {BooleanNode, calculateAssignments} from '../_model/bool-node';
import {generateBooleanFormula} from '../_model/bool-formula';
import {Router} from '@angular/router';

@Component({templateUrl: './bool-fill-out.component.html'})
export class BoolFillOutComponent extends BoolComponentHelper implements OnInit {

  readonly part: ToolPart = BoolFillOutPart;

  withSubFormulas = false;
  subFormulas: BooleanNode[] = [];

  constructor(protected router: Router) {
    super(router);
  }

  ngOnInit(): void {
    this.update();
  }

  update(): void {
    this.completelyCorrect = false;
    this.corrected = false;

    this.formula = generateBooleanFormula();
    this.assignments = calculateAssignments(this.formula.getVariables());
    this.subFormulas = this.formula.getSubFormulas();

    this.assignments.forEach((assignment) => {
      assignment.set(this.sampleVariable.variable, this.formula.evaluate(assignment));
      assignment.set(this.learnerVariable.variable, false);
    });
  }

  correct(): void {
    this.corrected = true;

    this.completelyCorrect = this.assignments.every((a) => this.isCorrect(a));
  }

  updateAssignment(assignment: Map<string, boolean>): void {
    for (const as of this.assignments) {
      if (as === assignment) {
        const newValue = !as.get(this.learnerVariable.variable);
        as.set(this.learnerVariable.variable, newValue);
      }
    }

    if (this.corrected) {
      this.correct();
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
