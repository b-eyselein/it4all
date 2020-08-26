import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {
  CollectionOverviewGQL,
  CollectionOverviewQuery,
  CollOverviewToolFragment,
  FieldsForLinkFragment
} from '../../../_services/apollo_services';
import {Subscription} from 'rxjs';
import {AuthenticationService} from '../../../_services/authentication.service';
import {BreadCrumbPart} from "../../../shared/breadcrumbs/breadcrumbs.component";

const SLICE_COUNT: number = 12;

// url => /tools/:toolId/collections/:collId

@Component({templateUrl: './collection-overview.component.html'})
export class CollectionOverviewComponent implements OnInit, OnDestroy {

  private sub: Subscription;

  collectionOverviewQuery: CollectionOverviewQuery;

  paginationNeeded = false;

  maxPage = 0;
  currentPage = 0;

  pages: number[] = [];

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
        .subscribe(({data}) => {
          this.collectionOverviewQuery = data;

          this.paginationNeeded = this.exercises.length > SLICE_COUNT;

          this.maxPage = Math.ceil(this.collectionOverviewQuery.me.tool.collection.exercises.length / SLICE_COUNT);

          this.pages = Array(this.maxPage).fill(0).map((value, index) => index);
        });
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  get tool(): CollOverviewToolFragment {
    return this.collectionOverviewQuery.me.tool;
  }

  get exercises(): FieldsForLinkFragment[] {
    return this.tool.collection.exercises;
  }

  getExercisesPaginated(): FieldsForLinkFragment[] {
    return this.exercises.slice(this.currentPage * SLICE_COUNT, (this.currentPage + 1) * SLICE_COUNT);
  }

  get breadCrumbs(): BreadCrumbPart[] {
    return [
      {routerLinkPart: '/', title: 'Tools'},
      {routerLinkPart: `tools/${this.tool.id}`, title: this.tool.name},
      {routerLinkPart: 'collections', title: 'Sammlungen'},
      {routerLinkPart: this.tool.collection.collectionId.toString(), title: this.tool.collection.title}
    ]
  }

}
