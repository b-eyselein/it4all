import {Component, OnDestroy, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Saveable} from '../../_interfaces/saveable';
import {ReadObjectComponent} from '../_components/read-object/read-object.component';
import {Subscription} from "rxjs";
import {AdminReadLessonsGQL, AdminReadLessonsQuery, CompleteLessonFragment} from 'src/app/_services/apollo_services';


@Component({templateUrl: './admin-read-lessons.component.html'})
export class AdminReadLessonsComponent implements OnInit, OnDestroy {

  private toolId: string;
  private sub: Subscription;

  readLessonsQuery: AdminReadLessonsQuery;

  @ViewChildren(ReadObjectComponent) readLessonsComponents: QueryList<ReadObjectComponent<CompleteLessonFragment>>;


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
        .subscribe(({data}) => this.readLessonsQuery = data);
    })
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  get readLessons(): Saveable<CompleteLessonFragment>[] | undefined {
    return this.readLessonsQuery.tool.readLessons.map((rl) => {
      return {saved: false, title: '', value: rl, stringified: ''}
    });
  }

  save(lesson: Saveable<CompleteLessonFragment>): void {
//    this.apiService.adminUpsertLesson(lesson.value)
    //     .subscribe((saved) => lesson.saved = saved);
  }

  saveAll(): void {
    this.readLessonsComponents.forEach((readLesson) => readLesson.save.emit());
  }

}
