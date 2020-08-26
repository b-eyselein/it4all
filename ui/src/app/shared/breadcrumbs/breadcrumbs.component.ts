import {Component, Input} from '@angular/core';

export interface BreadCrumbPart {
  routerLinkPart: string;
  title: string;
}

interface BreadCrumb {
  routerLink: string;
  title: string;
}

@Component({
  selector: 'it4all-breadcrumbs',
  template: `
    <nav class="breadcrumb" aria-label="breadcrumbs">
      <ul>
        <li *ngFor="let part of breadCrumbs; let last = last" [class.is-active]="last">
          <a routerLink="{{part.routerLink}}">{{part.title}}</a>
        </li>
      </ul>
    </nav>
  `
})
export class BreadcrumbsComponent {

  @Input() parts: BreadCrumbPart[];

  get breadCrumbs(): BreadCrumb[] {
    let partAggregator: string[] = [];

    return this.parts.map(({routerLinkPart, title}) => {
      partAggregator.push(routerLinkPart);

      return {routerLink: partAggregator.join('/'), title};
    });
  }

}
