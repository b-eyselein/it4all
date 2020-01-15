import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';

@Component({
  selector: 'it4all-points-notification',
  template: `
    <div class="notification has-text-{{textColor}}">
      <p class="has-text-centered">Sie haben {{points}} von maximal {{maxPoints}} Punkten erreicht.</p>

      <br>

      <progress class="progress is-{{textColor}}" value="{{percentage}}" max="100">{{percentage}}%</progress>
    </div>`,
})
export class PointsNotificationComponent implements OnInit, OnChanges {

  @Input() points: number;
  @Input() maxPoints: number;

  percentage: number;
  textColor = 'danger';

  constructor() {
  }

  ngOnInit() {
    this.updatePercentage();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.updatePercentage();
  }

  updatePercentage(): void {
    this.percentage = this.points / this.maxPoints * 100;

    if (this.percentage >= 90) {
      this.textColor = 'success';
    } else if (this.percentage >= 75) {
      this.textColor = 'warning';
    }

  }

}
