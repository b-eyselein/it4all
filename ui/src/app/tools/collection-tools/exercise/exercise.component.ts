import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../_services/api.service';
import {IExercise, IExerciseCollection} from '../../../_interfaces/models';
import {CollectionTool, ToolPart} from '../../../_interfaces/tool';
import {collectionTools} from '../collection-tools-list';

@Component({templateUrl: './exercise.component.html'})
export class ExerciseComponent implements OnInit {

  tool: CollectionTool;
  collection: IExerciseCollection;
  exercise: IExercise;
  part: ToolPart;

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {
    this.route.paramMap.subscribe((paramMap) => {
      this.tool = collectionTools.find((t) => t.id === paramMap.get('toolId'));
      this.part = this.tool.parts.find((p) => p.id === paramMap.get('partId'));
    });
  }

  private updateExercise(): void {
    const exerciseId = parseInt(this.route.snapshot.paramMap.get('exId'), 10);

    this.apiService.getExercise(this.tool.id, this.collection.id, exerciseId)
      .subscribe((ex: IExercise | undefined) => {
        if (ex) {
          this.exercise = ex;
        } else {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['../..']);
        }
      });
  }

  ngOnInit() {
    const collectionId = parseInt(this.route.snapshot.paramMap.get('collId'), 10);

    this.apiService.getCollection(this.tool.id, collectionId)
      .subscribe((coll: IExerciseCollection | undefined) => {
        if (coll) {
          this.collection = coll;
          this.updateExercise();
        } else {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['../../../..']);
        }
      });
  }

}
