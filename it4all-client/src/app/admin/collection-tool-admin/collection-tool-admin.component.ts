import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';
import {IExerciseCollectionWithExerciseMetaData} from '../../_interfaces/exercise';

@Component({templateUrl: './collection-tool-admin.component.html'})
export class CollectionToolAdminComponent extends ComponentWithCollectionTool implements OnInit {

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
