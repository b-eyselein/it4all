import {Component, OnInit} from '@angular/core';
import {ExerciseCollection, Tool} from '../../../_interfaces/tool';
import {ApiService} from '../../../_services/api.service';
import {ActivatedRoute, Router} from '@angular/router';
import {collectionTools} from '../collection-tools-list';

@Component({templateUrl: './tool-index.component.html'})
export class ToolIndexComponent implements OnInit {

  tool: Tool;
  collections: ExerciseCollection[] = [];

  constructor(private route: ActivatedRoute, private apiService: ApiService, private router: Router) {
    const toolId = this.route.snapshot.paramMap.get('toolId');
    this.tool = collectionTools.find((t) => t.id === toolId);

    if (!this.tool) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit() {
    this.apiService.getCollections(this.tool.id)
      .subscribe((collections: ExerciseCollection[]) => this.collections = collections);
  }

}
