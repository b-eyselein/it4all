import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdminIndexComponent} from './admin-index.component';
import {AppRoutingModule, routingComponents} from '../../app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('AdminIndexComponent', () => {
  let component: AdminIndexComponent;
  let fixture: ComponentFixture<AdminIndexComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [AdminIndexComponent, ...routingComponents]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminIndexComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
