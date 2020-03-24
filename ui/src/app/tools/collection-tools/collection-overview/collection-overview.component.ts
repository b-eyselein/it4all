import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ComponentWithCollectionTool} from '../_helpers/ComponentWithCollectionTool';
import {CollectionOverviewGQL, CollectionOverviewQuery} from "../../../_services/apollo_services";

@Component({templateUrl: './collection-overview.component.html'})
export class CollectionOverviewComponent extends ComponentWithCollectionTool implements OnInit {

  collectionOverviewQuery: CollectionOverviewQuery;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private collectionOverviewGQL: CollectionOverviewGQL,
  ) {
    super(route);

    if (!this.tool) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/']);
    }
  }


  ngOnInit() {
    const collId: number = parseInt(this.route.snapshot.paramMap.get('collId'), 10);

    this.collectionOverviewGQL
      .watch({toolId: this.tool.id, collId})
      .valueChanges
      .subscribe(({data}) => this.collectionOverviewQuery = data);
  }

}
