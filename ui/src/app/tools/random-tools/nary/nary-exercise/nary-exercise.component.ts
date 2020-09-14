import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Subscription} from "rxjs";

@Component({
  template: `
    <h1 class="title is-3 has-text-centered">{{title}}</h1>

    <ng-container [ngSwitch]="part">
      <it4all-nary-addition *ngSwitchCase="'addition'"></it4all-nary-addition>
      <it4all-nary-conversion *ngSwitchCase="'conversion'"></it4all-nary-conversion>
      <it4all-nary-two-conversion *ngSwitchCase="'twoConversion'"></it4all-nary-two-conversion>
      <div *ngSwitchDefault class="notification is-light-danger has-text-centered">
        Part &quot;{{part}}&quot; was not found!
      </div>
    </ng-container>
  `
})
export class NaryExerciseComponent implements OnInit {

  private sub: Subscription;

  part: string;
  title: string;

  constructor(private  route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      this.part = paramMap.get('part');

      this.title = this.getTitleForPart();
    })
  }

  private getTitleForPart(): string {
    switch (this.part) {
      case 'addition':
        return 'Addition';
      case 'conversion':
        return 'Zahlenumwandlung';
      case 'twoConversion':
        return 'Zahlenumwandlung im Zweiersystem';
      default:
        return '';
    }
  }

}
