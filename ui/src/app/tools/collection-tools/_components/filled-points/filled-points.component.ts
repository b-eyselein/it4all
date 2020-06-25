import {Component, Input} from '@angular/core';

@Component({
  selector: 'it4all-filled-points',
  template: `
    <span *ngFor="let x of filledDifficultyStars">&#9899;</span>
    <span *ngFor="let x of nonFilledDifficultyStars">&#9898;</span>
  `
})
export class FilledPointsComponent {

  @Input() filledPoints: number;
  @Input() maxPoints: number;

  get filledDifficultyStars(): number[] {
    return Array(this.filledPoints).fill(0);
  }

  get nonFilledDifficultyStars(): number[] {
    return Array(this.maxPoints - this.filledPoints).fill(0);
  }

}
