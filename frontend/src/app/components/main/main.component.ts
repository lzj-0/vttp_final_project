import { AfterViewInit, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { RecipeService } from '../../service/recipe.service';
import { RecipeDetail } from '../../model/RecipeDetail';
import { User } from '../../model/User';
import { LevelDetail } from '../../model/LevelDetail';
import { AuthStore } from '../../store/AuthStore';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-main',
  standalone: false,
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent implements OnInit, AfterViewInit {

  recipeService = inject(RecipeService);
  authStore = inject(AuthStore);

  trendingRecipes!: RecipeDetail[];
  newRecipes!: RecipeDetail[];
  loggedInUser!: User | null;
  levelDetail!: LevelDetail | null;

  tierMap: { [key: string]: number } = {
    Bronze: 1,
    Silver: 2,
    Gold: 3,
    Diamond: 4,
  };

  userSub$!: Subscription;
  levelStats$! : Subscription;
  newRecipesSub$!: Subscription;
  trendingRecipesSub$!: Subscription;

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.fetchTrendingRecipes();
    this.fetchNewRecipes();

    this.userSub$ = this.authStore.user$.subscribe((user) => {
      this.loggedInUser = user;
      console.log('Logged-in user updated:', this.loggedInUser);
    });

    this.levelStats$ = this.authStore.levelStats$.subscribe((levelDetail) => {
      this.levelDetail = levelDetail;
      console.log('Level detail updated:', this.levelDetail);
    });
  }

  fetchTrendingRecipes(): void {
    this.trendingRecipesSub$ = this.recipeService.getTrendingRecipes().subscribe({
      next: (data) => this.trendingRecipes = data.recipes,
      error: (error) => console.error(error),
      // complete : () => this.trendingRecipesSub$.unsubscribe()
    });
  }

  fetchNewRecipes(): void {
    this.newRecipesSub$ = this.recipeService.getNewRecipes().subscribe({
      next: (data) => this.newRecipes = data.recipes,
      error: (error) => console.error(error),
      // complete : () => this.newRecipesSub$.unsubscribe()
    });
  }

  ngAfterViewInit(): void {
    this.setupCircularScroll('.animate-scroll');
  }

  setupCircularScroll(selector: string): void {
    const container = document.querySelector(selector);
    if (!container) return;

    container.addEventListener('animationiteration', () => {
      const firstChild = container.firstElementChild;
      if (firstChild) {
        container.appendChild(firstChild.cloneNode(true));
        firstChild.remove();
      }
    });
  }

  isRecipeRestricted(recipe: RecipeDetail): boolean {
    if (!recipe.isGateKept) return false;

    if (this.loggedInUser) {
      const isOwner = this.isOwner(recipe);
      const hasPremium = this.loggedInUser?.isPremium;
      const isSameOrHigherTier = this.tierMap[this.loggedInUser.tier] >= this.tierMap[recipe.ownerTier];
      return !isOwner && !hasPremium && !isSameOrHigherTier;
    }

    return true;
  }

  isOwner(recipe: RecipeDetail): boolean {
    return this.loggedInUser?.email === recipe.email;
  }
  
  // ngOnDestroy(): void {
  //   this.userSub$.unsubscribe();
  //   this.levelStats$.unsubscribe();
  // }

}
