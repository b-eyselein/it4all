import {Component, Input} from '@angular/core';
import {ApiService} from '../../../tools/collection-tools/_services/api.service';
import {IExerciseCollection} from '../../../_interfaces/models';

@Component({
  selector: 'it4all-read-collection',
  templateUrl: './read-collection.component.html'
})
export class ReadCollectionComponent {

  @Input() collection: IExerciseCollection;

  collectionSaved = false;

  constructor(private adminApiService: ApiService) {
  }

  saveCollection(): void {
    this.adminApiService.adminUpsertCollection(this.collection)
      .subscribe((wasUpserted) => this.collectionSaved = wasUpserted);
  }

}
