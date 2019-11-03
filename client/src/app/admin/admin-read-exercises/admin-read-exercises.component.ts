import {Component, OnInit} from '@angular/core';
import {Exercise, ExerciseCollection, Tool} from '../../_interfaces/tool';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../_services/api.service';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {DexieService} from '../../_services/dexie.service';

@Component({templateUrl: './admin-read-exercises.component.html'})
export class AdminReadExercisesComponent implements OnInit {

  tool: Tool;
  collection: ExerciseCollection;
  exercises: Exercise[];

  constructor(private route: ActivatedRoute, private router: Router, private dexieService: DexieService, private apiService: ApiService) {
    const toolId: string = this.route.snapshot.paramMap.get('toolId');

    this.tool = collectionTools.find((t) => t.id === toolId);

    if (!this.tool) {
      this.router.navigate(['/admin']);
    }
  }

  ngOnInit() {
    const collId = parseInt(this.route.snapshot.paramMap.get('collId'), 10);
    this.dexieService.collections.get([this.tool.id, collId])
      .then((maybeCollection) => {
        if (maybeCollection) {
          this.collection = maybeCollection;
          this.loadExercises();
        } else {
          this.getCollectionFromServer(collId);
        }
      });
  }

  private getCollectionFromServer(collId: number): void {
    this.apiService.getCollection(this.tool.id, collId)
      .subscribe((maybeServerCollection: ExerciseCollection | undefined) => {
        if (maybeServerCollection) {
          this.collection = maybeServerCollection;
          this.loadExercises();
        } else {
          this.router.navigate(['/admin', this.tool.id]);
        }
      });
  }

  private loadExercises(): void {
    this.apiService.adminReadExercises(this.tool.id, this.collection.id)
      .subscribe((exercises) => this.exercises = exercises);
  }

}