import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RecipeDetailComponent } from './components/recipe-detail/recipe-detail.component';
import { RecipeComponent } from './components/recipe/recipe.component';
import { HomeComponent } from './components/home/home.component';
import { RegisterComponent } from './components/register/register.component';
import { SubscriptionComponent } from './components/subscription/subscription.component';
import { LoginComponent } from './components/login/login.component';
import { Oauth2Component } from './components/oauth2/oauth2.component';
import { SuccessComponent } from './components/subscription/success/success.component';
import { FailureComponent } from './components/subscription/failure/failure.component';
import { MainComponent } from './components/main/main.component';
import { AuthGuardService } from './service/auth-guard.service';
import { RecipeformComponent } from './components/recipeform/recipeform.component';
import { ProfileComponent } from './components/profile/profile.component';
import { CatalogueComponent } from './components/catalogue/catalogue.component';
import { LeaderboardComponent } from './components/leaderboard/leaderboard.component';
import { CalorieTrackerComponent } from './components/calorie-tracker/calorie-tracker.component';
import { TutorialComponent } from './components/tutorial/tutorial.component';
import { SecretComponent } from './components/secret/secret.component';

const routes: Routes = [
  { path : '', component : HomeComponent },
  { path : 'main', component : MainComponent, canActivate : [AuthGuardService]},
  { path : "recipes", component : CatalogueComponent},
  { path : 'recipe/:id', component : RecipeDetailComponent, canActivate : [AuthGuardService], runGuardsAndResolvers: 'always' },
  { path : 'subscription', component : SubscriptionComponent},
  { path : 'subscription/success', component : SuccessComponent, canActivate : [AuthGuardService]},
  { path : 'subscription/failure', component : FailureComponent, canActivate : [AuthGuardService]},
  { path : "register", component : RegisterComponent},
  { path : "login", component: LoginComponent },
  { path : "oauth2/callback", component: Oauth2Component},
  { path : "addrecipe", component : RecipeformComponent, canActivate : [AuthGuardService]},
  { path : "profile/:userId", component : ProfileComponent, canActivate : [AuthGuardService]},
  { path : "leaderboard", component : LeaderboardComponent, canActivate : [AuthGuardService]},
  { path : "calorietracker", component : CalorieTrackerComponent, canActivate : [AuthGuardService]},
  { path : "tutorial", component : TutorialComponent, canActivate : [AuthGuardService]},
  { path : "secret", component : SecretComponent, canActivate : [AuthGuardService]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
