import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {DexieService} from '../../_services/dexie.service';
import {ExerciseComponentHelpers} from '../../tools/collection-tools/_helpers/ExerciseComponentHelpers';
import {ExerciseCollection, ExerciseContent} from '../../_interfaces/exercise';

@Component({templateUrl: './collection-tool-admin.component.html'})
export class CollectionToolAdminComponent extends ExerciseComponentHelpers<ExerciseContent> implements OnInit {

  collections: ExerciseCollection[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService, private dexieService: DexieService) {
    super(route);

    if (!this.tool) {
      this.router.navigate(['/admin']);
    }
  }

  private fetchExerciseBasics(): void {
    this.collections.forEach((collection) => {
      this.apiService.getExercises<any>(this.tool.id, collection.id)
        .subscribe((exerciseBasics) => collection.exercises = exerciseBasics);
    });
  }

  private fetchCollections(): void {
    this.apiService.getCollections(this.tool.id)
      .subscribe((collections) => {
        this.collections = collections;
        this.fetchExerciseBasics();
      });
  }

  ngOnInit() {
    // this.dexieService.collections
    //   .filter((ec) => ec.toolId === this.tool.id).toArray()
    //   .then((collections: ExerciseCollection[]) => {
    //     if (collections && collections.length > 0) {
    //       this.collections = collections;
    //       this.fetchExerciseBasics();
    //     } else {
    this.fetchCollections();
    // }
    // });
  }

}
