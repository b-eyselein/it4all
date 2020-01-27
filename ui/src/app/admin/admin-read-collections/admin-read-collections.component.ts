import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';
import {IExerciseCollection} from '../../_interfaces/models';
import {Saveable} from '../../_interfaces/saveable';
import {ReadObjectComponent} from '../_components/read-object/read-object.component';

interface SaveableExerciseCollection extends IExerciseCollection, Saveable {
}

@Component({templateUrl: './admin-read-collections.component.html'})
export class AdminReadCollectionsComponent extends ComponentWithCollectionTool implements OnInit {

  loadedCollections: SaveableExerciseCollection[];

  @ViewChildren(ReadObjectComponent) readCollectionComponents: QueryList<ReadObjectComponent<SaveableExerciseCollection>>;

  constructor(private route: ActivatedRoute, private apiService: ApiService, private router: Router) {
    super(route);

    if (!this.tool) {
      this.router.navigate(['/admin']);
    }
  }

  ngOnInit() {
    this.apiService.adminReadCollections(this.tool.id)
      .subscribe((loadedCollections) => this.loadedCollections = loadedCollections);
  }

  save(collection: SaveableExerciseCollection): void {
    this.apiService.adminUpsertCollection(collection)
      .subscribe((wasUpserted) => collection.saved = wasUpserted);
  }

  saveAll(): void {
    this.readCollectionComponents.forEach((readCollectionComponent) => readCollectionComponent.save.emit());
  }

}
