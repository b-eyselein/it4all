import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ExerciseCollection, Tool} from '../../_interfaces/tool';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {ApiService} from '../../_services/api.service';
import {DexieService} from '../../_services/dexie.service';

@Component({templateUrl: './collection-tool-admin.component.html'})
export class CollectionToolAdminComponent implements OnInit {

  tool: Tool;
  collections: ExerciseCollection[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService, private dexieService: DexieService) {
    const toolId = this.route.snapshot.paramMap.get('toolId');

    this.tool = collectionTools.find((t) => t.id === toolId);

    if (!this.tool) {
      this.router.navigate(['/admin']);
    }
  }

  private fetchExerciseBasics(): void {
    this.collections.forEach((collection) => {
      this.apiService.getExerciseBasics(this.tool.id, collection.id)
        .subscribe((exerciseBasics) => collection.exercisesBasics = exerciseBasics);
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
