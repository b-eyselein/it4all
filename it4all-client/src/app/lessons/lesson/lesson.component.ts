import {Component, OnInit} from '@angular/core';
import {Lesson} from '../../_interfaces/lesson';
import {Tool} from '../../_interfaces/tool';
import {ActivatedRoute, Router} from '@angular/router';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {randomTools} from '../../tools/random-tools/random-tools-list';
import {LessonApiService} from '../_services/lesson-api.service';

@Component({templateUrl: './lesson.component.html'})
export class LessonComponent implements OnInit {

  tool: Tool;
  lesson: Lesson;
  currentIndex = 0;

  constructor(private route: ActivatedRoute, private router: Router, private lessonApiService: LessonApiService) {
    const toolId = this.route.snapshot.paramMap.get('toolId');
    this.tool = [...collectionTools, ...randomTools].find((t) => t.id === toolId);

    if (!this.tool) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/']);
    }
  }

  ngOnInit() {
    const lessonId: number = parseInt(this.route.snapshot.paramMap.get('lessonId'), 10);

    this.lessonApiService.getLesson(this.tool.id, lessonId)
      .subscribe((lesson) => {
        this.lesson = lesson;


        if (!this.lesson) {
          this.router.navigate(['/lessons', this.tool.id]);
        }

        this.update();
      });
  }

  update(): void {
    console.info(this.lesson.content.length);
    if (this.lesson.content.length > this.currentIndex) {
      this.lesson.content[this.currentIndex].priorSolved = true;
      this.currentIndex++;
    }
  }

}
