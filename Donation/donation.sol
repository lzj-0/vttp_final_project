// SPDX-License-Identifier: MIT
pragma solidity ^0.8.26;

contract ProfileDonation {
    mapping(address => bool) public isVerified;
    mapping(address => uint256) public donations;

    event WalletVerified(address indexed user);
    event DonationReceived(address indexed donor, address indexed recipient, uint256 amount);
    event Withdrawal(address indexed user, uint256 amount);

    function checkVerification(address user) external view returns (bool) {
        return isVerified[user];
    }

    function checkBalance(address user) external view returns (uint256) {
        return donations[user];
    }

    function verifyWallet() external {
        require(!isVerified[msg.sender], "Wallet already verified");
        isVerified[msg.sender] = true;
        emit WalletVerified(msg.sender);
    }

    function donate(address recipient) external payable {
        require(isVerified[recipient], "Recipient must be a verified user");
        require(msg.value > 0, "Donation amount must be greater than zero");

        donations[recipient] += msg.value;
        emit DonationReceived(msg.sender, recipient, msg.value);
    }

    function withdraw() external {
        require(isVerified[msg.sender], "Wallet has not been verified");
        uint256 amount = donations[msg.sender];
        require(amount > 0, "No funds to withdraw");

        donations[msg.sender] = 0;
        payable(msg.sender).transfer(amount);
        emit Withdrawal(msg.sender, amount);
    }

    function contractBalance() external view returns (uint256) {
        return address(this).balance;
    }

}