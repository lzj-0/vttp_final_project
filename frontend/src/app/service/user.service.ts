import { inject, Injectable } from '@angular/core';
import { AuthStore } from '../store/AuthStore';
import { AuthService } from './auth.service';
import { take } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  authStore = inject(AuthStore);
  authService = inject(AuthService);

  fetchUserProfile(): void {
    this.authStore.userId$.pipe(take(1)).subscribe((userId) => {
      if (userId) {
        console.log("setting user profile...");
        this.authService.getUserProfile(userId).subscribe({
          next: (data) => {
            this.authStore.setUser(data.user);
            this.authStore.setLevelStats(data.levelStats);
          },
          error: (error) => console.log(error),
        });
      }
    });
  }

}
