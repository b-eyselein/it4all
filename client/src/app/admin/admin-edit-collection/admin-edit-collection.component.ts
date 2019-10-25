import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../_services/api.service';
import {DexieService} from '../../_services/dexie.service';
import {ExerciseCollection, Tool} from '../../_interfaces/tool';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';

@Component({templateUrl: './admin-edit-collection.component.html'})
export class AdminEditCollectionComponent implements OnInit {

  tool: Tool;
  collection: ExerciseCollection;

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService, private dexieService: DexieService) {
    const toolId: string = this.route.snapshot.paramMap.get('toolId');


    this.tool = collectionTools.find((t) => t.id === toolId);

    if (!this.tool) {
      this.router.navigate(['/admin']);
    }
  }

  private fetchCollection(collId: number): void {
    this.apiService.getCollection(this.tool.id, collId)
      .subscribe((collection: ExerciseCollection | undefined) => {
        if (this.collection) {
          this.collection = collection;
        } else {
          this.router.navigate(['/admin', this.tool.id]);
        }
      });
  }

  ngOnInit() {
    const collId = parseInt(this.route.snapshot.paramMap.get('collId'), 10);

    this.dexieService.collections.get([this.tool.id, collId])
      .then((maybeCollection) => {
        if (maybeCollection) {
          this.collection = maybeCollection;
        } else {
          this.fetchCollection(collId);
        }
      });
  }

}
