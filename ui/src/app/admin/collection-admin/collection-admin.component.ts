import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionAdminGQL, CollectionAdminQuery, FieldsForLinkFragment} from '../../_services/apollo_services';
import {Subscription} from 'rxjs';

@Component({templateUrl: './collection-admin.component.html'})
export class CollectionAdminComponent implements OnInit, OnDestroy {

  private sub: Subscription;
  private apolloSub: Subscription;

  collectionAdminQuery: CollectionAdminQuery;

  constructor(private route: ActivatedRoute, private collectionAdminGQL: CollectionAdminGQL) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const collId: number = parseInt(paramMap.get('collId'), 10);

      this.apolloSub = this.collectionAdminGQL
        .watch({toolId, collId})
        .valueChanges
        .subscribe(({data}) => this.collectionAdminQuery = data);
    });
  }

  ngOnDestroy(): void {
    this.apolloSub.unsubscribe();
    this.sub.unsubscribe();
  }

  get queryExercises(): FieldsForLinkFragment[] {
    return this.collectionAdminQuery.tool.collection.exercises;
  }

}
