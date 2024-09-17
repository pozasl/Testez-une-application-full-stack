import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';

import { MeComponent } from './me.component';
import { UserService } from 'src/app/services/user.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';

class MatSnackBarStub{
  open(){
    return {
      // Can't wait 3 seconds for a test
      onAction: () => new Observable((obs) => obs.next())
    };
  }
}

describe('MeComponent with user', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let snackBar: MatSnackBar;
  let userService: UserService;
  let router: Router;
  const date1 =  new Date(2024, 7, 30);
  const date2 =  new Date(2024, 8, 6);
  const mockSessionService = {
    sessionInformation: {
      admin: false,
      id: 2,
    },
    logOut : jest.fn()
  }

  const mockUser = {
    id:'2',
    email: "alice@test.com",
    lastName: "In Wonderland",
    firstName: "Alice",
    admin: false,
    createdAt: date1,
    updatedAt: date2
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        RouterTestingModule,
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: {getById: jest.fn(), delete: jest.fn()} },
        { provide: MatSnackBar, useClass: MatSnackBarStub },
        { provide: Router, useValue: {navigate: jest.fn()} },
      ],
    })
      .compileComponents();
    snackBar = TestBed.inject(MatSnackBar);
    userService = TestBed.inject(UserService);
    router = TestBed.inject(Router);
    router.navigate = jest.fn();
    userService.getById = jest.fn(() => new Observable<any>((obs => obs.next(mockUser))));
    userService.delete = jest.fn(() => new Observable<any>((obs => obs.next("deleted"))));
    
    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get non admin user Info from session at initilization', () => {
    const userServiceSpy = jest.spyOn(userService, "getById");
    expect(userServiceSpy).toBeCalledWith('2');
    expect(component.user).toBe(mockUser);
    expect(component.user?.admin).toBeFalsy();
  });

  it('Back should navigate back in history', () => {
    const historySpy = jest.spyOn(window.history,"back");
    component.back();
    expect(historySpy).toBeCalled();
  });

  it('Delete should call delete service, notify the deletion and logout to root location', () => {
    const userServiceSpy = jest.spyOn(userService, "delete");
    const snackBarSpy = jest.spyOn(snackBar, "open");
    const routerSpy = jest.spyOn(router, "navigate");
    component.delete()
    expect(userServiceSpy).toBeCalledWith("2");
    expect(snackBarSpy).toBeCalled();
    expect(routerSpy).toBeCalledWith(['/']);
  });

});

describe('MeComponent with admin', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: UserService;
  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
    },
    logOut : jest.fn()
  }

  const date1 =  new Date(2024, 7, 26);
  const date2 =  new Date(2024, 8, 6);
  const mockAdminUser = {
    id:1,
    email: "bob@test.com",
    lastName: "Le Bricoleur",
    firstName: "Bob",
    admin: true,
    createdAt: date1,
    updatedAt: date2
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        RouterTestingModule,
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: {getById: jest.fn(), delete: jest.fn()} },
      ],
    })
      .compileComponents();
    userService = TestBed.inject(UserService);
    userService.getById = jest.fn(() => new Observable<any>((obs => obs.next(mockAdminUser))));
    
    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should get admin user Info from session at initialization', () => {
    const userServiceSpy = jest.spyOn(userService, "getById");
    expect(userServiceSpy).toBeCalledWith('1');
    expect(component.user).toBe(mockAdminUser);
    expect(component.user?.admin).toBeTruthy();
  });

});
