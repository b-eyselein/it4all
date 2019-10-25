import {BooleanNode, BooleanVariable} from './bool-node';
import {Router} from '@angular/router';
import {Tool} from '../../../../_interfaces/tool';
import {BoolTool} from '../../random-tools-list';

export abstract class BoolComponentHelper {

  protected readonly tool: Tool = BoolTool;

  constructor(protected router: Router) {
  }

  readonly sampleVariable: BooleanVariable = new BooleanVariable('z');
  readonly learnerVariable: BooleanVariable = new BooleanVariable('y');

  formula: BooleanNode;
  assignments: Map<string, boolean>[];

  corrected = false;
  completelyCorrect = false;

  displayAssignmentValue(assignment: Map<string, boolean>, variable: BooleanVariable): string {
    if (assignment.has(variable.variable)) {
      return assignment.get(variable.variable) ? '1' : '0';
    } else {
      return '';
    }
  }

  isCorrect(assignment: Map<string, boolean>): boolean {
    return assignment.get(this.learnerVariable.variable) === assignment.get(this.sampleVariable.variable);
  }

  end(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['/randomTools', this.tool.id]);
  }

}
