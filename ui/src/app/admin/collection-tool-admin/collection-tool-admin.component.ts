import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';
import {CollectionToolAdminGQL, CollectionToolAdminQuery} from "../../_services/apollo_services";

@Component({templateUrl: './collection-tool-admin.component.html'})
export class CollectionToolAdminComponent extends ComponentWithCollectionTool implements OnInit {

  collectionToolAdminQuery: CollectionToolAdminQuery;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private collectionToolAdminGQL: CollectionToolAdminGQL,
  ) {
    super(route);

    if (!this.tool) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/admin']);
    }
  }

  ngOnInit() {
   this.collectionToolAdminGQL
     .watch({toolId:this.tool.id})
     .valueChanges
     .subscribe(({data}) => this.collectionToolAdminQuery = data);
  }

}
