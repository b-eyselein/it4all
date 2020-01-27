import {Component, OnInit} from '@angular/core';
import {IExerciseCollectionWithExerciseMetaData} from '../../_interfaces/exercise';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';

@Component({templateUrl: './admin-collections-index.component.html'})
export class AdminCollectionsIndexComponent extends ComponentWithCollectionTool implements OnInit {

  collections: IExerciseCollectionWithExerciseMetaData[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {
    super(route);

    if (!this.tool) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/admin']);
    }
  }

  private fetchExerciseBasics(): void {
    this.collections.forEach((collection) => {
      this.apiService.getExercises(this.tool.id, collection.id)
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
    this.fetchCollections();
  }


}
