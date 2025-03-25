import { AfterViewInit, Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CalorieService } from '../../service/calorie.service';
import { CalorieLog } from '../../model/CalorieLog';
import { Chart, registerables } from 'chart.js';
import annotationPlugin from 'chartjs-plugin-annotation';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { AIService } from '../../service/ai.service';
import { Subscription } from 'rxjs';

declare const Datepicker: any;

@Component({
  selector: 'app-calorie-tracker',
  standalone: false,
  templateUrl: './calorie-tracker.component.html',
  styleUrl: './calorie-tracker.component.css',
  animations: [
    trigger('mealAnimation', [
      state('in', style({ opacity: 1, transform: 'translateY(0)' })),
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-20px)' }),
        animate('0.3s ease-in')
      ]),
      transition(':leave', [
        animate('0.3s ease-out', style({ opacity: 0, transform: 'translateY(-20px)' }))
      ])
    ])
  ]
})
export class CalorieTrackerComponent implements OnInit, AfterViewInit {

  fb = inject(FormBuilder);
  calorieService = inject(CalorieService);
  aiService = inject(AIService);

  currentDate: Date = new Date();
  selectedDate = new Date();
  gmtPlus8Offset = 8 * 60 * 60 * 1000;

  dailyCalorieGoal = 2000;
  totalCalories!: number;
  logs!: CalorieLog[];
  filteredLogs!: CalorieLog[];
  mealForm!: FormGroup;
  aiCalories: number | null = null;
  caloriesPercentage!: number;
  dailyCalories: { date: string, totalCalories: number }[] = [];
  chart!: any;
  aiFileName: string = '';
  imagePreview: string | null = null;
  selectedFile: File | null = null;
  isLoading: boolean = false;
  coolDownActive: boolean = false;
  coolDownTime = 0;
  coolDownInterval: any;
  errorMessage = '';
  addCalorie: number = 0;

  showPopup: boolean = false;
  popupMessage: string = '';

  logsSub$!: Subscription;
  guessCaloriesSub$!: Subscription;
  uploadMealSub$!: Subscription;

  ngOnInit(): void {
    window.scrollTo(0, 0);

    this.refreshLogs();

    this.mealForm = this.fb.group({
      meal: this.fb.control<string>('', [Validators.required]),
      name: this.fb.control<string>(''),
      calories: this.fb.control<number>(0, [Validators.required, Validators.min(1)])
    });
  }

  refreshLogs() {
    this.logsSub$ = this.calorieService.getLogs().subscribe({
      next: (data) => {
        this.logs = data.logs;
        this.filterLogsByDate();
        this.applyCalendarStyles(document.getElementById('datepicker-inline')!);
        this.prepareChartData();
        this.renderChart();
      },
      error: (error) => console.log(error),
      complete: () => this.logsSub$.unsubscribe()
    });
  }

  ngAfterViewInit(): void {
    const datepickerEl = document.getElementById('datepicker-inline');

    if (datepickerEl) {
      datepickerEl.setAttribute('data-date', new Date(this.currentDate.getTime() + this.gmtPlus8Offset).toISOString().split('T')[0]);
      console.log(this.currentDate.toISOString());
      const datepicker = new Datepicker(datepickerEl, {
        inline: true,
        format: 'yyyy-mm-dd',
        maxDate: this.currentDate,
      });

      // listen for date selection changes
      datepickerEl.addEventListener('changeDate', (event: Event) => {
        const customEvent = event as CustomEvent;
        this.selectedDate = new Date(customEvent.detail.date.getTime());
        this.filterLogsByDate();
        this.prepareChartData();
        this.renderChart();
      });

      // listen for month change events
      datepickerEl.addEventListener('changeMonth', () => {
        setTimeout(() => {
          this.applyCalendarStyles(datepickerEl);
        }, 2000);
      });

      // Apply styles to the calendar after it's rendered
      setTimeout(() => {
        this.applyCalendarStyles(datepickerEl);
      }, 2000);
    }
  }

  applyCalendarStyles(datepickerEl: HTMLElement): void {
    const dayElements = datepickerEl.querySelectorAll('.datepicker-cell.day');

    dayElements.forEach((dayElement) => {
      dayElement.classList.remove('red-day', 'green-day');
      const dataDate = dayElement.getAttribute('data-date');

      if (dataDate) {
        const timestamp = parseInt(dataDate, 10);

        if (!isNaN(timestamp)) {
          const dateInGMT8 = new Date(timestamp + this.gmtPlus8Offset).toISOString().split('T')[0];

          const totalCaloriesForDay = this.logs
            .filter((log) => new Date(log.date + this.gmtPlus8Offset).toISOString().split('T')[0] === dateInGMT8)
            .reduce((sum, log) => sum + log.calories, 0);

          // apply styles only if there are logs for this day
          if (totalCaloriesForDay > 0) {
            if (totalCaloriesForDay > this.dailyCalorieGoal) {
              dayElement.classList.add('red-day'); // style overshot days in red
            } else {
              dayElement.classList.add('green-day'); // style within-limit days in green
            }
          }
        }
      }
    });
  }

  filterLogsByDate(): void {
    this.filteredLogs = this.logs.filter((log) => {
      const logDate = new Date(log.date).toDateString();
      const selectedDate = this.selectedDate.toDateString();
      return logDate === selectedDate;
    });

    this.totalCalories = this.filteredLogs.reduce((sum, log) => sum + log.calories, 0);
    this.caloriesPercentage = (this.totalCalories) / this.dailyCalorieGoal * 100;
  }

  deleteLog(log: CalorieLog): void {
    this.calorieService.deleteLog(log._id.$oid).subscribe({
      next: (data) => {

        console.log(data);
        this.refreshLogs();

      },
      error: (error) => {
        console.error('Error deleting log:', error);
      },
    });
  }

  onAIImageChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.aiFileName = this.selectedFile.name;
      this.generateImagePreview(this.selectedFile);
    }
  }

  generateImagePreview(file: File): void {
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  clearFile(): void {
    this.selectedFile = null;
    this.aiFileName = '';
    this.imagePreview = null;
    this.resetFileInput();
  }

  resetFileInput(): void {
    const fileInput = document.getElementById('ai-image-upload') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  guessCalories() {
    this.isLoading = true;
    this.coolDownTime = 60;
    this.errorMessage = '';

    if (this.selectedFile) {
      const formData = new FormData();
      formData.append('file', this.selectedFile);
      this.guessCaloriesSub$ = this.aiService.guessCalories(formData)
        .subscribe({
          next: (response: any) => {
            console.log(response);
            this.popupMessage = response.response;
            this.showPopup = true;

            const regex = /(?:total\s*number\s*of\s*calories|total\s*calories|calories\s*in\s*the\s*dish|calories\s*from)\D*(\d+)/i;
            const match = this.popupMessage.match(regex);

            if (match && match[1]) {
              this.addCalorie = parseInt(match[1], 10);
            }
          },
          error: (error) => {
            console.error('Error querying AI', error);
            this.coolDownActive = true;
            this.isLoading = false;

            this.coolDownInterval = setInterval(() => {
              if (this.coolDownTime > 0) {
                this.coolDownTime--;
              } else {
                this.coolDownActive = false;
                clearInterval(this.coolDownInterval);
              }
            }, 1000);

            this.errorMessage = error.error?.message || 'An unknown error occurred.';

          },
          complete: () => {
            this.coolDownActive = true;
            this.isLoading = false;

            this.coolDownInterval = setInterval(() => {
              if (this.coolDownTime > 0) {
                this.coolDownTime--;
              } else {
                this.coolDownActive = false;
                clearInterval(this.coolDownInterval);
              }
            }, 1000);
            this.guessCaloriesSub$.unsubscribe();
          }
        });
    }
  }

  onPopupClose() {
    this.showPopup = false;
  }

  addCalorieToForm() {
    this.showPopup = false;
    this.mealForm.get("calories")?.setValue(this.addCalorie);
    this.addCalorie = 0;
  }

  onCalorieGoalChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.dailyCalorieGoal = parseInt(target.value, 10);

    this.caloriesPercentage = (this.totalCalories / this.dailyCalorieGoal) * 100;

    this.applyCalendarStyles(document.getElementById('datepicker-inline')!);

    this.renderChart();
  }

  prepareChartData(): void {
    const calorieMap = new Map<string, number>();

    this.logs.forEach((log) => {
      const logDateGMT8 = new Date(log.date + this.gmtPlus8Offset).toISOString().split('T')[0];
      if (calorieMap.has(logDateGMT8)) {
        calorieMap.set(logDateGMT8, calorieMap.get(logDateGMT8)! + log.calories);
      } else {
        calorieMap.set(logDateGMT8, log.calories);
      }
    });

    // graph shows 15 days before and after the selected date
    const startDate = new Date(this.selectedDate);
    startDate.setDate(startDate.getDate() - 15);

    const endDate = new Date(this.selectedDate);
    endDate.setDate(endDate.getDate() + 15);

    const dateRange = this.getDateRange(startDate, endDate);

    // fill in missing dates with 0 calories
    this.dailyCalories = dateRange.map((date) => {
      const dateString = date.toISOString().split('T')[0];
      return {
        date: dateString,
        totalCalories: calorieMap.get(dateString) || 0
      };
    });

    this.dailyCalories.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
  }

  getDateRange(startDate: Date, endDate: Date): Date[] {
    const dates: Date[] = [];
    let currentDate = new Date(startDate);

    while (currentDate <= endDate) {
      dates.push(new Date(currentDate));
      currentDate.setDate(currentDate.getDate() + 1);
    }

    return dates;
  }

  renderChart(): void {
    Chart.register(...registerables, annotationPlugin);

    const ctx = document.getElementById('calorieChart') as HTMLCanvasElement;

    if (this.chart) {
      this.chart.destroy();
    }

    const barColors = this.dailyCalories.map((entry) =>
      entry.totalCalories > this.dailyCalorieGoal ? 'rgba(255, 99, 132, 0.6)' : 'rgba(31, 190, 31, 0.6)'
    );

    this.chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: this.dailyCalories.map((entry) => entry.date),
        datasets: [{
          label: 'Total Daily Calorie Intake',
          data: this.dailyCalories.map((entry) => entry.totalCalories),
          backgroundColor: barColors,
          borderColor: barColors.map(color => color.replace('0.6', '1')),
          borderWidth: 1
        }]
      },
      options: {
        scales: {
          y: {
            beginAtZero: true,
            title: {
              display: true,
              text: 'Calories'
            }
          },
          x: {
            title: {
              display: true,
              text: 'Date'
            }
          }
        },
        plugins: {
          title: {
            display: true,
            text: 'Daily Calorie Intake'
          },
          annotation: {
            annotations: {
              line1: {
                type: 'line',
                yMin: this.dailyCalorieGoal,
                yMax: this.dailyCalorieGoal,
                borderColor: 'rgba(255, 99, 132, 1)',
                borderWidth: 2,
                borderDash: [5, 5],
                label: {
                  display: true,
                  content: 'Daily Calorie Goal',
                  position: 'end',
                  backgroundColor: 'rgba(255, 99, 132, 0.8)',
                  color: '#fff',
                  font: {
                    size: 12
                  }
                }
              }
            }
          },
          legend: {
            display: true,
            labels: {
              generateLabels: (chart) => {
                const datasets = chart.data.datasets;
                return [
                  {
                    text: 'Below Daily Goal (Green)',
                    fillStyle: 'rgba(31, 190, 31, 0.6)',
                    strokeStyle: 'rgba(31, 190, 31, 1)',
                    hidden: false
                  },
                  {
                    text: 'Over Daily Goal (Red)',
                    fillStyle: 'rgba(255, 99, 132, 0.6)',
                    strokeStyle: 'rgba(255, 99, 132, 1)',
                    hidden: false
                  }
                ];
              }
            }
          }
        }
      }
    });
  }

  onMealUpload() {
    console.log(this.mealForm.value);
    if (this.mealForm.valid) {

      const formWithTime = {
        ...this.mealForm.value,
        date: this.selectedDate.getTime()
      }
      this.uploadMealSub$ = this.calorieService.uploadMeal(formWithTime).subscribe({
        next: (data) => {
          console.log(data);
          this.mealForm.reset();

          this.refreshLogs();

        },
        error: (error) => console.log(error),
        complete: () => this.uploadMealSub$.unsubscribe()
      });
    }
  }

}
