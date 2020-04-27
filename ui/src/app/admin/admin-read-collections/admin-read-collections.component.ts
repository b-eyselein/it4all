import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';
import {AdminReadCollectionsGQL, AdminUpsertCollectionGQL} from '../../_services/apollo_services';
import {Saveable} from "../../_interfaces/saveable";
import {ExerciseCollection} from "../../_interfaces/graphql-types";

@Component({templateUrl: './admin-read-collections.component.html'})
export class AdminReadCollectionsComponent implements OnInit, OnDestroy {

  private toolId: string;

  private sub: Subscription;
  private apolloSub: Subscription;

  toolName: string;
  savableCollections: Saveable<ExerciseCollection>[];

  constructor(
    private route: ActivatedRoute,
    private adminReadCollectionsGQL: AdminReadCollectionsGQL,
    private adminUpsertCollectionGQL: AdminUpsertCollectionGQL
  ) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      this.toolId = paramMap.get('toolId');

      this.apolloSub = this.adminReadCollectionsGQL
        .watch({toolId: this.toolId})
        .valueChanges
        .subscribe(({data}) => {
            console.info(data);

            this.toolName = data.tool.name;

            this.savableCollections = data.tool.readCollections.map((ccf) => {
              const collection: ExerciseCollection = JSON.parse(ccf);
              return {
                saved: false,
                title: `${collection.collectionId}. ${collection.title}`,
                value: collection,
                stringified: ccf
              }
            });
          }
        );
    });
  }

  ngOnDestroy(): void {
    this.apolloSub.unsubscribe();
    this.sub.unsubscribe()
  }

  save(savableCollection: Saveable<ExerciseCollection>): void {
    this.adminUpsertCollectionGQL
      .mutate({
        toolId: this.toolId,
        content: savableCollection.stringified
      })
      .subscribe(({data}) => savableCollection.saved = !!data.upsertCollection)
  }

  saveAll(): void {
    this.savableCollections
      .filter((sc) => !sc.saved)
      .forEach((sc) => this.save(sc));
  }

}
