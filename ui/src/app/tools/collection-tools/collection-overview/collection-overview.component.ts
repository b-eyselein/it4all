import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionOverviewGQL, CollectionOverviewQuery, FieldsForLinkFragment} from '../../../_services/apollo_services';
import {Subscription} from 'rxjs';
import {AuthenticationService} from '../../../_services/authentication.service';

@Component({templateUrl: './collection-overview.component.html'})
export class CollectionOverviewComponent implements OnInit, OnDestroy {

  private sub: Subscription;

  collectionOverviewQuery: CollectionOverviewQuery;

  constructor(
    private authenticationService: AuthenticationService,
    private route: ActivatedRoute,
    private collectionOverviewGQL: CollectionOverviewGQL
  ) {
  }

  ngOnInit() {
    const userJwt = this.authenticationService.currentUserValue.jwt;

    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId: string = paramMap.get('toolId');
      const collId: number = parseInt(paramMap.get('collId'), 10);

      this.collectionOverviewGQL
        .watch({userJwt, toolId, collId})
        .valueChanges
        .subscribe(({data}) => this.collectionOverviewQuery = data);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  get exercises(): FieldsForLinkFragment[] {
    return this.collectionOverviewQuery.me.tool.collection.exercises;
  }

}
