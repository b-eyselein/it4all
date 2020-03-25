import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';
import {AdminCollectionsIndexGQL, AdminCollectionsIndexQuery} from "../../_services/apollo_services";

@Component({templateUrl: './admin-collections-index.component.html'})
export class AdminCollectionsIndexComponent extends ComponentWithCollectionTool implements OnInit {

  adminCollectionsIndexQuery: AdminCollectionsIndexQuery;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private adminCollectionsIndexGQL: AdminCollectionsIndexGQL,
  ) {
    super(route);

    if (!this.tool) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/admin']);
    }
  }

  ngOnInit() {
    this.adminCollectionsIndexGQL
      .watch({toolId: this.tool.id})
      .valueChanges
      .subscribe(({data}) => this.adminCollectionsIndexQuery = data);
  }

}
