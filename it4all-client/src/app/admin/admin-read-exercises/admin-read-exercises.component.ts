import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {DexieService} from '../../_services/dexie.service';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';
import {IExercise, IExerciseCollection} from '../../_interfaces/models';
import {Saveable} from '../../_interfaces/saveable';
import {ReadObjectComponent} from '../_components/read-object/read-object.component';

interface LoadedExercise extends IExercise, Saveable {
}

@Component({templateUrl: './admin-read-exercises.component.html'})
export class AdminReadExercisesComponent extends ComponentWithCollectionTool implements OnInit {

  collection: IExerciseCollection;
  exercises: LoadedExercise[];

  @ViewChildren(ReadObjectComponent) readExercises: QueryList<ReadObjectComponent<LoadedExercise>>;

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

  save(exercise: LoadedExercise): void {
    this.apiService.adminUpsertExercise(exercise)
      .subscribe((saved) => exercise.saved = saved);
  }

  saveAll(): void {
    this.readExercises.forEach((readExerciseComponent) => readExerciseComponent.save.emit());
  }

}
