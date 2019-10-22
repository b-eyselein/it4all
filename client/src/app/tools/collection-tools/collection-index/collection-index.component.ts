import {Component, OnInit} from '@angular/core';
import {ApiService} from '../../../_services/api.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Exercise, ExerciseCollection, Tool} from '../../../_interfaces/tool';
import {collectionTools} from '../collection-tools-list';

@Component({templateUrl: './collection-index.component.html'})
export class CollectionIndexComponent implements OnInit {

  tool: Tool;
  collId: number;

  collection: ExerciseCollection;

  exercises: Exercise[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {
    const toolId: string = this.route.snapshot.paramMap.get('toolId');
    this.tool = collectionTools.find((t) => t.id === toolId);

    if (!this.tool) {
      this.router.navigate(['/']);
    }

    this.collId = parseInt(route.snapshot.paramMap.get('collId'), 10);
  }

  ngOnInit() {
    this.apiService.getCollection(this.tool.id, this.collId)
      .subscribe((collection: ExerciseCollection | undefined) => {
        if (collection) {
          this.collection = collection;
          this.updateExercises();
        } else {
          this.router.navigate(['/tools', this.tool.id]);
        }
      });
  }

  updateExercises(): void {
    this.apiService.getExercises(this.tool.id, this.collection.id)
      .subscribe((exercises: Exercise[]) => this.exercises = exercises);
  }

}
