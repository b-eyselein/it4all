import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionToolAdminGQL, CollectionToolAdminQuery} from "../../_services/apollo_services";
import {Subscription} from "rxjs";

@Component({templateUrl: './collection-tool-admin.component.html'})
export class CollectionToolAdminComponent implements OnInit, OnDestroy {

  private paramMapSub: Subscription;
  private gqSub: Subscription;

  collectionToolAdminQuery: CollectionToolAdminQuery;

  constructor(private route: ActivatedRoute, private collectionToolAdminGQL: CollectionToolAdminGQL) {
  }

  ngOnInit() {
    this.paramMapSub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.gqSub = this.collectionToolAdminGQL
        .watch({toolId})
        .valueChanges
        .subscribe(({data}) => this.collectionToolAdminQuery = data);
    })
  }

  ngOnDestroy(): void {
    this.gqSub.unsubscribe();
    this.paramMapSub.unsubscribe();
  }

}
