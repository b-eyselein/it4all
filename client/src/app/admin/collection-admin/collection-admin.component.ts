import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {Exercise, ExerciseCollection, Tool} from '../../_interfaces/tool';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {DexieService} from '../../_services/dexie.service';

@Component({templateUrl: './collection-admin.component.html'})
export class CollectionAdminComponent implements OnInit {

  tool: Tool;
  collection: ExerciseCollection;
  exercises: Exercise[];

  constructor(private route: ActivatedRoute, private router: Router, private dexieService: DexieService, private apiService: ApiService) {
    const toolId = this.route.snapshot.paramMap.get('toolId');

    this.tool = collectionTools.find((t) => t.id === toolId);
  }

  ngOnInit() {
    const collId: number = parseInt(this.route.snapshot.paramMap.get('collId'), 10);

    this.dexieService.collections.get([this.tool.id, collId])
      .then((maybeCollection: ExerciseCollection | undefined) => {
        if (maybeCollection) {
          this.collection = maybeCollection;
          this.loadExercises();
        } else {
          this.loadCollectionFromServer(collId);
        }
      });
  }

  private loadCollectionFromServer(collId: number): void {
    this.apiService.getCollection(this.tool.id, collId)
      .subscribe((maybeCollection: ExerciseCollection | undefined) => {
        if (maybeCollection) {
          this.collection = maybeCollection;
          this.loadExercises();
        } else {
          this.router.navigate(['/admin', this.tool.id]);
        }
      });
  }

  private loadExercises(): void {
    this.apiService.getExercises(this.tool.id, this.collection.id)
      .subscribe((exercises: Exercise[]) => this.exercises = exercises);
  }

}
