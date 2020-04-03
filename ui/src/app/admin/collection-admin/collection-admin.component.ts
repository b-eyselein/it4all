import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';
import {CollectionAdminGQL, CollectionAdminQuery} from "../../_services/apollo_services";
import {Exercise} from '../../_interfaces/graphql-types';

@Component({templateUrl: './collection-admin.component.html'})
export class CollectionAdminComponent extends ComponentWithCollectionTool implements OnInit {

  collectionAdminQuery: CollectionAdminQuery;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private collectionAdminGQL: CollectionAdminGQL,
  ) {
    super(route);
  }

  ngOnInit() {
    const collId: number = parseInt(this.route.snapshot.paramMap.get('collId'), 10);

    this.collectionAdminGQL
      .watch({toolId: this.tool.id, collId})
      .valueChanges
      .subscribe(({data}) => this.collectionAdminQuery = data);
  }

  get queryExercises(): Array<({ __typename?: 'Exercise' } & Pick<Exercise, 'id' | 'title'>)> {
    return (this.collectionAdminQuery) ? this.collectionAdminQuery.tool.collection.exercises : [];
  }
}
