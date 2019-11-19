import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CollectionAdminComponent} from './collection-admin.component';
import {AppRoutingModule, routingComponents} from '../../app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('CollectionAdminComponent', () => {
  let component: CollectionAdminComponent;
  let fixture: ComponentFixture<CollectionAdminComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [CollectionAdminComponent, ...routingComponents]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollectionAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
