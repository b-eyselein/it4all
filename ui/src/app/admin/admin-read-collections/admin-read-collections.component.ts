import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {IExerciseCollection} from '../../_interfaces/models';
import {Saveable} from '../../_interfaces/saveable';
import {ReadObjectComponent} from '../_components/read-object/read-object.component';
import {Subscription} from 'rxjs';
import {AdminReadCollectionsGQL, AdminReadCollectionsQuery, AdminUpsertCollectionGQL} from '../../_services/apollo_services';

interface SaveableExerciseCollection extends IExerciseCollection, Saveable {
}

@Component({templateUrl: './admin-read-collections.component.html'})
export class AdminReadCollectionsComponent implements OnInit {


  sub: Subscription;

  adminReadCollectionsQuery: AdminReadCollectionsQuery;
  loadedCollections: SaveableExerciseCollection[];

  @ViewChildren(ReadObjectComponent) readCollectionComponents: QueryList<ReadObjectComponent<SaveableExerciseCollection>>;

  constructor(
    private route: ActivatedRoute,
    private apiService: ApiService,
    private adminReadCollectionsGQL: AdminReadCollectionsGQL,
    private adminUpsertCollectionGQL: AdminUpsertCollectionGQL
  ) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.adminReadCollectionsGQL
        .watch({toolId})
        .valueChanges
        .subscribe(({data}) => this.adminReadCollectionsQuery = data);

      this.apiService.adminReadCollections(toolId)
        .subscribe((loadedCollections) => this.loadedCollections = loadedCollections);
    });
  }

  save(collection: SaveableExerciseCollection): void {
    this.apiService.adminUpsertCollection(collection)
      .subscribe((wasUpserted) => collection.saved = wasUpserted);
  }

  saveAll(): void {
    this.readCollectionComponents.forEach((readCollectionComponent) => readCollectionComponent.save.emit());
  }

}
