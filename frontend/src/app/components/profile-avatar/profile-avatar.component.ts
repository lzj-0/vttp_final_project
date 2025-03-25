import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { User } from '../../model/User';
import { AuthStore } from '../../store/AuthStore';
import { LevelDetail } from '../../model/LevelDetail';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-profile-avatar',
  standalone: false,
  templateUrl: './profile-avatar.component.html',
  styleUrl: './profile-avatar.component.css'
})
export class ProfileAvatarComponent implements OnInit, OnDestroy {

  authStore = inject(AuthStore);

  loggedInUser!: User | null;
  levelDetail! : LevelDetail | null;
  isProfileAvatarVisible = false;

  userSub$!: Subscription;
  levelStatsSub$!: Subscription;


  ngOnInit(): void {
    console.log("profile avatar rendering");
    this.userSub$ = this.authStore.user$.subscribe((user) => {
      this.loggedInUser = user;
      console.log('Logged-in user updated:', this.loggedInUser);
    });

    this.levelStatsSub$ = this.authStore.levelStats$.subscribe((levelDetail) => {
      this.levelDetail = levelDetail;
      console.log('Level detail updated:', this.levelDetail);
    });

  }

  toggleProfileAvatar() {
    this.isProfileAvatarVisible = !this.isProfileAvatarVisible;
  }

  ngOnDestroy(): void {
    this.userSub$.unsubscribe();
    this.levelStatsSub$.unsubscribe();
  }
}
