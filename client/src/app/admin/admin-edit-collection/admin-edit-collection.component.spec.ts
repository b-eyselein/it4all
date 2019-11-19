import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdminEditCollectionComponent} from './admin-edit-collection.component';
import {AppRoutingModule, routingComponents} from '../../app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('AdminEditCollectionComponent', () => {
  let component: AdminEditCollectionComponent;
  let fixture: ComponentFixture<AdminEditCollectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [AdminEditCollectionComponent, ...routingComponents]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminEditCollectionComponent);
    component = fixture.componentInstance;
    component.collection = {
      id: 1, toolId: '', title: '', author: '', text: '', state: '', shortName: '', exercisesBasics: []
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
