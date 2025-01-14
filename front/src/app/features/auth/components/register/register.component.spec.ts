import { HttpClientModule, HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let router: Router;

  const registerRequest = {
    email: "bob@tst.com",
    firstName: "Bob",
    lastName: "Le Bricoleur",
    password: "Pass1234"
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        RouterTestingModule.withRoutes(
          [
            {path: 'login', redirectTo : '/'},
          ]
        ),
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,  
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    })
      .compileComponents();
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('submit should call register service', () => {
    const authServiceSpy = jest.spyOn(authService,"register");
    component.submit();
    expect(authServiceSpy).toBeCalled()
  });

  it('failing regiter should set onError', () => {
    const httpError = new HttpErrorResponse({ error: 'Unhautorized', status: 401 });
    authService.register = jest.fn(() => new Observable((obs) => obs.error(httpError)));
    component.submit();
    expect(component.onError).toBe(true);
  });

  it('submit with successfull register should go to /login', () => {
    const routerSpy = jest.spyOn(router,"navigate");
    authService.register = jest.fn(() => new Observable((obs) => obs.next()));
    const authServiceSpy = jest.spyOn(authService,"register");
    component.form.setValue(registerRequest);
    component.submit();
    expect(authServiceSpy).toBeCalledWith(registerRequest);
    expect(routerSpy).toBeCalledWith(['/login']);
    expect(component.onError).toBe(false);
  });
  
});
