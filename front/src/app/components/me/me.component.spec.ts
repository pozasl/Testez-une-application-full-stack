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

class MatSnackBarStub{
  open(){
    return {
      // Can't wait 3 seconds for a test
      onAction: () => new Observable((obs) => obs.next())
    };
  }
}

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let snackBar: MatSnackBar;
  let userService: UserService;
  let router: Router;
  const now =  new Date();
  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
    },
    logOut : jest.fn()
  }

  const mockUser = {
    id:1,
    email: "bob@test.com",
    lastName: "Le Bricoleur",
    firstName: "Bob",
    admin: true,
    createdAt: now,
    updatedAt: now
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
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

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get user Info from session at initilization', () => {
    const userServiceSpy = jest.spyOn(userService, "getById");
    expect(userServiceSpy).toHaveBeenCalledWith("1");
    expect(component.user).toBe(mockUser);
  });

  it('back should navigate back in history', () => {
    const historySpy = jest.spyOn(window.history,"back");
    component.back()
    expect(historySpy).toHaveBeenCalled();
  });

  it('delete should delete user, notify the deletion and logout to root location', () => {
    const userServiceSpy = jest.spyOn(userService, "delete");
    const snackBarSpy = jest.spyOn(snackBar, "open");
    const routerSpy = jest.spyOn(router, "navigate");
    component.delete();
    expect(userServiceSpy).toHaveBeenCalledWith("1");
    expect(snackBarSpy).toHaveBeenCalled();
    expect(routerSpy).toHaveBeenCalledWith(['/']);
  });

});
