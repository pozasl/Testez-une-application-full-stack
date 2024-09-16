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

  it('Submit button should be disabled when fields are empty', () => {
    const submitBtn : HTMLButtonElement | null = fixture.nativeElement.querySelector('button[type="submit"]')
    expect(submitBtn?.disabled).toBe(true);
  });

  it('invalid field should be invalid and submit should be disabled', () => {
    component.form.setValue({
      email: "aa",
      firstName: "",
      lastName: "",
      password: ""
    });
    fixture.detectChanges();
    const emailField : HTMLFormElement | null = fixture.nativeElement.querySelector('input[ng-reflect-name="email"]');
    const firstNameField : HTMLFormElement | null = fixture.nativeElement.querySelector('input[ng-reflect-name="firstName"]');
    const lastNameField : HTMLFormElement | null = fixture.nativeElement.querySelector('input[ng-reflect-name="lastName"]');
    const passField : HTMLFormElement | null = fixture.nativeElement.querySelector('input[ng-reflect-name="password"]');
    const submitBtn : HTMLButtonElement | null = fixture.nativeElement.querySelector('button[type="submit"]')
    expect(submitBtn?.disabled).toBe(true);
    expect(emailField?.classList).toContain("ng-invalid");
    expect(firstNameField?.classList).toContain("ng-invalid");
    expect(lastNameField?.classList).toContain("ng-invalid");
    expect(passField?.classList).toContain("ng-invalid");
  });

  

  it('Submit button should be enabled when fields are ok', () => {
    component.form.setValue(registerRequest);
    fixture.detectChanges();
    const submitBtn : HTMLButtonElement | null = fixture.nativeElement.querySelector('button[type="submit"]')
    expect(submitBtn?.disabled).toBe(false);
  });

  it('click on Submit button with successfull register should go to /login', () => {
    authService.register = jest.fn(() => new Observable((obs) => obs.next()));
    component.form.setValue(registerRequest);
    fixture.detectChanges();
    const submitBtn : HTMLButtonElement | null = fixture.nativeElement.querySelector('button[type="submit"]') 
    const authServiceSpy = jest.spyOn(authService,"register");
    const routerSpy = jest.spyOn(router,"navigate");
    submitBtn?.click()
    expect(authServiceSpy).toBeCalledWith(registerRequest);
    expect(routerSpy).toBeCalledWith(['/login']);
  });

  it('click on Submit button with register failure should display error message', () => {
    const httpError = new HttpErrorResponse({ error: 'BadRequest', status: 400 });
    const loginElement: HTMLElement = fixture.nativeElement;
    authService.register = jest.fn(() => new Observable((obs) => obs.error(httpError)));
    component.form.setValue(registerRequest);
    fixture.detectChanges();
    const submitBtn : HTMLButtonElement | null = fixture.nativeElement.querySelector('button[type="submit"]') 
    submitBtn?.click()
    fixture.detectChanges(); 
    expect(loginElement.querySelector('span.error')?.textContent).toContain('An error occurred');
  });
});
