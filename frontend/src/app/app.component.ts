import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AuthService } from './service/auth.service';
import { AuthStore } from './store/AuthStore';
import { UserService } from './service/user.service';
import { initFlowbite } from 'flowbite';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

  authService = inject(AuthService);
  authStore = inject(AuthStore);
  userService = inject(UserService);

  userId! : string | null;
  user$ = this.authStore.user$;

  ngOnInit() {
    initFlowbite();
    setInterval(() => this.authService.checkTokenExpiry(), 60000);
    this.userService.fetchUserProfile();
  }

  logout() {
    this.authService.logout();
  }

}
