import { ethers } from 'ethers';
import dotenv from 'dotenv';

dotenv.config();

const IBT_ABI = [
  "function mint(address to, uint256 amount) external",
  "function burn(address from, uint256 amount) external",
  "function balanceOf(address account) external view returns (uint256)",
  "function decimals() external view returns (uint8)",
  "function totalSupply() external view returns (uint256)"
];

class EthereumService {
  constructor() {
    this.provider = new ethers.JsonRpcProvider(process.env.ETH_RPC_URL);
    this.wallet = new ethers.Wallet(process.env.ETH_PRIVATE_KEY, this.provider);
    this.contract = new ethers.Contract(
      process.env.ETH_CONTRACT_ADDRESS,
      IBT_ABI,
      this.wallet
    );
    console.log('✅ Ethereum service initialized (Anvil Local)');
    console.log('   Address:', this.wallet.address);
  }

  // Validate și normalizează adresa Ethereum
  validateAddress(address) {
    try {
      // Verifică dacă e adresă validă
      if (!address || !address.startsWith('0x')) {
        throw new Error('Invalid Ethereum address format');
      }
      
      // Normalizează adresa (checksum)
      return ethers.getAddress(address);
    } catch (error) {
      console.error('❌ Invalid address:', address);
      throw new Error(`Invalid Ethereum address: ${address}`);
    }
  }

  async mint(toAddress, amount) {
    try {
      // Validează și normalizează adresa
      const validAddress = this.validateAddress(toAddress);
      
      console.log(`\n🔨 Minting ${amount} IBT to ${validAddress} on Ethereum...`);
      
      const amountInWei = ethers.parseEther(amount.toString());
      const tx = await this.contract.mint(validAddress, amountInWei);
      console.log(`   TX Hash: ${tx.hash}`);
      
      const receipt = await tx.wait();
      console.log(`   ✅ Confirmed in block ${receipt.blockNumber}`);
      
      return {
        success: true,
        txHash: tx.hash,
        blockNumber: receipt.blockNumber
      };
    } catch (error) {
      console.error('❌ Mint error:', error.message);
      throw error;
    }
  }

  async burn(fromAddress, amount) {
    try {
      // Validează și normalizează adresa
      const validAddress = this.validateAddress(fromAddress);
      
      console.log(`\n🔥 Burning ${amount} IBT from ${validAddress} on Ethereum...`);
      
      const amountInWei = ethers.parseEther(amount.toString());
      const tx = await this.contract.burn(validAddress, amountInWei);
      console.log(`   TX Hash: ${tx.hash}`);
      
      const receipt = await tx.wait();
      console.log(`   ✅ Confirmed in block ${receipt.blockNumber}`);
      
      return {
        success: true,
        txHash: tx.hash,
        blockNumber: receipt.blockNumber
      };
    } catch (error) {
      console.error('❌ Burn error:', error.message);
      throw error;
    }
  }

  async getBalance(address) {
    try {
      const validAddress = this.validateAddress(address);
      const balance = await this.contract.balanceOf(validAddress);
      return ethers.formatEther(balance);
    } catch (error) {
      console.error('❌ Balance error:', error.message);
      return '0';
    }
  }
}

export default new EthereumService();
