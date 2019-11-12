import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdminEditCollectionComponent} from './admin-edit-collection.component';
import {AppRoutingModule} from '../../app-routing.module';
import {ToolOverviewComponent} from '../../tool-overview/tool-overview.component';

describe('AdminEditCollectionComponent', () => {
  let component: AdminEditCollectionComponent;
  let fixture: ComponentFixture<AdminEditCollectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AdminEditCollectionComponent],
      imports: [AppRoutingModule, ToolOverviewComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminEditCollectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
