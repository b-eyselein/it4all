import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionToolOverviewGQL, CollectionToolOverviewQuery} from "../../../_services/apollo_services";
import {Subscription} from "rxjs";
import {AuthenticationService} from "../../../_services/authentication.service";

@Component({templateUrl: './collection-tool-overview.component.html'})
export class CollectionToolOverviewComponent implements OnInit, OnDestroy {

  private sub: Subscription;

  /**
   * @deprecated
   */
  toolId: string;
  collectionToolOverviewQuery: CollectionToolOverviewQuery;

  constructor(
    private route: ActivatedRoute,
    private authenticationService: AuthenticationService,
    private collectionToolOverviewGQL: CollectionToolOverviewGQL
  ) {
  }

  ngOnInit(): void {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      this.toolId = paramMap.get('toolId');

      this.collectionToolOverviewGQL
        .watch({toolId: this.toolId, userJwt: this.authenticationService.currentUserValue.jwt})
        .valueChanges
        .subscribe(({data}) => this.collectionToolOverviewQuery = data);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
