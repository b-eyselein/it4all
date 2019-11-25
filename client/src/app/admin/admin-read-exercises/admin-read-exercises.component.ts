import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {DexieService} from '../../_services/dexie.service';
import {ReadExerciseComponent} from './read-exercise/read-exercise.component';
import {ExerciseComponentHelpers} from '../../tools/collection-tools/_helpers/ExerciseComponentHelpers';
import {IExercise, IExerciseCollection} from '../../_interfaces/models';

@Component({templateUrl: './admin-read-exercises.component.html'})
export class AdminReadExercisesComponent extends ExerciseComponentHelpers implements OnInit {

  collection: IExerciseCollection;
  exercises: IExercise[];

  @ViewChildren(ReadExerciseComponent) readExercises: QueryList<ReadExerciseComponent>;

  constructor(private route: ActivatedRoute, private router: Router, private dexieService: DexieService, private apiService: ApiService) {
    super(route);

    if (!this.tool) {
      this.router.navigate(['/admin']);
    }
  }

  private getCollectionFromServer(collId: number): void {
    this.apiService.getCollection(this.tool.id, collId)
      .subscribe((maybeServerCollection: IExerciseCollection | undefined) => {
        if (maybeServerCollection) {
          this.collection = maybeServerCollection;
          this.loadExercises();
        } else {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['/admin', this.tool.id]);
        }
      });
  }

  private loadExercises(): void {
    this.apiService.adminReadExercises(this.tool.id, this.collection.id)
      .subscribe((exercises) => this.exercises = exercises);
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

  saveAll(): void {
    this.readExercises.forEach((readExerciseComponent) => readExerciseComponent.saveExercise());
  }

}
