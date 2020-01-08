import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {Lesson} from '../../_interfaces/lesson';

@Component({templateUrl: './admin-lessons-index.component.html'})
export class AdminLessonsIndexComponent extends ComponentWithCollectionTool implements OnInit {

  lessons: Lesson[];

  constructor(private route: ActivatedRoute, private apiService: ApiService) {
    super(route);
  }

  ngOnInit() {
    this.apiService.getLessons(this.tool.id)
      .subscribe((lessons) => this.lessons = lessons);
  }

}
