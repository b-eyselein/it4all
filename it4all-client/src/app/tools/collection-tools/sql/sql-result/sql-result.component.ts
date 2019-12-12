import {Component, Input, OnInit} from '@angular/core';
import {SqlResult} from '../sql-interfaces';

@Component({selector: 'it4all-sql-result', templateUrl: './sql-result.component.html'})
export class SqlResultComponent implements OnInit {

  @Input() result: SqlResult;

  constructor() {
  }

  ngOnInit() {
  }

}
