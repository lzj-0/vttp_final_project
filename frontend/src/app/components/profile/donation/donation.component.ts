import { Component, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { User } from '../../../model/User';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../service/auth.service';
import { DonationService } from '../../../service/donation.service';
import { interval, startWith, Subject, Subscription, switchMap } from 'rxjs';
import { ethers } from 'ethers';
import { UserService } from '../../../service/user.service';
import { AuthStore } from '../../../store/AuthStore';

@Component({
  selector: 'app-donation',
  standalone: false,
  templateUrl: './donation.component.html',
  styleUrl: './donation.component.css'
})
export class DonationComponent implements OnInit, OnDestroy {

  @Input() userDetail!: User;
  @Input() isLoggedInUser!: boolean;
  @Output() walletVerified = new Subject<void>();

  route = inject(ActivatedRoute);
  authService = inject(AuthService);
  donationService = inject(DonationService);
  fb = inject(FormBuilder);
  userService = inject(UserService);
  authStore = inject(AuthStore);

  donationForm!: FormGroup;
  fixedDonationAmounts = [0.01, 0.05, 0.1, 0.5];
  userBalance: number = 0;
  withdrawMessage: string = "";
  withdrawError: string = "";
  donateMessage: string = "";
  donateError: string = "";
  verifyError: string = "";
  connectedWalletAddress: string | null = null;
  creditDonationForm!: FormGroup;
  creditDonationMessage: string = "";
  creditDonationError: string = "";
  currentUser!: User | null;
  ethPrice!: number;

  ethPriceSubscription!: Subscription;
  userSub$!: Subscription;
  walletSub$!: Subscription;
  donationSub$!: Subscription;

  ngOnInit(): void {
    this.donationForm = this.fb.group({
      customDonationAmount: ['', [Validators.required, Validators.min(0)]]
    });

    this.creditDonationForm = this.fb.group({
      creditAmount: ['', [Validators.required, Validators.min(1)]]
    });
    this.userService.fetchUserProfile();
    this.userSub$ = this.authStore.user$.subscribe((data) => this.currentUser = data);

    this.ethPriceSubscription = interval(10 * 60 * 1000)
      .pipe(
        startWith(0),
        switchMap(() => this.donationService.getPrice("ethereum", "usd"))
      )
      .subscribe({
        next: (data: any) => {
          console.log(data);
          this.ethPrice = data.ethereum.usd;
        },
        error: (error) => {
          console.error('Failed to fetch ETH price:', error);
        },
      });
  }

  async connectWallet(): Promise<void> {
    try {
      await this.donationService.connectMetaMask();
      const signer = this.donationService.getSigner();
      const walletAddress = await signer?.getAddress();

      if (walletAddress) {
        this.connectedWalletAddress = walletAddress;
        console.log("Wallet connected:", walletAddress);
        this.fetchUserBalance();
      } else {
        throw new Error("Failed to retrieve wallet address.");
      }
    } catch (error: any) {
      console.error("Failed to connect wallet:", error);
    }
  }

  disconnectWallet(): void {
    this.connectedWalletAddress = null;
    this.userBalance = 0;
    console.log("Wallet disconnected.");
  }

  async fetchUserBalance(): Promise<void> {
    if (!this.connectedWalletAddress) {
      return;
    }

    try {
      await this.donationService.connectMetaMask();
      const signer = this.donationService.getSigner();
      const connectedWallet = await signer?.getAddress();
      console.log("Connected MetaMask Wallet:", connectedWallet);
      console.log("User Detail Wallet Address:", this.userDetail.walletAddress);

      if (connectedWallet) {
        if (connectedWallet.toLowerCase() === this.userDetail.walletAddress.toLowerCase()) {
          this.userBalance = await this.donationService.checkBalance(this.userDetail.walletAddress);
        } else {
          console.log("Signed wallet don't match verified wallet");
          return;
        }
      } else {
        alert("Wallet not connected");
        return;
      }
    } catch (error) {
      console.error('Failed to fetch user balance:', error);
    }
  }

  async donate(amount: number): Promise<void> {
    try {
      this.donateMessage = "";
      this.donateError = "";

      if (!this.connectedWalletAddress) {
        this.donateError = "Please connect your wallet first.";
        return;
      }

      if (this.userDetail.walletAddress === "" || !ethers.isAddress(this.userDetail.walletAddress)) {
        this.donateError = 'User has not verified wallet.';
        return;
      }

      if (isNaN(amount) || amount <= 0) {
        this.donateError = 'Invalid donation amount.';
        return;
      }

      await this.donationService.connectMetaMask();
      await this.donationService.donate(this.userDetail.walletAddress, amount.toString());
      this.donateMessage = 'Donation successful!';
    } catch (error: any) {
      console.log(error);
      if (error.code === "INSUFFICIENT_FUNDS") {
        this.donateError = "Insufficient funds in wallet";
      } else {
        this.donateError = 'An unknown error occurred.';
      }
    }
  }

  async verifyWallet(): Promise<void> {
    if (!this.connectedWalletAddress) {
      this.verifyError = "Please connect your wallet first.";
      return;
    }

    try {
      await this.donationService.connectMetaMask();
      await this.donationService.verifyWallet();

      const signer = this.donationService.getSigner();
      const walletAddress = await signer?.getAddress();

      if (!walletAddress) {
        this.verifyError = 'Failed to retrieve wallet address.';
        return;
      }

      this.walletSub$ = this.authService.updateWalletAddress(walletAddress).subscribe({
        next: (data) => {
          console.log(data);
          alert('Wallet verified!');
          this.walletVerified.next();
        },
        error: (error) => {
          console.error('Failed to update wallet address:', error);
          this.verifyError = 'Failed to set up wallet address.';
        },
        complete: () => this.walletSub$.unsubscribe()
      });
    } catch (error: any) {
      console.error('Error during wallet verification:', error);
      if (error.reason) {
        this.verifyError = error.reason + " in another account. Please switch to another wallet";
      } else {
        this.verifyError = 'An unknown error occurred.';
      }
    }
  }

  async withdraw(): Promise<void> {
    try {
      this.withdrawError = "";
      this.withdrawMessage = "";

      if (!this.connectedWalletAddress) {
        this.withdrawError = "Please connect your wallet first.";
        return;
      }

      await this.donationService.connectMetaMask();

      const signer = this.donationService.getSigner();
      const connectedWallet = await signer?.getAddress();

      if (connectedWallet) {
        if (connectedWallet.toLowerCase() !== this.userDetail.walletAddress.toLowerCase()) {
          this.withdrawError = "Current wallet does not have withdrawal rights for this account. Only the stated verified wallet can withdraw.";
          return;
        }
        await this.donationService.withdraw();
        this.withdrawMessage = 'Withdrawal successful!';
        this.fetchUserBalance();
        return;
      }

      this.withdrawError = "Wallet is not connected";

    } catch (error: any) {
      console.error('Error during withdrawal:', error);

      if (error.reason) {
        this.withdrawError = error.reason;
      } else {
        this.withdrawError = "An unknown error occurred.";
      }
    }
  }

  onSubmit(): void {
    if (this.donationForm.valid) {
      const amount = this.donationForm.value.customDonationAmount;
      this.donate(amount);
    }
  }

  onCreditDonationSubmit(): void {
    if (this.creditDonationForm.valid) {
      const amount = this.creditDonationForm.value.creditAmount;
      this.donateCredits(amount);
    }
  }

  donateCredits(amount: number): void {
    this.creditDonationMessage = "";
    this.creditDonationError = "";

    if (this.isLoggedInUser) {
      this.creditDonationError = "You cannot donate credits to yourself.";
      return;
    }

    if (this.currentUser) {
      if (this.currentUser.credits < amount) {
        this.creditDonationError = "You do not have sufficient credits to donate";
        return;
      }
    }

    this.donationSub$ = this.donationService.donateCredits(this.userDetail.id, amount).subscribe({
      next: (data) => {
        this.creditDonationMessage = data.message;
        this.creditDonationForm.reset();
        this.userService.fetchUserProfile();
      },
      error: (error) => {
        this.creditDonationError = error.message || "Failed to donate credits.";
      },
      complete: () => this.donationSub$.unsubscribe()
    });
  }

  ngOnDestroy(): void {
    if (this.ethPriceSubscription) {
      this.ethPriceSubscription.unsubscribe();
    }
    this.userSub$.unsubscribe();
  }

}
