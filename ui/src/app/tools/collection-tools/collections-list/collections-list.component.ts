import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ComponentWithCollectionTool} from '../_helpers/ComponentWithCollectionTool';
import {ApiService} from '../_services/api.service';
import {IExerciseCollection} from '../../../_interfaces/models';

@Component({templateUrl: './collections-list.component.html'})
export class CollectionsListComponent extends ComponentWithCollectionTool implements OnInit {

  collections: IExerciseCollection[];

  constructor(protected route: ActivatedRoute, private apiService: ApiService) {
    super(route);
  }

  ngOnInit() {
    this.apiService.getCollections(this.tool.id)
      .subscribe((collections) => this.collections = collections);
  }

}
