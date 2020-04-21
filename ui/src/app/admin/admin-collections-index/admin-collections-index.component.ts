import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AdminCollectionsIndexGQL, AdminCollectionsIndexQuery} from "../../_services/apollo_services";
import {Subscription} from "rxjs";

@Component({templateUrl: './admin-collections-index.component.html'})
export class AdminCollectionsIndexComponent implements OnInit, OnDestroy {

  sub: Subscription;
  toolId: string;
  adminCollectionsIndexQuery: AdminCollectionsIndexQuery;

  constructor(private route: ActivatedRoute, private adminCollectionsIndexGQL: AdminCollectionsIndexGQL) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      this.toolId = paramMap.get('toolId');

      this.adminCollectionsIndexGQL
        .watch({toolId: this.toolId})
        .valueChanges
        .subscribe(({data}) => this.adminCollectionsIndexQuery = data);
    })
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
