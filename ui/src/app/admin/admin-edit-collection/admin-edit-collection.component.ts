import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AdminEditCollectionGQL, AdminUpsertCollectionGQL} from '../../_services/apollo_services';
import {Subscription} from 'rxjs';
import {ExerciseCollection} from "../../_interfaces/graphql-types";

@Component({templateUrl: './admin-edit-collection.component.html'})
export class AdminEditCollectionComponent implements OnInit, OnDestroy {

  private sub: Subscription;
  private toolId: string;

  collectionToEdit: ExerciseCollection;

  constructor(
    private route: ActivatedRoute,
    private adminEditCollectionGQL: AdminEditCollectionGQL,
    private adminUpsertCollectionGQL: AdminUpsertCollectionGQL
  ) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      this.toolId = paramMap.get('toolId');
      const collId = parseInt(paramMap.get('collId'), 10);

      this.adminEditCollectionGQL
        .watch({toolId: this.toolId, collId})
        .valueChanges
        .subscribe(({data}) => this.collectionToEdit = JSON.parse(data.tool.collection.asJsonString));
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  save(): void {
    this.adminUpsertCollectionGQL
      .mutate({toolId: this.toolId, content: JSON.stringify(this.collectionToEdit)})
      .subscribe((data) => {
        console.info(data);
      });
  }

}
