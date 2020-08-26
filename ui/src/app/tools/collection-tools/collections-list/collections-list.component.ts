import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionListGQL, CollectionListQuery, CollectionValuesFragment} from '../../../_services/apollo_services';
import {Subscription} from 'rxjs';
import {AuthenticationService} from '../../../_services/authentication.service';
import {BreadCrumbPart} from "../../../shared/breadcrumbs/breadcrumbs.component";

// url => /tools/:toolId/collections

@Component({templateUrl: './collections-list.component.html'})
export class CollectionsListComponent implements OnInit, OnDestroy {

  private sub: Subscription;

  collectionListQuery: CollectionListQuery;

  constructor(
    private authenticationService: AuthenticationService,
    private route: ActivatedRoute,
    private collectionsGQL: CollectionListGQL
  ) {
  }

  ngOnInit() {
    const userJwt = this.authenticationService.currentUserValue.jwt;

    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.collectionsGQL
        .watch({userJwt, toolId})
        .valueChanges
        .subscribe(({data}) => this.collectionListQuery = data);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  get tool() {
    return this.collectionListQuery.me.tool;
  }

  get collections(): CollectionValuesFragment[] {
    return this.tool.collections;
  }

  get breadCrumbs(): BreadCrumbPart[] {
    return [
      {routerLinkPart: '/', title: 'Tools'},
      {routerLinkPart: `tools/${this.tool.id}`, title: this.tool.name},
      {routerLinkPart: 'collections', title: 'Sammlungen'},
    ]
  }

}
