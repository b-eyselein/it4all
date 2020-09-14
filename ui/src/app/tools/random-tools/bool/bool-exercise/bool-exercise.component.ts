import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Subscription} from "rxjs";

@Component({
  template: `
    <div class="container">
      <h1 class="title is-3 has-text-centered">{{title}}</h1>

      <ng-container [ngSwitch]="part">
        <it4all-bool-fillout *ngSwitchCase="'fillOut'"></it4all-bool-fillout>
        <it4all-bool-create *ngSwitchCase="'create'"></it4all-bool-create>
        <div *ngSwitchDefault class="notification is-light-danger has-text-centered">
          Part &quot;{{part}}&quot; was not found!
        </div>
      </ng-container>
    </div>
  `
})
export class BoolExerciseComponent implements OnInit {

  private sub: Subscription;

  part: string;
  title: string;

  constructor(private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      this.part = paramMap.get('part');

      this.title = this.getTitleForPart();
    });
  }

  private getTitleForPart(): string {
    switch (this.part) {
      case 'fillOut':
        return 'Wahrheitstabellen ausfüllen';
      case 'create':
        return 'Boolesche Formel erstellen';
      case 'drawing':
        return '';
      default:
        return 'Fehler!';
    }
  }

}
