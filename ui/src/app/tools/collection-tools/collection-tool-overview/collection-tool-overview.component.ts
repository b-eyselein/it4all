import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ComponentWithCollectionTool} from '../_helpers/ComponentWithCollectionTool';

@Component({templateUrl: './collection-tool-overview.component.html'})
export class CollectionToolOverviewComponent extends ComponentWithCollectionTool  {

  constructor(private route: ActivatedRoute) {
    super(route);
  }

}
