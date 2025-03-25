import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthStore } from '../../store/AuthStore';
import { UserService } from '../../service/user.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit, OnDestroy {
  
  fb = inject(FormBuilder);
  authService = inject(AuthService);
  route = inject(ActivatedRoute);
  router = inject(Router);
  authStore = inject(AuthStore);
  userService = inject(UserService);

  response!: Response;
  token!: string | undefined;  
  form!: FormGroup;
  successReg! : string | null;
  alertTimeout : any;
  errorMessage!: string | null;

  routeSub$!: Subscription;
  loginSub$!: Subscription;
  loginStatusSub$!: Subscription;
  
  ngOnInit(): void {
    this.loginStatusSub$ = this.authStore.isLoggedIn$.subscribe((data) => {
      if (data) {
        this.router.navigate(['/main']);
      }
    })

    this.form = this.fb.group({
      email : this.fb.control<string>('', [Validators.required, Validators.email]),
      password : this.fb.control<string>('', [Validators.required])
    });

    this.routeSub$ = this.route.queryParams.subscribe((params) => {
      if (params['registerSuccess']) {
        this.showSuccessAlert('Registration successful! Please log in.');
        this.router.navigate([], { queryParams: { registerSuccess: null }, queryParamsHandling: 'merge' });
      }
    })
  }

  closeAlert() {
    this.successReg = null;
    if (this.alertTimeout) {
      clearTimeout(this.alertTimeout);
    }
  }

  showSuccessAlert(message: string) {
    this.successReg = message;

    this.alertTimeout = setTimeout(() => {
      this.closeAlert();
    }, 5000);
  }


  processLogin() {
    console.log(this.form.value);
    this.loginSub$ = this.authService.verifyLogin(this.form.value).subscribe({
      next : (data) => {
        this.token = data.token;
        // localStorage.setItem('jwtToken', data.token);
        // localStorage.setItem("userId", data.userId);
        this.authStore.setToken(data.token);
        this.authStore.setUserId(data.userId);
        console.log(this.token);
        this.userService.fetchUserProfile();


        this.router.navigate(['/main']);
      },
      error : (error) => {
        console.log(error);
        if (error.status === 401) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'An unexpected error occurred. Please try again.';
        }
      },
      complete : () => this.loginSub$.unsubscribe()
    })
  }

  ngOnDestroy(): void {
    this.routeSub$.unsubscribe();
    this.loginStatusSub$.unsubscribe();
  }

}
