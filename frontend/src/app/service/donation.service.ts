import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ethers } from 'ethers';
import { AuthService } from './auth.service';
import { Response } from '../model/Response';

@Injectable({
  providedIn: 'root'
})
export class DonationService {

  private provider: ethers.BrowserProvider | null = null;
  private signer: ethers.Signer | null = null;
  private contract: ethers.Contract | null = null;
  private contractAddress = '0x8231404a1f1e7E6c8C6886955cEd5BBb73b90682';
  private contractABI = [{"anonymous":false,"inputs":[{"indexed":true,"internalType":"address","name":"donor","type":"address"},{"indexed":true,"internalType":"address","name":"recipient","type":"address"},{"indexed":false,"internalType":"uint256","name":"amount","type":"uint256"}],"name":"DonationReceived","type":"event"},{"anonymous":false,"inputs":[{"indexed":true,"internalType":"address","name":"user","type":"address"}],"name":"WalletVerified","type":"event"},{"anonymous":false,"inputs":[{"indexed":true,"internalType":"address","name":"user","type":"address"},{"indexed":false,"internalType":"uint256","name":"amount","type":"uint256"}],"name":"Withdrawal","type":"event"},{"inputs":[{"internalType":"address","name":"user","type":"address"}],"name":"checkBalance","outputs":[{"internalType":"uint256","name":"","type":"uint256"}],"stateMutability":"view","type":"function"},{"inputs":[{"internalType":"address","name":"user","type":"address"}],"name":"checkVerification","outputs":[{"internalType":"bool","name":"","type":"bool"}],"stateMutability":"view","type":"function"},{"inputs":[],"name":"contractBalance","outputs":[{"internalType":"uint256","name":"","type":"uint256"}],"stateMutability":"view","type":"function"},{"inputs":[{"internalType":"address","name":"recipient","type":"address"}],"name":"donate","outputs":[],"stateMutability":"payable","type":"function"},{"inputs":[{"internalType":"address","name":"","type":"address"}],"name":"donations","outputs":[{"internalType":"uint256","name":"","type":"uint256"}],"stateMutability":"view","type":"function"},{"inputs":[{"internalType":"address","name":"","type":"address"}],"name":"isVerified","outputs":[{"internalType":"bool","name":"","type":"bool"}],"stateMutability":"view","type":"function"},{"inputs":[],"name":"verifyWallet","outputs":[],"stateMutability":"nonpayable","type":"function"},{"inputs":[],"name":"withdraw","outputs":[],"stateMutability":"nonpayable","type":"function"}];

  http = inject(HttpClient);
  authService = inject(AuthService);

  constructor() {}

  async connectMetaMask(): Promise<void> {
    if ((window as any).ethereum) {
      await (window as any).ethereum.request({ method: 'eth_requestAccounts' });
      this.provider = new ethers.BrowserProvider((window as any).ethereum);
      this.signer = await this.provider.getSigner();
  
      if (!this.signer) {
        console.error("Signer not initialized!");
        return;
      }
  
      console.log("Signer Address:", await this.signer.getAddress());
  
      this.contract = new ethers.Contract(this.contractAddress, this.contractABI, this.signer);
      console.log("Contract initialized with signer:", this.contract);
    } else {
      alert('Please install MetaMask to use this feature.');
    }
  }
  
  getSigner(): ethers.Signer | null {
    return this.signer;
  }

  async verifyWallet(): Promise<void> {
    if (!this.contract) throw new Error('Contract not initialized');
    await this.contract['verifyWallet']();
  }

  async donate(recipient: string, amount: string): Promise<void> {
    if (!this.contract) throw new Error('Contract not initialized');
    if (!ethers.isAddress(recipient)) throw new Error('Invalid recipient address');
    if (isNaN(parseFloat(amount))) throw new Error('Invalid donation amount');
  
    console.log('Recipient:', recipient);
    console.log('Amount:', amount);

    const isVerified = await this.isWalletVerified(recipient);
    if (!isVerified) {
      throw new Error('Recipient wallet is not verified.');
    }
  
    const tx = await this.contract['donate'](recipient, { value: ethers.parseEther(amount) });
    await tx.wait();
  }

  async withdraw(): Promise<void> {
    if (!this.contract) throw new Error('Contract not initialized');
    const tx = await this.contract['withdraw']();
    await tx.wait();
  }

  async isWalletVerified(userAddress: string): Promise<boolean> {
    if (!this.contract) throw new Error('Contract not initialized');
    return await this.contract['checkVerification'](userAddress);
  }

  async checkBalance(userAddress: string): Promise<number> {
    if (!this.contract) throw new Error('Contract not initialized');
    const balance = await this.contract['checkBalance'](userAddress);
    return parseFloat(ethers.formatEther(balance));
  }

  donateCredits(userId : string, amount : number) {
    const url = "/api/user/donatecredit";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.put<Response>(url, {userId : userId, amount : amount}, {headers : headers});
  }

  getPrice(coin : string, currency : string) {
    const url = "/api/crypto/price";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    const params = new HttpParams().set("coin", coin)
                                    .set("currency", currency);

    return this.http.get(url, {params : params, headers: headers});
  }

}

