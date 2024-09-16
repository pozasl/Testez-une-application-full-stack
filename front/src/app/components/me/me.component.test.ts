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
  const dateString1 =  "August 30, 2024";
  const date2 =  new Date(2024, 8, 6);
  const dateString2 =  "September 6, 2024";
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
    userService.getById = jest.fn(() => new Observable<any>((obs => obs.next(mockUser))));
    userService.delete = jest.fn(() => new Observable<any>((obs => obs.next("deleted"))));
    
    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should display non admin user Info and delete button at initilization', () => {
    const meElement: HTMLElement = fixture.nativeElement;
    expect(meElement.querySelectorAll('p')[0].textContent).toContain(mockUser.firstName + " " + mockUser.lastName.toUpperCase());
    expect(meElement.querySelectorAll('p')[1].textContent).toContain(mockUser.email);
    expect(meElement.querySelectorAll('p')[3].textContent).toContain(dateString1);
    expect(meElement.querySelectorAll('p')[4].textContent).toContain(dateString2);
    expect(meElement.querySelectorAll('button')[1]?.textContent).toBeTruthy();
  });

  it('Back button on click should navigate back in history', () => {
    const historySpy = jest.spyOn(window.history,"back");
    const backBtn: HTMLButtonElement = fixture.nativeElement.querySelector('button');
    backBtn.click();
    expect(historySpy).toBeCalled();
  });

  it('Click on Delete button should call delete service, notify the deletion and logout to root location', () => {
    const userServiceSpy = jest.spyOn(userService, "delete");
    const snackBarSpy = jest.spyOn(snackBar, "open");
    const routerSpy = jest.spyOn(router, "navigate");
    const deleteBtn: HTMLButtonElement = fixture.nativeElement.querySelectorAll('button')[1];
    deleteBtn.click();
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
  const dateString1 =  "August 26, 2024";
  const date2 =  new Date(2024, 8, 6);
  const dateString2 =  "September 6, 2024";
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

  it('should display admin Info at initialization', () => {
    const meElement: HTMLElement = fixture.nativeElement;
    expect(meElement.querySelectorAll("p")[0].textContent).toContain(mockAdminUser.firstName + " " + mockAdminUser.lastName.toUpperCase());
    expect(meElement.querySelectorAll("p")[1].textContent).toContain(mockAdminUser.email);
    expect(meElement.querySelectorAll("p")[2].textContent).toContain("You are admin");
    expect(meElement.querySelectorAll('p')[3].textContent).toContain(dateString1);
    expect(meElement.querySelectorAll('p')[4].textContent).toContain(dateString2);
  });

  it('should not display delete button', () => {
    const meElement: HTMLElement = fixture.nativeElement;
    expect(meElement.querySelectorAll('button')[1]).toBeUndefined();
  });

});
