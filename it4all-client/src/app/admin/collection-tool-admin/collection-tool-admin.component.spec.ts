import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CollectionToolAdminComponent} from './collection-tool-admin.component';
import {AppRoutingModule} from '../../app-routing.module';
import {ToolOverviewComponent} from '../../tool-overview/tool-overview.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('CollectionToolAdminComponent', () => {
  let component: CollectionToolAdminComponent;
  let fixture: ComponentFixture<CollectionToolAdminComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [CollectionToolAdminComponent, ToolOverviewComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollectionToolAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
