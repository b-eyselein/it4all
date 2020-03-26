import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {Lesson} from '../../_interfaces/lesson';
import {AdminLessonIndexGQL, AdminLessonIndexQuery} from "../../_services/apollo_services";

@Component({templateUrl: './admin-lessons-index.component.html'})
export class AdminLessonsIndexComponent extends ComponentWithCollectionTool implements OnInit {

  lessons: Lesson[];

  adminLessonIndexQuery: AdminLessonIndexQuery;

  constructor(private route: ActivatedRoute, private adminLessonIndexGQL: AdminLessonIndexGQL, private apiService: ApiService) {
    super(route);
  }

  ngOnInit() {
    this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.adminLessonIndexGQL
        .watch({toolId})
        .valueChanges
        .subscribe(({data}) => this.adminLessonIndexQuery = data);
    });

    this.apiService.getLessons(this.tool.id)
      .subscribe((lessons) => this.lessons = lessons);
  }

}
