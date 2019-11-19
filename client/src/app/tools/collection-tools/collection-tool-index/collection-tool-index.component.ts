import {Component, OnInit} from '@angular/core';
import {ExerciseCollection, Tool} from '../../../_interfaces/tool';
import {ApiService} from '../_services/api.service';
import {ActivatedRoute, Router} from '@angular/router';
import {collectionTools} from '../collection-tools-list';

@Component({templateUrl: './collection-tool-index.component.html'})
export class CollectionToolIndexComponent implements OnInit {

  // FIXME: show number of exercise for every collection!

  tool: Tool;
  collections: ExerciseCollection[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiService: ApiService,
    // private dexieService: DexieService
  ) {
    const toolId: string = this.route.snapshot.paramMap.get('toolId');
    this.tool = collectionTools.find((t) => t.id === toolId);

    if (!this.tool) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit() {
    // this.dexieService.collections.toArray()
    //   .then((collections: ExerciseCollection[]) => this.collections = collections);
    this.apiService.getCollections(this.tool.id)
      .subscribe((collections: ExerciseCollection[]) => {
        this.collections = collections;
        this.loadExerciseBasics();
      });
  }

  loadExerciseBasics(): void {
    this.collections.forEach((collection) => {
      this.apiService.getExerciseBasics(this.tool.id, collection.id)
        .subscribe((exerciseBasics) => collection.exercisesBasics = exerciseBasics);
    });
  }

}
