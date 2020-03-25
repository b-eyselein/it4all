import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ComponentWithCollectionTool} from '../../tools/collection-tools/_helpers/ComponentWithCollectionTool';
import {IExerciseCollectionWithExerciseMetaData} from '../../_interfaces/exercise';
import {AdminEditCollectionGQL, AdminEditCollectionQuery} from "../../_services/apollo_services";

@Component({templateUrl: './admin-edit-collection.component.html'})
export class AdminEditCollectionComponent extends ComponentWithCollectionTool implements OnInit {

  adminEditCollectionQuery: AdminEditCollectionQuery;

  collection: IExerciseCollectionWithExerciseMetaData;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private adminEditCollectionGQL: AdminEditCollectionGQL,
  ) {
    super(route);

    if (!this.tool) {
      this.router.navigate(['/admin']);
    }
  }


  ngOnInit() {
    const collId = parseInt(this.route.snapshot.paramMap.get('collId'), 10);

    this.adminEditCollectionGQL
      .watch({toolId: this.tool.id, collId})
      .valueChanges
      .subscribe(({data}) => this.adminEditCollectionQuery = data);
  }

}
