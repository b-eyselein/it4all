import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {ReadCollectionComponent} from './read-collection/read-collection.component';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';
import {IExerciseCollection} from '../../_interfaces/models';

@Component({templateUrl: './admin-read-collections.component.html'})
export class AdminReadCollectionsComponent extends ComponentWithCollectionTool implements OnInit {

  loadedCollections: IExerciseCollection[];

  @ViewChildren(ReadCollectionComponent) readCollectionComponents: QueryList<ReadCollectionComponent>;

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

  saveAll(): void {
    this.readCollectionComponents.forEach((readCollectionComponent) => readCollectionComponent.saveCollection());
  }

}
