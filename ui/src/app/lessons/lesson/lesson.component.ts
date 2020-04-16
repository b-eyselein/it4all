import {Component, OnInit} from '@angular/core';
import {Tool} from '../../_interfaces/tool';
import {ActivatedRoute} from '@angular/router';
import {Lesson, LessonContentBase} from '../../_interfaces/lesson';
import {LessonGQL, LessonQuery} from "../../_services/apollo_services";

interface SolvableLessonContent extends LessonContentBase {
  priorSolved?: boolean;
}

@Component({templateUrl: './lesson.component.html'})
export class LessonComponent implements OnInit {

  tool: Tool;

  lesson: Lesson;
  contents: SolvableLessonContent[];

  lessonQuery: LessonQuery;

  currentIndex = 0;

  constructor(private route: ActivatedRoute, private lessonGQL: LessonGQL) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const lessonId = parseInt(paramMap.get('lessonId'), 10);

      this.lessonGQL
        .watch({toolId, lessonId})
        .valueChanges
        .subscribe(({data}) => {
          this.lessonQuery = data;
          this.contents = [];
        });
    })
  }

  update(): void {
    console.info(this.contents.length + ' :: ' + this.currentIndex);

    if (this.contents.length > this.currentIndex) {
      this.contents[this.currentIndex].priorSolved = true;
      this.currentIndex++;
    }
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
