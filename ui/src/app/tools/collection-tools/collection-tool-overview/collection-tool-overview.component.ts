import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ComponentWithCollectionTool} from '../_helpers/ComponentWithCollectionTool';
import {ApiService} from '../_services/api.service';
import {IProficiencies} from '../../../_interfaces/models';

@Component({templateUrl: './collection-tool-overview.component.html'})
export class CollectionToolOverviewComponent extends ComponentWithCollectionTool implements OnInit {

  private proficiencies: IProficiencies;

  constructor(private route: ActivatedRoute, private apiService: ApiService) {
    super(route);
  }

  ngOnInit(): void {
    console.info('TODO!');

    this.apiService.getProficiencies(this.tool.id)
      .subscribe((proficiencies) => {
        if (proficiencies) {
          this.proficiencies = proficiencies;
          console.info(JSON.stringify(this.proficiencies, null, 2));
        }
      });
  }

}
