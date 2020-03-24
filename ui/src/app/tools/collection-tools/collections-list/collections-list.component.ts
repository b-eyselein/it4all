import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ComponentWithCollectionTool} from '../_helpers/ComponentWithCollectionTool';
import {CollectionsGQL, CollectionsQuery} from "../../../_services/apollo_services";

@Component({templateUrl: './collections-list.component.html'})
export class CollectionsListComponent extends ComponentWithCollectionTool implements OnInit {

  collectionsQuery: CollectionsQuery;

  constructor(protected route: ActivatedRoute, private collectionsGQL: CollectionsGQL) {
    super(route);
  }

  ngOnInit() {
    console.info(this.tool.id);

    this.collectionsGQL
      .watch({toolId: this.tool.id})
      .valueChanges
      .subscribe(({data}) => this.collectionsQuery = data);
  }

}
