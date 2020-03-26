import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ComponentWithCollectionTool} from '../_helpers/ComponentWithCollectionTool';
import {CollectionToolOverviewGQL, CollectionToolOverviewQuery} from "../../../_services/apollo_services";

@Component({templateUrl: './collection-tool-overview.component.html'})
export class CollectionToolOverviewComponent extends ComponentWithCollectionTool implements OnInit {

  collectionToolOverviewQuery: CollectionToolOverviewQuery;

  constructor(private route: ActivatedRoute, private collectionToolOverviewGQL: CollectionToolOverviewGQL) {
    super(route);
  }

  ngOnInit(): void {
    console.info('TODO!');

    this.collectionToolOverviewGQL
      .watch({toolId: this.tool.id})
      .valueChanges
      .subscribe(({data}) => this.collectionToolOverviewQuery = data);
  }

}
