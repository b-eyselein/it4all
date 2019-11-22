import {Component, OnInit} from '@angular/core';
import {ApiService} from '../_services/api.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ExerciseComponentHelpers} from '../_helpers/ExerciseComponentHelpers';
import {ExerciseCollection, ExerciseContent} from '../../../_interfaces/exercise';

@Component({templateUrl: './collection-tool-overview.component.html'})
export class CollectionToolOverviewComponent extends ExerciseComponentHelpers<ExerciseContent> implements OnInit {

  collections: ExerciseCollection[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiService: ApiService,
  ) {
    super(route);

    if (!this.tool) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit() {
    this.apiService.getCollections(this.tool.id)
      .subscribe((collections: ExerciseCollection[]) => {
        this.collections = collections;
        this.loadExercises();
      });
  }

  loadExercises(): void {
    this.collections.forEach((collection) =>
      this.apiService.getExercises<any>(this.tool.id, collection.id)
        .subscribe((exerciseBasics) => collection.exercises = exerciseBasics)
    );
  }

}
