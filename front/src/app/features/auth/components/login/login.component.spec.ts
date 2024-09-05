import { HttpClient, HttpClientModule, HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { Observable } from 'rxjs';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { Router } from '@angular/router';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let sessionService: SessionService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [SessionService],
      imports: [
        RouterTestingModule.withRoutes(
          [
            {path: 'sessions', redirectTo : '/'},
            {path: 'login', redirectTo : '/'},
          ]
        ),
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule]
    })
      .compileComponents();
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('submit should call authenticate service', () => {
    const authServiceSpy = jest.spyOn(authService,"login");
    component.submit();
    expect(authServiceSpy).toBeCalled()
  });

  it('submit with login failure should set onError', () => {
    const httpError = new HttpErrorResponse({ error: 'Unhautorized', status: 401 });
    authService.login = jest.fn(() => new Observable((obs) => obs.error(httpError)));
    component.submit();
    fixture.detectChanges();
    expect(component.onError).toBe(true);
  });

  it('Submit button should be disabled when fields are empty', () => {
    const submitBtn : HTMLButtonElement | null = fixture.nativeElement.querySelector('button[type="submit"]')
    expect(submitBtn?.disabled).toBe(true);
  });

  it('email field should be invalid with wrong email and submit should be disabled', () => {
    component.form.setValue({email: 'bob-test.com', password: '123456'});
    fixture.detectChanges();
    const emailField : HTMLFormElement | null = fixture.nativeElement.querySelector('input[ng-reflect-name="email"]');
    const submitBtn : HTMLButtonElement | null = fixture.nativeElement.querySelector('button[type="submit"]')
    expect(submitBtn?.disabled).toBe(true);
    expect(emailField?.classList).toContain("ng-invalid");
  });

  it('password field should be invalid when empty and submit should be disabled', () => {
    component.form.setValue({email: 'bob@test.com', password: ''});
    fixture.detectChanges();
    const passField : HTMLFormElement | null = fixture.nativeElement.querySelector('input[ng-reflect-name="password"]');
    const submitBtn : HTMLButtonElement | null = fixture.nativeElement.querySelector('button[type="submit"]')
    expect(submitBtn?.disabled).toBe(true);
    expect(passField?.classList).toContain("ng-invalid");
  });

  it('Submit button should be enabled when fields are ok', () => {
    component.form.setValue({email: 'bob@test.com', password: '123456'});
    fixture.detectChanges();
    const submitBtn : HTMLButtonElement | null = fixture.nativeElement.querySelector('button[type="submit"]')
    expect(submitBtn?.disabled).toBe(false);
  });

  it('click on Submit button with successfull login should login session and go to /session', () => {
    component.form.setValue({email: 'bob@test.com', password: '123456'});
    fixture.detectChanges();
    const submitBtn : HTMLButtonElement | null = fixture.nativeElement.querySelector('button[type="submit"]') 
    const sessionServiceSpy = jest.spyOn(sessionService,"logIn");
    const session = Object({
      token: "token",
      type: "type",
      username: "bob",
      firstName: "Bob",
      lastName: "Le Bricoleur",
      admin: true
    }) as SessionInformation;
    const routerSpy = jest.spyOn(router,"navigate");
    authService.login = jest.fn(() => new Observable((obs) => obs.next(session)));
    submitBtn?.dispatchEvent(new MouseEvent('click', {bubbles: true}))
    expect(sessionServiceSpy).toBeCalledWith(session)
    expect(routerSpy).toBeCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  });

  it('click on Submit button with login failure should display error', () => {
    const httpError = new HttpErrorResponse({ error: 'Unhautorized', status: 401 });
    const loginElement: HTMLElement = fixture.nativeElement;
    const submitBtn : HTMLButtonElement | null = loginElement.querySelector('button[type="submit"]') 
    authService.login = jest.fn(() => new Observable((obs) => obs.error(httpError)));
    component.form.setValue({email: 'bob@test.com', password: '123456'});
    fixture.detectChanges();
    expect(submitBtn?.disabled).toBe(false);
    submitBtn?.dispatchEvent(new MouseEvent('click', {bubbles: true}))
    fixture.detectChanges(); 
    expect(loginElement.querySelector('p.error')?.textContent).toContain('An error occurred');
  });

});
