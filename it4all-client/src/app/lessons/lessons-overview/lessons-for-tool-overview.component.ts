import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Tool} from '../../_interfaces/tool';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {randomTools} from '../../tools/random-tools/random-tools-list';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {Lesson} from '../../_interfaces/lesson';

@Component({templateUrl: './lessons-for-tool-overview.component.html'})
export class LessonsForToolOverviewComponent implements OnInit {

  tool: Tool;
  lessons: Lesson[];

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {
    const toolId = this.route.snapshot.paramMap.get('toolId');

    this.tool = [...collectionTools, ...randomTools].find((t) => t.id === toolId);

    if (!this.tool) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit() {
    this.apiService.getLessons(this.tool.id)
      .subscribe((lessons) => this.lessons = lessons);
  }

}
