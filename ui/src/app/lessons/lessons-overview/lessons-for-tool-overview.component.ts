import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Tool} from '../../_interfaces/tool';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {randomTools} from '../../tools/random-tools/random-tools-list';
import {LessonsForToolGQL, LessonsForToolQuery} from "../../_services/apollo_services";

@Component({templateUrl: './lessons-for-tool-overview.component.html'})
export class LessonsForToolOverviewComponent implements OnInit {

  tool: Tool;

  lessonsForToolQuery: LessonsForToolQuery;

  constructor(private route: ActivatedRoute, private lessonsForToolGQL: LessonsForToolGQL) {
    this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.tool = [...collectionTools, ...randomTools].find((t) => t.id === toolId);
    });
  }

  ngOnInit() {
    this.lessonsForToolGQL
      .watch({toolId: this.tool.id})
      .valueChanges
      .subscribe(({data}) => this.lessonsForToolQuery = data);
  }

}
