import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ReactiveFormsModule } from '@angular/forms';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { RecipeformComponent } from './components/recipeform/recipeform.component';
import { provideHttpClient } from '@angular/common/http';
import { RecipeComponent } from './components/recipe/recipe.component';
import { StarRatingComponent } from './components/star-rating/star-rating.component';
import { RecipeService } from './service/recipe.service';
import { RecipeDetailComponent } from './components/recipe-detail/recipe-detail.component';
import { HomeComponent } from './components/home/home.component';
import { RegisterComponent } from './components/register/register.component';
import { RouterModule } from '@angular/router';
import { SubscriptionComponent } from './components/subscription/subscription.component';
import { LoginComponent } from './components/login/login.component';
import { Oauth2Component } from './components/oauth2/oauth2.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AlertComponent } from './components/alert/alert.component';
import { SuccessComponent } from './components/subscription/success/success.component';
import { FailureComponent } from './components/subscription/failure/failure.component';
import { MainComponent } from './components/main/main.component';
import { ReviewsComponent } from './components/reviews/reviews.component';
import { BackComponent } from './components/back/back.component';
import { ProfileComponent } from './components/profile/profile.component';
import { DonationComponent } from './components/profile/donation/donation.component';
import { CatalogueComponent } from './components/catalogue/catalogue.component';
import { ProfileAvatarComponent } from './components/profile-avatar/profile-avatar.component';
import { CookingImageComponent } from './components/cooking-image/cooking-image.component';
import { LeaderboardComponent } from './components/leaderboard/leaderboard.component';
import { CalorieTrackerComponent } from './components/calorie-tracker/calorie-tracker.component';
import { PopupComponent } from './components/popup/popup.component';
import { TutorialComponent } from './components/tutorial/tutorial.component';
import { SecretComponent } from './components/secret/secret.component';

@NgModule({
  declarations: [
    AppComponent,
    RecipeformComponent,
    RecipeComponent,
    StarRatingComponent,
    RecipeDetailComponent,
    HomeComponent,
    RegisterComponent,
    SubscriptionComponent,
    LoginComponent,
    Oauth2Component,
    AlertComponent,
    SuccessComponent,
    FailureComponent,
    MainComponent,
    ReviewsComponent,
    BackComponent,
    ProfileComponent,
    DonationComponent,
    CatalogueComponent,
    ProfileAvatarComponent,
    CookingImageComponent,
    LeaderboardComponent,
    CalorieTrackerComponent,
    PopupComponent,
    TutorialComponent,
    SecretComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterModule,
    ReactiveFormsModule,
    DragDropModule,
    BrowserAnimationsModule
  ],
  providers: [RecipeService, provideHttpClient()],
  bootstrap: [AppComponent]
})
export class AppModule { }
