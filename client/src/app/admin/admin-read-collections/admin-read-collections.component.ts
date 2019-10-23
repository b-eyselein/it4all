import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {ExerciseCollection, Tool} from '../../_interfaces/tool';
import {AdminApiService} from '../../_services/admin-api.service';

@Component({templateUrl: './admin-read-collections.component.html'})
export class AdminReadCollectionsComponent implements OnInit {

  tool: Tool;
  loadedCollections: ExerciseCollection[];

  constructor(private route: ActivatedRoute, private apiService: AdminApiService, private router: Router) {
    const toolId = this.route.snapshot.paramMap.get('toolId');

    this.tool = collectionTools.find((t) => t.id === toolId);

    if (!this.tool) {
      this.router.navigate(['/admin']);
    }
  }

  ngOnInit() {
    this.apiService.loadCollections(this.tool.id)
      .subscribe((loadedCollections) => this.loadedCollections = loadedCollections);
  }

}
