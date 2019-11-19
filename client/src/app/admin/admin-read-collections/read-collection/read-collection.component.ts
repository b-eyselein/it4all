import {Component, Input} from '@angular/core';
import {ExerciseCollection, Tool} from '../../../_interfaces/tool';
import {ApiService} from '../../../tools/collection-tools/_services/api.service';

@Component({selector: 'it4all-read-collection', templateUrl: './read-collection.component.html'})
export class ReadCollectionComponent {

  @Input() tool: Tool;
  @Input() collection: ExerciseCollection;

  collectionSaved = false;

  constructor(private adminApiService: ApiService) {
  }

  saveCollection(): void {
    this.adminApiService.adminUpsertCollection(this.collection)
      .subscribe((wasUpserted) => this.collectionSaved = wasUpserted);
  }

}
