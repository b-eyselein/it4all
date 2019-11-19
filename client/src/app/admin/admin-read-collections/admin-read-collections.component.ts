import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {ExerciseCollection, Tool} from '../../_interfaces/tool';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {ReadCollectionComponent} from './read-collection/read-collection.component';

@Component({templateUrl: './admin-read-collections.component.html'})
export class AdminReadCollectionsComponent implements OnInit {

  tool: Tool;
  loadedCollections: ExerciseCollection[];

  @ViewChildren(ReadCollectionComponent) readCollectionComponents: QueryList<ReadCollectionComponent>;

  constructor(private route: ActivatedRoute, private apiService: ApiService, private router: Router) {
    const toolId = this.route.snapshot.paramMap.get('toolId');

    this.tool = collectionTools.find((t) => t.id === toolId);

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
