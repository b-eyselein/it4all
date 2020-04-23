import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Saveable} from '../../_interfaces/saveable';
import {Subscription} from "rxjs";
import {AdminReadLessonsGQL, LessonFragment} from 'src/app/_services/apollo_services';


@Component({templateUrl: './admin-read-lessons.component.html'})
export class AdminReadLessonsComponent implements OnInit, OnDestroy {

  private toolId: string;
  private sub: Subscription;

  toolName: string;
  readLessons: Saveable<LessonFragment>[];


  constructor(
    private route: ActivatedRoute,
    private adminReadLessonsGQL: AdminReadLessonsGQL,
  ) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      this.toolId = paramMap.get('toolId');

      this.adminReadLessonsGQL
        .watch({toolId: this.toolId})
        .valueChanges
        .subscribe(({data}) => {
            this.toolName = data.tool.name;
            this.readLessons = data.tool.readLessons.map((rl) => {
              return {saved: false, title: '', value: rl, stringified: ''}
            })
          }
        );
    })
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  save(_lesson: Saveable<LessonFragment>): void {
//    this.apiService.adminUpsertLesson(lesson.value)
    //     .subscribe((saved) => lesson.saved = saved);
  }

  saveAll(): void {
    this.readLessons
      .filter((rl) => !rl.saved)
      .forEach((readLesson) => this.save(readLesson));
  }

}
