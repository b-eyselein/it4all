import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../_services/api.service';
import {ComponentWithCollectionTool} from '../_helpers/ComponentWithCollectionTool';
import {IExercise} from '../../../_interfaces/models';

@Component({templateUrl: './exercise-overview.component.html'})
export class ExerciseOverviewComponent extends ComponentWithCollectionTool implements OnInit {

  collectionId: number;
  exercise: IExercise;

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {
    super(route);

    if (!this.tool) {
      this.router.navigate(['../..']);
    }

    this.collectionId = parseInt(this.route.snapshot.paramMap.get('collId'), 10);
  }

  ngOnInit() {
    const exerciseId = parseInt(this.route.snapshot.paramMap.get('exId'), 10);

    this.apiService.getExercise(this.tool.id, this.collectionId, exerciseId)
      .subscribe((exercise) => this.exercise = exercise);
  }

}
