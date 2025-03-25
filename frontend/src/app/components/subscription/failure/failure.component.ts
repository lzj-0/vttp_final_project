import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-failure',
  standalone: false,
  templateUrl: './failure.component.html',
  styleUrl: './failure.component.css'
})
export class FailureComponent implements OnInit {

  router = inject(Router);

  seconds: number = 5;
  private countdownInterval: any;

  ngOnInit(): void {
    this.countdownInterval = setInterval(() => {
      this.seconds--;
      if (this.seconds === 0) {
        clearInterval(this.countdownInterval);
        this.router.navigate(['/subscription']);
      }
    }, 1000);
  }

  redirectNow() {
    clearInterval(this.countdownInterval);
    this.router.navigate(['/subscription']);
  }

}
