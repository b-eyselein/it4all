import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionAdminGQL, CollectionAdminQuery, FieldsForLinkFragment} from '../../_services/apollo_services';
import {Subscription} from 'rxjs';

@Component({templateUrl: './collection-admin.component.html'})
export class CollectionAdminComponent implements OnInit, OnDestroy {

  sub: Subscription;

  collectionAdminQuery: CollectionAdminQuery;

  constructor(
    private route: ActivatedRoute,
    private collectionAdminGQL: CollectionAdminGQL,
  ) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const collId: number = parseInt(paramMap.get('collId'), 10);

      this.collectionAdminGQL
        .watch({toolId, collId})
        .valueChanges
        .subscribe(({data}) => {
          console.info(data.tool.collection.exercises.length);
          this.collectionAdminQuery = data;
        });
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  get queryExercises(): FieldsForLinkFragment[] {
    return this.collectionAdminQuery.tool.collection.exercises;
  }

}
