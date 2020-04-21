import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AdminLessonIndexGQL, AdminLessonIndexQuery} from "../../_services/apollo_services";
import {Subscription} from "rxjs";

@Component({templateUrl: './admin-lessons-index.component.html'})
export class AdminLessonsIndexComponent implements OnInit, OnDestroy {

  sub: Subscription;
  adminLessonIndexQuery: AdminLessonIndexQuery;

  constructor(private route: ActivatedRoute, private adminLessonIndexGQL: AdminLessonIndexGQL) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.adminLessonIndexGQL
        .watch({toolId})
        .valueChanges
        .subscribe(({data}) => this.adminLessonIndexQuery = data);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
