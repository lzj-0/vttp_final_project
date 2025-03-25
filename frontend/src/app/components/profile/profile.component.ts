import { Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { User } from '../../model/User';
import { DonationComponent } from './donation/donation.component';
import { Recipe } from '../../model/Recipe';
import { Review } from '../../model/Review';
import { LevelDetail } from '../../model/LevelDetail';
import { AuthStore } from '../../store/AuthStore';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WithdrawService } from '../../service/withdraw.service';
import { Subscription } from 'rxjs';
import { UserService } from '../../service/user.service';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit, OnDestroy {

  route = inject(ActivatedRoute);
  authService = inject(AuthService);
  authStore = inject(AuthStore);
  fb = inject(FormBuilder);
  withdrawService = inject(WithdrawService);
  userService = inject(UserService);

  @ViewChild(DonationComponent) donationComponent!: DonationComponent;

  userId!: string | null;
  userDetail!: User;
  levelDetail!: LevelDetail;
  publicRecipes!: Recipe[];
  privateRecipes!: Recipe[];
  reviews!: Review[];
  activeTab: string = 'overview';
  isLoggedInUser = false;
  nextTier!: string;
  creditToCashForm!: FormGroup;
  cashAmount: number = 0;
  withdrawCreditAmount!: number;
  showResponseMessage = false;
  successMessage = false;
  failureMessage = false;
  loggedInUser: User | null = null;

  expToTier: { [key: string]: number } = {
    "Bronze": 0,
    "Silver": 1260,
    "Gold": 3090,
    "Diamond": 9640
  }

  tierMap: { [key: string]: number } = {
    Bronze: 1,
    Silver: 2,
    Gold: 3,
    Diamond: 4,
  };

  userIdSub$!: Subscription;
  userProfileSub$!: Subscription;
  withdrawSub$!: Subscription;


  ngOnInit(): void {
    this.userIdSub$ = this.route.paramMap.subscribe(params => {
      this.userId = params.get("userId");
      this.loggedInUser = this.authStore.getState().user;
      console.log("accessing" + this.userId);
      this.getUserProfile();
    });
  }

  getUserProfile() {
    this.userProfileSub$ = this.authService.getUserProfile(this.userId).subscribe({
      next: (data) => {
        console.log(data);
        this.userService.fetchUserProfile();
        this.userDetail = data.user;
        this.publicRecipes = data.publicRecipes;
        this.privateRecipes = data.privateRecipes;
        this.reviews = data.reviews;
        this.levelDetail = data.levelStats;
        this.loggedInUser = this.authStore.getState().user;
        this.isLoggedInUser = this.authService.getUserId() === this.userDetail.id;

        this.creditToCashForm = this.fb.group({
          selectedBank: ['', Validators.required],
          bankAccount: ['', [Validators.required, Validators.pattern(/^\d{10,}$/)]],
          credits: [100, [Validators.required, Validators.min(100), Validators.max(this.userDetail.credits)]],
        });

        this.updateCashAmount();

        if (this.levelDetail.tier === "Bronze") {
          this.nextTier = "Silver";
        } else if (this.levelDetail.tier === "Silver") {
          this.nextTier = "Gold";
        } else if (this.levelDetail.tier === "Gold") {
          this.nextTier = "Diamond";
        } else {
          this.nextTier = "";
        }

        if (this.donationComponent) {
          this.donationComponent.fetchUserBalance();
        }
      },
      error: (error) => console.log(error),
      complete: () => this.userProfileSub$.unsubscribe()
    });
  }

  switchTab(tab: string): void {
    this.activeTab = tab;
  }

  isRecipeRestricted(recipe: Recipe): boolean {
    if (!recipe.isGateKept) return false;

    if (this.loggedInUser) {
      const isOwner = this.isOwner(recipe);
      const hasPremium = this.loggedInUser?.isPremium;
      const isSameOrHigherTier = this.tierMap[this.loggedInUser.tier] >= this.tierMap[recipe.ownerTier];
      return !isOwner && !hasPremium && !isSameOrHigherTier;
    }

    return false;
  }

  isOwner(recipe: Recipe): boolean {
    return this.loggedInUser?.email === recipe.email;
  }

  updateCashAmount(): void {
    const credits = this.creditToCashForm.get('credits')?.value;
    this.cashAmount = credits / 100;
  }

  onConvertCredits(): void {
    this.showResponseMessage = false;
    this.successMessage = false;
    this.failureMessage = false;
    this.withdrawCreditAmount = this.creditToCashForm.get("credits")?.value;

    if (this.creditToCashForm.invalid) return;

    console.log(this.creditToCashForm.value);

    this.withdrawSub$ = this.withdrawService.withdrawCredit(this.creditToCashForm.value).subscribe({
      next: (data) => {
        console.log(data);
        this.showResponseMessage = true;
        this.successMessage = true;
        this.getUserProfile();
        this.updateCashAmount();
      },
      error: (error) => {
        console.log(error);
        this.showResponseMessage = true;
        this.failureMessage = true;
      },
      complete: () => this.withdrawSub$.unsubscribe()
    })

  }

  ngOnDestroy(): void {
    this.userIdSub$.unsubscribe();
  }

}
