import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';

@Component({
  selector: 'it4all-points-notification',
  template: `
    <div class="notification is-light-grey {{textColor}}">
      <p class="has-text-centered">
        <span i18n>Sie haben {{points}} von maximal {{maxPoints}} Punkten erreicht</span>.
      </p>

      <br>

      <progress class="progress" value="{{percentage}}" max="100">{{percentage}}%</progress>
    </div>`,
})
export class PointsNotificationComponent implements OnInit, OnChanges {

  @Input() points: number;
  @Input() maxPoints: number;

  percentage: number;

  textColor = 'has-text-danger';
  backgroundColor = 'is-danger';

  constructor() {
  }

  ngOnInit() {
    this.updatePercentage();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.updatePercentage();
  }

  updatePercentage(): void {
    if (isNaN(this.points) || isNaN(this.maxPoints)) {
      this.percentage = 0;
    } else {
      this.percentage = this.points / this.maxPoints * 100;

      if (this.percentage >= 90) {
        this.textColor = 'has-text-dark-success';
      } else if (this.percentage >= 75) {
        this.textColor = 'has-text-warning';
      } else {
        this.textColor = 'has-text-danger';
      }
    }
  }

}
