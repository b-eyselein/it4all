import {Component, Input} from '@angular/core';
import {CollectionTool} from '../../../_interfaces/tool';
import {ApiService} from '../../../tools/collection-tools/_services/api.service';
import {ExerciseCollection} from '../../../_interfaces/exercise';

@Component({selector: 'it4all-read-collection', templateUrl: './read-collection.component.html'})
export class ReadCollectionComponent {

  @Input() tool: CollectionTool<any>;
  @Input() collection: ExerciseCollection;

  collectionSaved = false;

  constructor(private adminApiService: ApiService) {
  }

  saveCollection(): void {
    this.adminApiService.adminUpsertCollection(this.collection)
      .subscribe((wasUpserted) => this.collectionSaved = wasUpserted);
  }

}
