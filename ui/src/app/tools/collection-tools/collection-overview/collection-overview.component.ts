import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionOverviewGQL, CollectionOverviewQuery} from "../../../_services/apollo_services";
import {Subscription} from "rxjs";

@Component({templateUrl: './collection-overview.component.html'})
export class CollectionOverviewComponent implements OnInit, OnDestroy {

  sub: Subscription;
  collectionOverviewQuery: CollectionOverviewQuery;

  constructor(private route: ActivatedRoute, private collectionOverviewGQL: CollectionOverviewGQL) {
  }


  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId: string = paramMap.get('toolId');
      const collId: number = parseInt(paramMap.get('collId'), 10);

      this.collectionOverviewGQL
        .watch({toolId, collId})
        .valueChanges
        .subscribe(({data}) => this.collectionOverviewQuery = data);
    })
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
