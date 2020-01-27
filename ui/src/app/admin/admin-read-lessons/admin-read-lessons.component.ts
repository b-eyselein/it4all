import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';
import {Saveable} from '../../_interfaces/saveable';
import {ReadObjectComponent} from '../_components/read-object/read-object.component';
import {Lesson} from '../../_interfaces/lesson';

interface LoadedLesson extends Lesson, Saveable {
}

@Component({templateUrl: './admin-read-lessons.component.html'})
export class AdminReadLessonsComponent extends ComponentWithCollectionTool implements OnInit {

  loadedLessons: LoadedLesson[];

  @ViewChildren(ReadObjectComponent) readLessons: QueryList<ReadObjectComponent<LoadedLesson>>;

  constructor(private route: ActivatedRoute, private apiService: ApiService) {
    super(route);
  }

  ngOnInit() {
    this.apiService.adminReadLessons(this.tool.id)
      .subscribe((loadedLessons) => this.loadedLessons = loadedLessons);
  }

  save(lesson: LoadedLesson): void {
    this.apiService.adminUpsertLesson(lesson)
      .subscribe((saved) => lesson.saved = saved);
  }

  saveAll(): void {
    this.readLessons.forEach((readLesson) => readLesson.save.emit());
  }

}
