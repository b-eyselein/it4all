import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionListGQL, CollectionListQuery} from "../../../_services/apollo_services";
import {Subscription} from "rxjs";

@Component({templateUrl: './collections-list.component.html'})
export class CollectionsListComponent implements OnInit, OnDestroy {

  private sub: Subscription;

  collectionListQuery: CollectionListQuery;

  constructor(protected route: ActivatedRoute, private collectionsGQL: CollectionListGQL) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.collectionsGQL
        .watch({toolId})
        .valueChanges
        .subscribe(({data}) => this.collectionListQuery = data);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
