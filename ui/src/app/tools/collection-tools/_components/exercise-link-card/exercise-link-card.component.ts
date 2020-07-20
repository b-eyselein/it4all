import {Component, Input} from '@angular/core';
import {FieldsForLinkFragment} from '../../../../_services/apollo_services';

@Component({
  selector: 'it4all-exercise-link-card',
  template: `
    <div class="card">
      <header class="card-header">
        <div class="card-header-title">
          {{exercise.exerciseId}}. {{exercise.title}}
          &nbsp;
          <div class="tag" title="Schwierigkeit">
            <it4all-filled-points [filledPoints]="exercise.difficulty" maxPoints="5"></it4all-filled-points>
          </div>
        </div>
      </header>

      <div class="card-content">
       <div class="columns is-multiline" *ngIf="exercise.topicsWithLevels.length > 0; else noTagsBlock">
          <div class="column is-one-third" *ngFor="let topicWithLevel of exercise.topicsWithLevels" [title]="topicWithLevel.topic.title">
            <div class="tag">
              {{topicWithLevel.topic.abbreviation}}
              &nbsp; - &nbsp;
              <it4all-filled-points
                [filledPoints]="topicWithLevel.level.levelIndex"
                [maxPoints]="topicWithLevel.topic.maxLevel.levelIndex">
              </it4all-filled-points>
            </div>
          </div>
        </div>
      </div>

      <footer class="card-footer">
        <a class="card-footer-item"
           [routerLink]="['/tools', exercise.toolId, 'collections', exercise.collectionId, 'exercises', exercise.exerciseId]">
          Zur Aufgabe
        </a>
      </footer>

    </div>

    <ng-template #noTagsBlock>
      <div class="tag is-warning">Keine Tags vorhanden</div>
    </ng-template>
  `
})
export class ExerciseLinkCardComponent {

  @Input() exercise: FieldsForLinkFragment;

}
