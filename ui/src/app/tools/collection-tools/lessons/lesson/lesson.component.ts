import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {
  LessonFragment,
  LessonGQL,
  LessonMultipleChoiceQuestionFragment,
  LessonQuery,
  LessonTextContentFragment
} from '../../../../_services/apollo_services';
import {AuthenticationService} from '../../../../_services/authentication.service';
import {LessonTextContent} from '../../../../_interfaces/graphql-types';

type LessonContentFragmentTypes = LessonTextContentFragment | LessonMultipleChoiceQuestionFragment;

interface SolvableLessonContent<T extends LessonContentFragmentTypes> {
  contentFragment: T;
  priorSolved: boolean;
}

function isSolvableLessonTextContentFragment(lessonContentFragment: SolvableLessonContent<LessonContentFragmentTypes>): lessonContentFragment is SolvableLessonContent<LessonTextContentFragment> {
  return lessonContentFragment.contentFragment.__typename === 'LessonTextContent';
}

function isSolvableLessonMultipleChoiceQuestionFragment(lessonContentFragment: SolvableLessonContent<LessonContentFragmentTypes>): lessonContentFragment is SolvableLessonContent<LessonMultipleChoiceQuestionFragment> {
  return lessonContentFragment.contentFragment.__typename === 'LessonMultipleChoiceQuestion';
}

@Component({templateUrl: './lesson.component.html'})
export class LessonComponent implements OnInit {

  contents: SolvableLessonContent<any>[];

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
        .subscribe(({data}) => {
          this.lessonQuery = data;
          this.contents = [];
        });
    });
  }

  update(): void {
    console.log(this.contents.length + ' :: ' + this.currentIndex);

    if (this.contents.length > this.currentIndex) {
      this.contents[this.currentIndex].priorSolved = true;
      this.currentIndex++;
    }
  }

  get lesson(): LessonFragment {
    return this.lessonQuery ? this.lessonQuery.me.tool.lesson : null;
  }

  get lessonContents(): SolvableLessonContent<any>[] {
    return this.lesson ? this.lesson.contents.map((contentFragment) => {
      return {contentFragment, priorSolved: false};
    }) : [];
  }

  asLessonTextContentFragment(lessonContentFragment: SolvableLessonContent<LessonContentFragmentTypes>): SolvableLessonContent<LessonTextContentFragment> | undefined {
    return isSolvableLessonTextContentFragment(lessonContentFragment) ? lessonContentFragment : undefined;
  }

  asMultipleChoiceQuestionContentFragment(lessonContentFragment: SolvableLessonContent<LessonContentFragmentTypes>): SolvableLessonContent<LessonMultipleChoiceQuestionFragment> | undefined {
    return isSolvableLessonMultipleChoiceQuestionFragment(lessonContentFragment) ? lessonContentFragment : undefined;
  }

  /*
  asTextContent(content: LessonContentBase): LessonTextContent | undefined {
    if (isLessonTextContent(content)) {
      return content;
    } else {
      return undefined;
    }
  }
   */

}
