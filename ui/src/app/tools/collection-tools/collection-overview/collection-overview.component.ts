import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionOverviewGQL, CollectionOverviewQuery, FieldsForLinkFragment} from '../../../_services/apollo_services';
import {Subscription} from 'rxjs';
import {AuthenticationService} from '../../../_services/authentication.service';

const SLICE_COUNT: number = 12;

@Component({templateUrl: './collection-overview.component.html'})
export class CollectionOverviewComponent implements OnInit, OnDestroy {

  private sub: Subscription;

  collectionOverviewQuery: CollectionOverviewQuery;

  maxPage: number = 0;
  currentPage: number = 0;

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

          this.maxPage = this.collectionOverviewQuery.me.tool.collection.exercises.length / SLICE_COUNT;

          this.pages = Array(this.maxPage).fill(0).map((value, index) => index);

          console.info(this.pages);
        });
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  get exercises(): FieldsForLinkFragment[] {
    return this.collectionOverviewQuery.me.tool.collection.exercises;
  }

  getExercisesPaginated(): FieldsForLinkFragment[] {
    return this.exercises.slice(this.currentPage * SLICE_COUNT, (this.currentPage + 1) * SLICE_COUNT);
  }

}
