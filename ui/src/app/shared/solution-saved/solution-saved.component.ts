import {Component, Input} from '@angular/core';

@Component({
  selector: 'it4all-solution-saved',
  template: `
    <div class="notification is-light-grey" [ngClass]="solutionSaved ? 'has-text-dark-success' : 'has-text-danger'">
      &#10004; Ihre Lösung wurde <span [innerHTML]="solutionSaved ? '' : '<b>nicht</b>'"></span> gespeichert.
    </div>`,
})
export class SolutionSavedComponent {

  @Input() solutionSaved: boolean;

}
