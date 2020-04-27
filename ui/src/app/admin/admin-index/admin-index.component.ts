import {Component, OnDestroy, OnInit} from '@angular/core';
import {AdminIndexGQL, AdminIndexQuery} from "../../_services/apollo_services";
import {Subscription} from "rxjs";

@Component({templateUrl: './admin-index.component.html'})
export class AdminIndexComponent implements OnInit, OnDestroy {

  private apolloSub: Subscription;

  adminIndexQuery: AdminIndexQuery;


  constructor(private adminIndexGQL: AdminIndexGQL) {
  }

  ngOnInit(): void {
    this.apolloSub = this.adminIndexGQL
      .watch({})
      .valueChanges
      .subscribe(({data}) => this.adminIndexQuery = data);
  }

  ngOnDestroy(): void {
    this.apolloSub.unsubscribe();
  }

}
