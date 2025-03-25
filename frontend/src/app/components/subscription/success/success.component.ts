import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-success',
  standalone: false,
  templateUrl: './success.component.html',
  styleUrl: './success.component.css'
})
export class SuccessComponent implements OnInit {

  router = inject(Router);

  seconds : number = 5;
  private countdownInterval! : any;

  ngOnInit(): void {
    this.countdownInterval = setInterval(() => {
      this.seconds--;
      if (this.seconds === 0) {
        clearInterval(this.countdownInterval);
        this.router.navigate(['/main']);
      }
    }, 1000);
  }

  redirectNow() {
    clearInterval(this.countdownInterval);
    this.router.navigate(['/main']);
  }

}
