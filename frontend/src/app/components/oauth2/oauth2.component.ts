import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthStore } from '../../store/AuthStore';
import { UserService } from '../../service/user.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-oauth2',
  standalone: false,
  templateUrl: './oauth2.component.html',
  styleUrl: './oauth2.component.css'
})
export class Oauth2Component implements OnInit, OnDestroy {

  route = inject(ActivatedRoute);
  router = inject(Router);
  authStore = inject(AuthStore);
  userService = inject(UserService);

  sub$!: Subscription;

  ngOnInit(): void {
    this.sub$ = this.route.queryParams.subscribe((params) => {
      console.log("oauth");
      const email = params['email'];
      const token = params['token'];
      const userId = params['userId'];

      this.authStore.setToken(token);
      this.authStore.setUserId(userId);
      this.userService.fetchUserProfile();

      this.router.navigate(['/main']);
    });
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe();
  }

}
