import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {
  LessonAsTextGQL,
  LessonAsTextQuery,
  LessonMultipleChoiceQuestionContentFragment,
  LessonTextContentFragment
} from '../../../../_services/apollo_services';
import {AuthenticationService} from '../../../../_services/authentication.service';
import {isSolvableLessonMultipleChoiceQuestionContentFragment, isSolvableLessonTextContentFragment} from '../solvable-lesson-content';
import {Subscription} from 'rxjs';

type ContentFragment = LessonTextContentFragment | LessonMultipleChoiceQuestionContentFragment;

@Component({templateUrl: './lesson-as-text.component.html'})
export class LessonAsTextComponent implements OnInit, OnDestroy {

  private sub: Subscription;

  lessonQuery: LessonAsTextQuery;
  currentIndex = 0;

  constructor(
    private authenticationService: AuthenticationService,
    private route: ActivatedRoute,
    private lessonGQL: LessonAsTextGQL,
  ) {
  }

  ngOnInit() {
    const userJwt = this.authenticationService.currentUserValue.jwt;

    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const lessonId = parseInt(paramMap.get('lessonId'), 10);

      this.lessonGQL
        .watch({userJwt, toolId, lessonId})
        .valueChanges
        .subscribe(({data}) => this.lessonQuery = data);
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  get contents(): ContentFragment[] | null {
    return this.lessonQuery?.me.tool.lesson.contents;
  }

  get contentIndexes(): number[] {
    return this.contents?.map((value: ContentFragment, index: number) => index);
  }

  get currentContent(): ContentFragment | null {
    return this.contents ? this.contents[this.currentIndex] : null;
  }

  get currentTextContentFragment(): LessonTextContentFragment | undefined {
    return this.currentContent && isSolvableLessonTextContentFragment(this.currentContent) ? this.currentContent : undefined;
  }

  get currentMultipleChoiceFragment(): LessonMultipleChoiceQuestionContentFragment | undefined {
    return this.currentContent && isSolvableLessonMultipleChoiceQuestionContentFragment(this.currentContent) ? this.currentContent : undefined;
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
