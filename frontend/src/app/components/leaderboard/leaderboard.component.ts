import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { LeaderboardService } from '../../service/leaderboard.service';
import { User } from '../../model/User';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-leaderboard',
  standalone: false,
  templateUrl: './leaderboard.component.html',
  styleUrl: './leaderboard.component.css'
})
export class LeaderboardComponent implements OnInit, OnDestroy {

  leaderboardService = inject(LeaderboardService);

  countdown: string = '';
  countdownSub$!: Subscription;
  topUsersSub$!: Subscription;
  leaderboard!: { user: User, expEarned: number }[];

  ngOnInit(): void {
    this.topUsersSub$ = this.leaderboardService.getTopUsers().subscribe({
      next: (data) => this.leaderboard = data.leaderboard,
      error: (error) => console.log(error),
      complete: () => this.topUsersSub$.unsubscribe()
    });

    this.startCountdown();
  }

  private startCountdown(): void {
    const calculateNextSunday = () => {
      const now = new Date();
      const nextSunday = new Date(now);
      const daysUntilSunday = (7 - now.getDay()) % 7;
      nextSunday.setDate(now.getDate() + (daysUntilSunday === 0 ? 7 : daysUntilSunday));
      nextSunday.setHours(0, 0, 0, 0);
      return nextSunday;
    };

    let nextSunday = calculateNextSunday();

    this.countdownSub$ = interval(1000).subscribe(() => {
      const now = new Date();
      const diff = nextSunday.getTime() - now.getTime();

      if (diff > 0) {
        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((diff % (1000 * 60)) / 1000);

        this.countdown = `${days}d ${hours}h ${minutes}m ${seconds}s`;
      } else {
        nextSunday = calculateNextSunday();
      }
    });
  }

  ngOnDestroy(): void {
    this.countdownSub$.unsubscribe();
  }

}
