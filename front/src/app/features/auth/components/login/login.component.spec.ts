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
        RouterTestingModule,
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

  it('submit should authenticate user', () => {
    const authServiceSpy = jest.spyOn(authService,"login");
    component.submit();
    expect(authServiceSpy).toBeCalled()
  });

  it('sucessfull submit should login session and go to /session', () => {
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
    component.submit();
    expect(sessionServiceSpy).toBeCalledWith(session)
    expect(routerSpy).toBeCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  });

  it('failing submit should set onError', () => {
    const httpError = new HttpErrorResponse({ error: 'Unhautorized', status: 401 });
    authService.login = jest.fn(() => new Observable((obs) => obs.error(httpError)));
    component.submit();
    expect(component.onError).toBe(true);
  });

});
