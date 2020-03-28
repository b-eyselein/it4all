import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionToolOverviewGQL, CollectionToolOverviewQuery} from "../../../_services/apollo_services";

@Component({templateUrl: './collection-tool-overview.component.html'})
export class CollectionToolOverviewComponent implements OnInit {

  collectionToolOverviewQuery: CollectionToolOverviewQuery;

  constructor(
    private route: ActivatedRoute,
    private collectionToolOverviewGQL: CollectionToolOverviewGQL
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.collectionToolOverviewGQL
        .watch({toolId})
        .valueChanges
        .subscribe(({data}) => this.collectionToolOverviewQuery = data);
    });
  }

}
