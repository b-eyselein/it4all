import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {LessonContentFragment, LessonFragment, LessonGQL, LessonQuery} from '../../../../_services/apollo_services';
import {AuthenticationService} from '../../../../_services/authentication.service';

interface SolvableLessonContent {
  contentFragment: LessonContentFragment;
  priorSolved: boolean;
}

@Component({templateUrl: './lesson.component.html'})
export class LessonComponent implements OnInit {

  contents: SolvableLessonContent[];

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

  get lessonContents(): SolvableLessonContent[] {
    return this.lesson ? this.lesson.contents.map((contentFragment) => {
      return {contentFragment, priorSolved: false};
    }) : [];
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
