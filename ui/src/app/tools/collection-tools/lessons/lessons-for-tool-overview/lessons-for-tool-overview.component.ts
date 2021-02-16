import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {LessonIdentifierFragment, LessonsForToolGQL, LessonsForToolQuery} from '../../../../_services/apollo_services';
import {Subscription} from 'rxjs';

@Component({
  template: `
    <div class="container">
      <ng-container *ngIf="lessonsForToolQuery; else loadingLessonsBlock">

        <h1 class="title is-3 has-text-centered">Lektionen für {{lessonsForToolQuery.me.tool.name}}</h1>

        <div class="columns is-multiline" *ngIf="lessons.length > 0; else noLessonsFoundBlock">
          <div class="column is-half-desktop" *ngFor="let lesson of lessons">
            <div class="card">
              <header class="card-header">
                <p class="card-header-title">{{lesson.lessonId}}. {{lesson.title}}</p>
              </header>
              <div class="card-content">{{lesson.description}}</div>
              <div class="card-footer">
                <a class="card-footer-item" [routerLink]="[lesson.lessonId]">Zur Lektion</a>
              </div>
            </div>
          </div>
        </div>

      </ng-container>
    </div>

    <ng-template #loadingLessonsBlock>
      <div class="notification is-primary has-text-centered">Lade Lektionen...</div>
    </ng-template>

    <ng-template #noLessonsFoundBlock>
      <div class="notification is-danger has-text-centered">Es konnten keine Lektionen gefunden werden!</div>
    </ng-template>
  `
})
export class LessonsForToolOverviewComponent implements OnInit, OnDestroy {

  private sub: Subscription;

  lessonsForToolQuery: LessonsForToolQuery;

  constructor(private route: ActivatedRoute, private lessonsForToolGQL: LessonsForToolGQL) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.lessonsForToolGQL
        .watch({toolId})
        .valueChanges
        .subscribe(({data}) => this.lessonsForToolQuery = data);
    });
  }

  get lessons(): LessonIdentifierFragment[] {
    return this.lessonsForToolQuery ? this.lessonsForToolQuery.me.tool.lessons : [];
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
