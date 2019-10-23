import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ExerciseCollection, Tool} from '../../_interfaces/tool';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {ApiService} from '../../_services/api.service';

@Component({templateUrl: './coll-tool-admin.component.html'})
export class CollToolAdminComponent implements OnInit {

  tool: Tool;
  collections: ExerciseCollection[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {
    const toolId = this.route.snapshot.paramMap.get('toolId');

    this.tool = collectionTools.find((t) => t.id === toolId);

    if (!this.tool) {
      this.router.navigate(['/admin']);
    }
  }

  ngOnInit() {
    this.apiService.getCollections(this.tool.id)
      .subscribe((collections) => this.collections = collections);
  }

}
