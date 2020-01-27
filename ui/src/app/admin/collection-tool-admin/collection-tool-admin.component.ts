import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';

@Component({templateUrl: './collection-tool-admin.component.html'})
export class CollectionToolAdminComponent extends ComponentWithCollectionTool implements OnInit {

  collectionCount: number;
  lessonCount: number;

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {
    super(route);

    if (!this.tool) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/admin']);
    }
  }

  ngOnInit() {
    this.apiService.getCollectionCount(this.tool.id)
      .subscribe((collectionCount) => this.collectionCount = collectionCount);

    this.apiService.getLessonCount(this.tool.id)
      .subscribe((lessonCount) => this.lessonCount = lessonCount);
  }

}
