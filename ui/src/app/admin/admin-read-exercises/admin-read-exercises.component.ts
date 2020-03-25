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

  collectionId: number;

  collection: IExerciseCollection;
  exercises: LoadedExercise[];

  @ViewChildren(ReadObjectComponent) readExercises: QueryList<ReadObjectComponent<LoadedExercise>>;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dexieService: DexieService,
    private apiService: ApiService
  ) {
    super(route);

    if (!this.tool) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/admin']);
    }

    this.route.paramMap.subscribe((paramMap) => {
      this.collectionId = parseInt(paramMap.get('collId'), 10);
    })
  }

  private loadExercises(): void {
    this.apiService.adminReadExercises(this.tool.id, this.collectionId)
      .subscribe((exercises) => this.exercises = exercises);
  }

  ngOnInit() {
    this.loadExercises();
  }

  save(exercise: LoadedExercise): void {
    this.apiService.adminUpsertExercise(exercise)
      .subscribe((saved) => exercise.saved = saved);
  }

  saveAll(): void {
    this.readExercises.forEach((readExerciseComponent) => readExerciseComponent.save.emit());
  }

}
