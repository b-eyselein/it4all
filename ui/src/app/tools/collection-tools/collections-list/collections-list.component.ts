import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionListGQL, CollectionListQuery} from "../../../_services/apollo_services";

@Component({templateUrl: './collections-list.component.html'})
export class CollectionsListComponent implements OnInit {

  collectionListQuery: CollectionListQuery;

  constructor(protected route: ActivatedRoute, private collectionsGQL: CollectionListGQL) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.collectionsGQL
        .watch({toolId})
        .valueChanges
        .subscribe(({data}) => this.collectionListQuery = data);
    });
  }

}
