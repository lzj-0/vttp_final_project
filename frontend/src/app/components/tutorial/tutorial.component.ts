import { Component, inject, OnInit } from '@angular/core';
import { RewardsService } from '../../service/rewards.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-tutorial',
  standalone: false,
  templateUrl: './tutorial.component.html',
  styleUrl: './tutorial.component.css'
})
export class TutorialComponent implements OnInit {

  rewardsService = inject(RewardsService);

  nonGatekeepData: any[] = [];
  gatekeepData: any[] = [];

  actionDescriptions: { [key: string]: string } = {
    upload: 'Upload a Recipe',
    try: 'Try Cooking with a Recipe',
    review: 'Review a Recipe',
    views100: 'Gain 100 Views on Your Recipe',
    leaderboard: 'Be in the Top 10 of the Weekly Leaderboard',
    gettry: 'Someone Tries Cooking with Your Recipe',
    getreview: 'Someone Reviews Your Recipe',
    getviews100: 'Your Recipe Reaches 100 Views',
  };

  sub$!: Subscription;

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.sub$ = this.rewardsService.getActionData().subscribe({
      next: (data) => {
        const tierOrder = ['Bronze', 'Silver', 'Gold', 'Diamond'];
        console.log(data);
        this.nonGatekeepData = data.nonGatekeepData.filter((reward: any) => (
          reward.tier === 'Bronze'
        )).map((reward: any) => ({
          ...reward, act: this.actionDescriptions[reward.act]
        })).sort((a, b) => a.exp - b.exp);
        this.gatekeepData = data.gatekeepData.map((reward: any) => ({
          ...reward, act: this.actionDescriptions[reward.act]
        })).sort((a, b) => tierOrder.indexOf(a.tier) - tierOrder.indexOf(b.tier));
      },
      error: (error) => console.log(error),
      complete: () => this.sub$.unsubscribe()
    })
  }
}
