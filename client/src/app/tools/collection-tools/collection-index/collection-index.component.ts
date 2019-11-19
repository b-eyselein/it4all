import {Component, OnInit} from '@angular/core';
import {ApiService} from '../_services/api.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Exercise, ExerciseCollection, Tool} from '../../../_interfaces/tool';
import {collectionTools} from '../collection-tools-list';
import {DexieService} from '../../../_services/dexie.service';

@Component({templateUrl: './collection-index.component.html'})
export class CollectionIndexComponent implements OnInit {

  tool: Tool;
  collection: ExerciseCollection;

  exercises: Exercise[];

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService, private dexieService: DexieService) {
    const toolId: string = this.route.snapshot.paramMap.get('toolId');
    this.tool = collectionTools.find((t) => t.id === toolId);

    if (!this.tool) {
      this.router.navigate(['/']);
    }
  }

  private fetchCollection(collId: number): void {
    this.apiService.getCollection(this.tool.id, collId)
      .subscribe((collection: ExerciseCollection | undefined) => {
        if (collection) {
          this.collection = collection;
          this.updateExercises();
        } else {
          this.router.navigate(['/tools', this.tool.id]);
        }
      });
  }

  private updateExercises(): void {
    this.apiService.getExercises(this.tool.id, this.collection.id)
      .subscribe((exercises: Exercise[]) => this.exercises = exercises);
  }

  ngOnInit() {
    const collId: number = parseInt(this.route.snapshot.paramMap.get('collId'), 10);

    this.dexieService.collections.get([this.tool.id, collId])
      .then((maybeCollection: ExerciseCollection | undefined) => {
        if (maybeCollection) {
          this.collection = maybeCollection;
          this.updateExercises();
        } else {
          this.fetchCollection(collId);
        }
      });
  }

}
