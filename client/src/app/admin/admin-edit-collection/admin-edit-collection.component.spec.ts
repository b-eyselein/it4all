import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdminEditCollectionComponent} from './admin-edit-collection.component';
import {AdminRoutingModule} from '../admin.routing';

describe('AdminEditCollectionComponent', () => {
  let component: AdminEditCollectionComponent;
  let fixture: ComponentFixture<AdminEditCollectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AdminRoutingModule],
      declarations: [AdminEditCollectionComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminEditCollectionComponent);
    component = fixture.componentInstance;
    component.collection = {
      id: 1, toolId: '', title: '', author: '', text: '', state: '', shortName: '', exercises: []
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
