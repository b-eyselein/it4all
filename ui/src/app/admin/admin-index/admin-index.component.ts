import {Component, OnInit} from '@angular/core';
import {AdminIndexGQL, AdminIndexQuery} from "../../_services/apollo_services";

@Component({templateUrl: './admin-index.component.html'})
export class AdminIndexComponent implements OnInit {

  adminIndexQuery: AdminIndexQuery;

  constructor(private adminIndexGQL: AdminIndexGQL) {
  }

  ngOnInit(): void {
    this.adminIndexGQL
      .watch({})
      .valueChanges
      .subscribe(({data}) => this.adminIndexQuery = data);
  }

}
