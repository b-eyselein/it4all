import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {LessonsForToolGQL, LessonsForToolQuery} from "../../_services/apollo_services";
import {Subscription} from "rxjs";

@Component({templateUrl: './lessons-for-tool-overview.component.html'})
export class LessonsForToolOverviewComponent implements OnInit {

  sub: Subscription;
  toolId: string;

  lessonsForToolQuery: LessonsForToolQuery;

  constructor(private route: ActivatedRoute, private lessonsForToolGQL: LessonsForToolGQL) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      this.toolId = paramMap.get('toolId');

      this.lessonsForToolGQL
        .watch({toolId: this.toolId})
        .valueChanges
        .subscribe(({data}) => this.lessonsForToolQuery = data);
    });
  }

}
