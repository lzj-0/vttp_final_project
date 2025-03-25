import { Component, inject, OnInit, Output } from '@angular/core';
import { RecipeDetail } from '../../model/RecipeDetail';
import { ActivatedRoute, Router } from '@angular/router';
import { RecipeService } from '../../service/recipe.service';
import { AuthService } from '../../service/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReviewService } from '../../service/review.service';
import { Review } from '../../model/Review';
import { UserService } from '../../service/user.service';
import { Subject, Subscription } from 'rxjs';

@Component({
  selector: 'app-reviews',
  standalone: false,
  templateUrl: './reviews.component.html',
  styleUrl: './reviews.component.css'
})
export class ReviewsComponent implements OnInit {

  route = inject(ActivatedRoute);
  recipeService = inject(RecipeService);
  authService = inject(AuthService);
  fb = inject(FormBuilder);
  reviewService = inject(ReviewService);
  router = inject(Router);
  userService = inject(UserService);

  @Output() onAddReview = new Subject<void>();

  recipeId!: string | null;
  recipeDetail!: RecipeDetail;
  isReviewFormVisible: boolean = false;
  reviewForm!: FormGroup;
  expDetail!: { expAwarded: number, levelUp: boolean, tierUp: string };
  expAlertMessage: string | null = null;

  recipeSub$!: Subscription;
  reviewSub$!: Subscription;


  ngOnInit(): void {
    this.recipeId = this.route.snapshot.paramMap.get('id');
    this.loadRecipeDetails();
    this.reviewForm = this.fb.group({
      rating: this.fb.control<number>(0, [Validators.required, Validators.min(1), Validators.max(5)]),
      comment: this.fb.control<string>('', [Validators.required, Validators.minLength(10)])
    });
  }

  loadRecipeDetails() {
    this.recipeSub$ = this.recipeService.getRecipeById(this.recipeId).subscribe({
      next: (data) => { this.recipeDetail = data.recipe; console.log(data) },
      error: (error) => console.log(error),
      complete: () => this.recipeSub$.unsubscribe()
    })
  }

  toggleReviewForm(): void {
    this.isReviewFormVisible = !this.isReviewFormVisible;
    if (!this.isReviewFormVisible) {
      this.reviewForm.reset();
    }
  }

  submitReview(): void {

    const newReview: Review = {
      ...this.reviewForm.value,
      recipeId: this.recipeId
    }
    console.log(newReview);

    if (this.reviewForm.valid) {
      this.reviewSub$ = this.reviewService.addReview(newReview).subscribe({
        next: (data) => {
          console.log(data);
          this.expDetail = data.expActivity;
          this.handleExpActivityResponse();
          this.userService.fetchUserProfile();
          this.toggleReviewForm();
          this.onAddReview.next();
          this.loadRecipeDetails();
        },
        error: (error) => console.log(error),
        complete: () => this.reviewSub$.unsubscribe()
      });
    }
  }

  handleExpActivityResponse() {
    this.expAlertMessage = `You have earned ${this.expDetail.expAwarded} EXP!`;

    setTimeout(() => {
      this.clearExpAlert();
    }, 5000);
  }

  clearExpAlert() {
    this.expAlertMessage = null;
  }

}
