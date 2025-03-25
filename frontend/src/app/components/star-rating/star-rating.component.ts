import { Component, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-star-rating',
  standalone: false,
  templateUrl: './star-rating.component.html',
  styleUrl: './star-rating.component.css'
})
export class StarRatingComponent implements OnInit, OnChanges {

  @Input() averageRating!: number;
  @Input() isInteractive: boolean = false;
  @Output() ratingSelected = new Subject<number>();

  stars: number[] = [0, 0, 0, 0, 0];
  hoverRating: number = 0;
  displayRating: number = 0;

  ngOnInit(): void {
    if (!this.isInteractive) {
      this.calculateStars(this.averageRating);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['averageRating'] && !this.isInteractive) {
      this.calculateStars(this.averageRating);
    }
  }

  private calculateStars(rating: number): void {
    const fullStarsCount = Math.floor(rating);
    const hasHalfStar = rating % 1 >= 0.5;

    this.stars = this.stars.map((_, index) => {
      if (index < fullStarsCount) return 1; // full star
      if (index === fullStarsCount && hasHalfStar) return 0.5; // half star
      return 0; // empty star
    });
  }

  setRating(rating: number): void {
    if (this.isInteractive) {
      this.displayRating = rating;
      this.calculateStars(rating);
      this.ratingSelected.next(rating);
    }
  }

  getStarValue(index: number): number {
    if (this.hoverRating > 0) {
      return index < this.hoverRating ? 1 : 0;
    }
    return this.stars[index];
  }




}
