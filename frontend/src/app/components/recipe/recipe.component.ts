import { Component, inject, Input, OnInit } from '@angular/core';
import { RecipeService } from '../../service/recipe.service';
import { Recipe } from '../../model/Recipe';
import { AuthService } from '../../service/auth.service';
import { Subscription } from 'rxjs';
import { User } from '../../model/User';
import { AuthStore } from '../../store/AuthStore';

@Component({
  selector: 'app-recipe',
  standalone: false,
  templateUrl: './recipe.component.html',
  styleUrl: './recipe.component.css'
})
export class RecipeComponent implements OnInit {

  @Input() recipes!: Recipe[];

  recipeService = inject(RecipeService);
  authService = inject(AuthService);
  authStore = inject(AuthStore);
  sub!: Subscription;

  loggedInUser: User | null = null;

  tierMap: { [key: string]: number } = {
    Bronze: 1,
    Silver: 2,
    Gold: 3,
    Diamond: 4,
  };

  ngOnInit(): void {

    if (!this.recipes) {
      this.sub = this.recipeService.getRecipes().subscribe({
        next: (data) => {
          this.recipes = data.recipes;
          this.loggedInUser = this.authStore.getState().user;
          console.log(this.recipes)
        },
        error: (error) => console.log(error),
        complete: () => this.sub.unsubscribe()
      })
    }

    if (!this.loggedInUser) {
      this.loggedInUser = this.authStore.getState().user;
    }
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

}
