import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { RecipeService } from '../../service/recipe.service';
import { AuthService } from '../../service/auth.service';
import { RecipeDetail } from '../../model/RecipeDetail';
import { User } from '../../model/User';
import { AuthStore } from '../../store/AuthStore';
import { Subscription, take } from 'rxjs';

@Component({
  selector: 'app-recipe-detail',
  standalone: false,
  templateUrl: './recipe-detail.component.html',
  styleUrl: './recipe-detail.component.css'
})
export class RecipeDetailComponent implements OnInit, OnDestroy {

  recipeId!: string | null;
  recipeDetail!: RecipeDetail;
  userDetail!: User;
  selectedStep: number | null = null;
  expAlertMessage: string | null = null;
  isOwner!: boolean;
  loggedInUser!: User | null;
  isRestricted = false;

  tierMap: { [key: string]: number } = {
    Bronze: 1,
    Silver: 2,
    Gold: 3,
    Diamond: 4,
  };

  route = inject(ActivatedRoute);
  recipeService = inject(RecipeService);
  authService = inject(AuthService);
  router = inject(Router);
  authStore = inject(AuthStore);

  recipeSub$!: Subscription;
  gatekeepSub$!: Subscription;
  privateSub$!: Subscription;
  sub$!: Subscription;

  ngOnInit(): void {
    // this.router.events.subscribe((event) => {
    //   if (event instanceof NavigationEnd) {
    //     const navigation = this.router.getCurrentNavigation();
    //     if (navigation?.extras.state) {
    //       const expActivity = navigation.extras.state['expActivity'];
    //       console.log('expActivity:', expActivity);
    //       if (expActivity) {
    //         console.log("showing exp alert");
    //         this.showExpAlert(expActivity);
    //       }
    //     }
    //   }
    // });

    this.sub$ = this.route.paramMap.subscribe(
        params => {
          this.recipeId = params.get("id");
          this.loadRecipeDetails();
        }
      );
  }

  // showExpAlert(expActivity: any) {
  //   console.log("in showExpAlert");
  //   this.expAlertMessage = `You earned ${expActivity.expAwarded} exp!`;

  //   setTimeout(() => {
  //     this.expAlertMessage = null;
  //   }, 5000);
  // }

  clearExpAlert() {
    this.expAlertMessage = null;
  }

  loadRecipeDetails() {
    this.recipeSub$ = this.recipeService.getRecipeById(this.recipeId).subscribe({
      next: (data) => {
        this.recipeDetail = data.recipe;
        this.userDetail = data.owner;
        this.authStore.user$.subscribe(
          (data) => {
            this.loggedInUser = data;
            this.isOwner = this.loggedInUser?.id === this.userDetail.id;
    
            this.isRestricted = this.isRecipeRestricted(this.recipeDetail);
    
            if (this.isRestricted || (this.recipeDetail.isPrivate && !this.isOwner)) {
              this.router.navigate(['/recipes']);
            }
          });
      },
      error: (error) => this.router.navigate(['/recipes']),
      complete: () => this.recipeSub$.unsubscribe()
    })
  }

  isRecipeRestricted(recipe: RecipeDetail): boolean {
    if (!recipe.isGateKept) return false;

    if (this.loggedInUser) {
      const isOwner = this.isOwner;
      const hasPremium = this.loggedInUser?.isPremium;
      const isSameOrHigherTier = this.tierMap[this.loggedInUser.tier] >= this.tierMap[recipe.ownerTier];

      return !isOwner && !hasPremium && !isSameOrHigherTier;
    }

    return false;
  }

  toggleStep(index: number) {
    this.selectedStep = this.selectedStep === index ? null : index;
  }

  toggleGatekeep() {
    if (!this.recipeDetail) return;

    this.gatekeepSub$ = this.recipeService.toggleGatekeep(this.recipeId!).subscribe({
      next: (data) => {
        // console.log(data);
        this.loadRecipeDetails();
      },
      error: (error) => console.log(error),
      complete: () => this.gatekeepSub$.unsubscribe()
    });
  }

  togglePrivate() {
    if (!this.recipeDetail.isPrivate && this.recipeDetail.isGateKept) {
      this.privateSub$ = this.recipeService.togglePrivate(this.recipeId!).subscribe({
        next: (data) => this.loadRecipeDetails(),
        error: (error) => console.log(error),
        complete: () => this.privateSub$.unsubscribe()
      });
      this.toggleGatekeep();
    } else {
      this.privateSub$ = this.recipeService.togglePrivate(this.recipeId!).subscribe({
        next: (data) => this.loadRecipeDetails(),
        error: (error) => console.log(error),
        complete: () => this.privateSub$.unsubscribe()
      });
    }
  }

  ngOnDestroy(): void {
    // console.log("destroying recipe detail");
    this.recipeService.addRecipeView(this.recipeId).subscribe({
      next: (data) => console.log(data),
      error: (error) => console.log(error)
    });
    this.sub$.unsubscribe();
  }

  reloadRecipe() {
    // console.log("reloading recipe");
    this.loadRecipeDetails();
  }

}
