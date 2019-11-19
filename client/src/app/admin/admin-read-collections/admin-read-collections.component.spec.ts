import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdminReadCollectionsComponent} from './admin-read-collections.component';
import {AppRoutingModule, routingComponents} from '../../app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('AdminReadCollectionsComponent', () => {
  let component: AdminReadCollectionsComponent;
  let fixture: ComponentFixture<AdminReadCollectionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [AdminReadCollectionsComponent, ...routingComponents]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminReadCollectionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
