import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {
  LessonGQL,
  LessonMultipleChoiceQuestionContentFragment,
  LessonQuery,
  LessonTextContentFragment
} from '../../../../_services/apollo_services';
import {AuthenticationService} from '../../../../_services/authentication.service';
import {isSolvableLessonMultipleChoiceQuestionContentFragment, isSolvableLessonTextContentFragment,} from '../solvable-lesson-content';


@Component({templateUrl: './lesson.component.html'})
export class LessonComponent implements OnInit {

  lessonQuery: LessonQuery;

  currentIndex = 0;

  constructor(
    private authenticationService: AuthenticationService,
    private route: ActivatedRoute,
    private lessonGQL: LessonGQL
  ) {
  }

  ngOnInit() {
    const userJwt = this.authenticationService.currentUserValue.jwt;

    this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const lessonId = parseInt(paramMap.get('lessonId'), 10);

      this.lessonGQL
        .watch({userJwt, toolId, lessonId})
        .valueChanges
        .subscribe(({data}) => this.lessonQuery = data);
    });
  }

  get currentTextContentFragment(): LessonTextContentFragment | undefined {
    const currentContent = this.lessonQuery.me.tool.lesson.contents[this.currentIndex];

    return isSolvableLessonTextContentFragment(currentContent) ? currentContent : undefined;
  }

  get currentMultipleChoiceFragment(): LessonMultipleChoiceQuestionContentFragment | undefined {
    const currentContent = this.lessonQuery.me.tool.lesson.contents[this.currentIndex];

    return isSolvableLessonMultipleChoiceQuestionContentFragment(currentContent) ? currentContent : undefined;
  }

  hasMoreContent(): boolean {
    return this.lessonQuery && this.currentIndex < (this.lessonQuery.me.tool.lesson.contents.length - 1);
  }

  previousContent(): void {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    }
  }

  nextContent(): void {
    if (this.currentIndex <= this.lessonQuery.me.tool.lesson.contents.length) {
      this.currentIndex++;
    }
  }

}
