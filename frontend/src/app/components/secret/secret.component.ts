import { Component, inject } from '@angular/core';
import { ReviewService } from '../../service/review.service';
import { Review } from '../../model/Review';
import { UserService } from '../../service/user.service';

@Component({
  selector: 'app-secret',
  standalone: false,
  templateUrl: './secret.component.html',
  styleUrl: './secret.component.css'
})
export class SecretComponent {

  reviewService = inject(ReviewService);
  userService = inject(UserService);

  addExp() {
    const review : Review = {
      rating: -1,
      comment: "",
      recipeId: "",
      userId: '',
      userName: '',
      createdAt: 0
    }
    this.reviewService.addReview(review).subscribe({
      next: (data) => this.userService.fetchUserProfile(),
      error: (error) => console.log(error)
    })
  }
}
