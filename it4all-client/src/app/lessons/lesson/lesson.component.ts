import {Component, OnInit} from '@angular/core';
import {Tool} from '../../_interfaces/tool';
import {ActivatedRoute, Router} from '@angular/router';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {randomTools} from '../../tools/random-tools/random-tools-list';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {isLessonTextContent, Lesson, LessonContentBase, LessonTextContent} from '../../_interfaces/lesson';

interface SolvableLessonContent extends LessonContentBase {
  priorSolved?: boolean;
}

@Component({templateUrl: './lesson.component.html'})
export class LessonComponent implements OnInit {

  tool: Tool;
  lesson: Lesson;
  contents: SolvableLessonContent[];
  currentIndex = 0;

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {
    const toolId = this.route.snapshot.paramMap.get('toolId');
    this.tool = [...collectionTools, ...randomTools].find((t) => t.id === toolId);

    if (!this.tool) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/']);
    }
  }

  ngOnInit() {
    const lessonId: number = parseInt(this.route.snapshot.paramMap.get('lessonId'), 10);

    this.apiService.getLesson(this.tool.id, lessonId)
      .subscribe((lesson) => {

        this.lesson = lesson;
        this.contents = lesson.content;

        if (!this.lesson) {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['/lessons', this.tool.id]);
        }

        this.update();
      });
  }

  update(): void {
    console.info(this.contents.length + ' :: ' + this.currentIndex);

    if (this.contents.length > this.currentIndex) {
      this.contents[this.currentIndex].priorSolved = true;
      this.currentIndex++;
    }
  }

  asTextContent(content: LessonContentBase): LessonTextContent | undefined {
    if (isLessonTextContent(content)) {
      return content;
    } else {
      return undefined;
    }
  }

}
