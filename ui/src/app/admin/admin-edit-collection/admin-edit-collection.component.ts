import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AdminEditCollectionGQL, AdminEditCollectionQuery} from '../../_services/apollo_services';
import {Subscription} from 'rxjs';

@Component({templateUrl: './admin-edit-collection.component.html'})
export class AdminEditCollectionComponent implements OnInit, OnDestroy {

  sub: Subscription;

  adminEditCollectionQuery: AdminEditCollectionQuery;

  constructor(private route: ActivatedRoute, private adminEditCollectionGQL: AdminEditCollectionGQL) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const collId = parseInt(paramMap.get('collId'), 10);

      this.adminEditCollectionGQL
        .watch({toolId, collId})
        .valueChanges
        .subscribe(({data}) => this.adminEditCollectionQuery = data);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  get collectionAsJson(): object | undefined {
    return JSON.parse(this.adminEditCollectionQuery.tool.collection.asJson);
  }

}
