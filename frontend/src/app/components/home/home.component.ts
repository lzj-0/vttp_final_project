import { Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  @ViewChild('videoContainer', { static: true }) videoContainer!: ElementRef<HTMLDivElement>;
  @ViewChild('videoText', { static: true }) videoText!: ElementRef<HTMLDivElement>;
  @ViewChild('textSections', { static: true }) textSections!: ElementRef<HTMLDivElement>;
  @ViewChild('scrollIndicator', { static: true }) scrollIndicator!: ElementRef<HTMLDivElement>;

  ngOnInit(): void {
    window.scrollTo(0, 0);
    gsap.registerPlugin(ScrollTrigger);
    this.setupAnimations();
  }

  @HostListener('window:scroll', [])
  onScroll(): void {
    const scrollY = window.scrollY;
    const screenWidth = window.innerWidth;
  
    const movementAmount = screenWidth < 768 ? '50%' : '20%';
  
    // move video to right when scrollY > 500
    if (scrollY > 500) {
      gsap.to(this.videoContainer.nativeElement, {
        x: movementAmount,
        borderRadius: '20px',
        boxShadow: '0px 10px 30px rgba(0, 0, 0, 0.3)',
        duration: 0.5,
        ease: 'power2.out',
      });
  
      // Fade out video text
      gsap.to(this.videoText.nativeElement, {
        opacity: 0,
        duration: 0.5,
        ease: 'power2.out',
      });
  
      gsap.to(this.scrollIndicator.nativeElement, {
        opacity: 0,
        duration: 0.5,
        ease: 'power2.out',
      });
  
    } else {
      // reset video to original position
      gsap.to(this.videoContainer.nativeElement, {
        x: '0%',
        borderRadius: '0',
        boxShadow: 'none',
        duration: 0.5,
        ease: 'power2.out',
      });
  
      // Fade in video text
      gsap.to(this.videoText.nativeElement, {
        opacity: 1,
        duration: 0.5,
        ease: 'power2.out',
      });
  
      gsap.to(this.scrollIndicator.nativeElement, {
        opacity: 1,
        duration: 0.5,
        ease: 'power2.out',
      });
    }
  }

  private setupAnimations(): void {
    gsap.set(this.videoText.nativeElement, {
      opacity: 1,
    });

    gsap.set(this.scrollIndicator.nativeElement, {
      opacity: 1,
    });
  }
}
