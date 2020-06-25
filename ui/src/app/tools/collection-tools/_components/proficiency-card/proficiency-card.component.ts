import {Component, Input} from '@angular/core';
import {UserProficiencyFragment} from '../../../../_services/apollo_services';

@Component({
  selector: 'it4all-proficiency-card',
  template: `
    <div class="card">
      <header class="card-header">
        <p class="card-header-title">{{proficiency.topic.title}}</p>
      </header>
      <div class="card-content">
        <p>Level: {{proficiency.level.title}}</p>

        <p>{{proficiency.points}} von {{proficiency.pointsForNextLevel}} Punkten für nächstes Level</p>

        <p>
          <it4all-filled-points
            [filledPoints]="proficiency.level.levelIndex"
            [maxPoints]="proficiency.topic.maxLevel.levelIndex">
          </it4all-filled-points>
        </p>


        <!--
        <hr>

        <div class="table-container">

          <table class="table is-fullwidth">
            <thead>
              <tr>
                <th>Level</th><th>Punkte</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Beginner</td><td>{{proficiency.beginnerPoints}}</td>
              </tr>
              <tr>
                <td>Intermediate</td><td>{{proficiency.intermediatePoints}}</td>
              </tr>
              <tr>
                <td>Advanced</td><td>{{proficiency.advancedPoints}}</td>
              </tr>
              <tr>
                <td>Expert</td><td>{{proficiency.expertPoints}}</td>
              </tr>
            </tbody>
          </table>

        </div>
       -->

      </div>
    </div>`
})
export class ProficiencyCardComponent {

  @Input() proficiency: UserProficiencyFragment;

}
