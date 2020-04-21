import {Component, Input, OnInit} from '@angular/core';
import {FieldsForLinkFragment} from "../../../../_services/apollo_services";

@Component({
  selector: 'it4all-exercise-link-card',
  templateUrl: './exercise-link-card.component.html'
})
export class ExerciseLinkCardComponent implements OnInit {

  @Input() exercise: FieldsForLinkFragment;

  ngOnInit(): void {
    console.info(this.exercise.topics.length);
  }

  get filledDifficultyStars(): number[] {
    return Array(this.exercise.difficulty).fill(0);
  }

  get nonFilledDifficultyStars(): number[] {
    return Array(5 - this.exercise.difficulty).fill(0)
  }

}
