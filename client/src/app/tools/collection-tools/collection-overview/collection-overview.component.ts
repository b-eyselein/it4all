import {Component, OnInit} from '@angular/core';
import {ApiService} from '../_services/api.service';
import {ActivatedRoute, Router} from '@angular/router';
import {DexieService} from '../../../_services/dexie.service';
import {ExerciseComponentHelpers} from '../_helpers/ExerciseComponentHelpers';
import {IExercise, IExerciseCollection} from '../../../_interfaces/models';

@Component({templateUrl: './collection-overview.component.html'})
export class CollectionOverviewComponent extends ExerciseComponentHelpers implements OnInit {

  collection: IExerciseCollection;
  exercises: IExercise[];

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService, private dexieService: DexieService) {
    super(route);

    if (!this.tool) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/']);
    }
  }

  private updateExercises(): void {
    this.apiService.getExercises(this.tool.id, this.collection.id)
      .subscribe((exercises: IExercise[]) => this.exercises = exercises);
  }

  private fetchCollection(collId: number): void {
    this.apiService.getCollection(this.tool.id, collId)
      .subscribe((collection: IExerciseCollection | undefined) => {
        if (collection) {
          this.collection = collection;
          this.updateExercises();
        } else {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['../..']);
        }
      });
  }

  ngOnInit() {
    const collId: number = parseInt(this.route.snapshot.paramMap.get('collId'), 10);

    this.dexieService.collections.get([this.tool.id, collId])
      .then((maybeCollection: IExerciseCollection | undefined) => {
        if (maybeCollection) {
          this.collection = maybeCollection;
          this.updateExercises();
        } else {
          this.fetchCollection(collId);
        }
      });
  }

}
