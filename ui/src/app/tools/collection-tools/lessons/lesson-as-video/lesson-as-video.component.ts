import {Component, OnDestroy, OnInit} from '@angular/core';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {Subscription} from 'rxjs';
import {LessonAsVideoGQL, LessonAsVideoQuery} from '../../../../_services/apollo_services';
import {AuthenticationService} from '../../../../_services/authentication.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'it4all-lesson-as-video',
  template: `
    <div class="container">

      <h1 class="title is-3 has-text-centered">{{lessonAsVideoQuery.me.tool.lesson.title}}</h1>

      <figure class="image is-16by9" *ngIf="lessonAsVideoQuery.me.tool.lesson.video">
        <iframe [src]="sanitizeUrl(lessonAsVideoQuery.me.tool.lesson.video)"
                frameborder="0" width="560" height="315" class="has-ratio"
                allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
                allowfullscreen></iframe>
      </figure>
    </div>
  `
})
export class LessonAsVideoComponent implements OnInit, OnDestroy {

  private sub: Subscription;

  lessonAsVideoQuery: LessonAsVideoQuery;

  constructor(
    private authenticationService: AuthenticationService,
    private route: ActivatedRoute,
    private lessonAsVideoGQL: LessonAsVideoGQL,
    private domSanitizer: DomSanitizer
  ) {
  }

  ngOnInit(): void {
    const userJwt = this.authenticationService.currentUserValue.jwt;

    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const lessonId = parseInt(paramMap.get('lessonId'), 10);

      this.lessonAsVideoGQL
        .watch({userJwt, toolId, lessonId})
        .valueChanges
        .subscribe(({data}) => this.lessonAsVideoQuery = data);
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  sanitizeUrl(videoUrl: string): SafeResourceUrl {
    return this.domSanitizer.bypassSecurityTrustResourceUrl(videoUrl);
  }

}
