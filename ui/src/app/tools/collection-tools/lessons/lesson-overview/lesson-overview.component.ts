import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {LessonOverviewFragment, LessonOverviewGQL, LessonOverviewQuery} from '../../../../_services/apollo_services';
import {Subscription} from 'rxjs';

@Component({
  selector: 'it4all-lesson-overview',
  template: `
    <div class="container">

      <ng-container *ngIf="lessonOverviewQuery; else loadingDataBlock">

        <h1 class="title is-2 has-text-centered">{{lessonOverviewFragment.title}}</h1>

        <div class="content box">{{lessonOverviewFragment.description}}</div>

        <div class="columns">
          <div class="column">
            <a class="button is-fullwidth" *ngIf="lessonOverviewFragment.video" routerLink="video">Als Video</a>
          </div>
          <div class="column">
            <a class="button is-fullwidth" *ngIf="lessonOverviewFragment.contentCount > 0" routerLink="text">Als
              Text</a>
          </div>
        </div>

      </ng-container>

    </div>

    <ng-template #loadingDataBlock>
      <div class="notification is-primary has-text-centered">Lade Daten...</div>
    </ng-template>
  `,
})
export class LessonOverviewComponent implements OnInit, OnDestroy {

  private sub: Subscription;

  lessonOverviewQuery: LessonOverviewQuery;

  constructor(private route: ActivatedRoute, private lessonOverviewGQL: LessonOverviewGQL) {
  }

  ngOnInit(): void {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const lessonId = parseInt(paramMap.get('lessonId'), 10);

      this.lessonOverviewGQL
        .watch({toolId, lessonId})
        .valueChanges
        .subscribe(({data}) => this.lessonOverviewQuery = data);
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  get lessonOverviewFragment(): LessonOverviewFragment | null {
    return this.lessonOverviewQuery ? this.lessonOverviewQuery.me.tool.lesson : undefined;
  }

}
