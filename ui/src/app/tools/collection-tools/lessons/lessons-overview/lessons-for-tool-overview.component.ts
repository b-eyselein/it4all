import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {LessonIdentifierFragment, LessonsForToolGQL, LessonsForToolQuery} from '../../../../_services/apollo_services';
import {Subscription} from 'rxjs';
import {AuthenticationService} from '../../../../_services/authentication.service';

@Component({templateUrl: './lessons-for-tool-overview.component.html'})
export class LessonsForToolOverviewComponent implements OnInit, OnDestroy {

  private sub: Subscription;

  lessonsForToolQuery: LessonsForToolQuery;

  constructor(
    private authenticationService: AuthenticationService,
    private route: ActivatedRoute,
    private lessonsForToolGQL: LessonsForToolGQL
  ) {
  }

  ngOnInit() {
    const userJwt = this.authenticationService.currentUserValue.jwt;

    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.lessonsForToolGQL
        .watch({userJwt, toolId})
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
