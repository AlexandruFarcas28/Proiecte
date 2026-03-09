import dotenv from 'dotenv';
import { SuiClient } from '@mysten/sui/client';
import { Ed25519Keypair } from '@mysten/sui/keypairs/ed25519';
import { Transaction } from '@mysten/sui/transactions';
import { decodeSuiPrivateKey } from '@mysten/sui/cryptography';

dotenv.config();

class SuiService {
  constructor() {
    const rpcUrl = process.env.SUI_RPC_URL;
    const privateKeyString = process.env.SUI_PRIVATE_KEY;

    if (!rpcUrl) throw new Error('Missing SUI_RPC_URL in .env');
    if (!privateKeyString) throw new Error('Missing SUI_PRIVATE_KEY in .env');

    this.client = new SuiClient({ url: rpcUrl });

    // ✅ Correct: parse Bech32 "suiprivkey1..." using Mysten helper
    try {
      const { schema, secretKey } = decodeSuiPrivateKey(privateKeyString);

      if (schema !== 'ED25519') {
        throw new Error(`Expected ED25519 key, got ${schema}`);
      }

      // secretKey is Uint8Array(32)
      this.keypair = Ed25519Keypair.fromSecretKey(secretKey);
    } catch (error) {
      console.error('❌ Failed to parse Sui private key:', error?.message || error);
      throw new Error('Invalid SUI_PRIVATE_KEY. Expected "suiprivkey1..." (ED25519) format.');
    }

    this.packageId = process.env.SUI_PACKAGE_ID;
    this.treasuryCapId = process.env.SUI_TREASURY_CAP;

    if (!this.packageId) throw new Error('Missing SUI_PACKAGE_ID in .env');
    if (!this.treasuryCapId) throw new Error('Missing SUI_TREASURY_CAP in .env');

    console.log('✅ Sui service initialized');
    console.log('   RPC:', rpcUrl);
    console.log('   Address:', this.keypair.getPublicKey().toSuiAddress());
    console.log('   Package:', this.packageId);
    console.log('   TreasuryCap:', this.treasuryCapId);
  }

  async mint(recipient, amount) {
    try {
      console.log(`\n🔨 Minting ${amount} IBT to ${recipient} on Sui...`);

      // dacă tokenul tău folosește 9 decimals:
      const amountInMist = Math.floor(Number(amount) * 1_000_000_000);

      const tx = new Transaction();
      tx.moveCall({
        target: `${this.packageId}::ibt_token::mint`,
        arguments: [
          tx.object(this.treasuryCapId),
          tx.pure.u64(amountInMist),
          tx.pure.address(recipient),
        ],
      });

      const result = await this.client.signAndExecuteTransaction({
        signer: this.keypair,
        transaction: tx,
      });

      console.log(`   TX Digest: ${result.digest}`);
      console.log('   ✅ Transaction executed');

      return { success: true, digest: result.digest };
    } catch (error) {
      console.error('❌ Mint error:', error?.message || error);
      throw error;
    }
  }

  async burn(coinId) {
    try {
      console.log(`\n🔥 Burning coin ${coinId} on Sui...`);

      const tx = new Transaction();
      tx.moveCall({
        target: `${this.packageId}::ibt_token::burn`,
        arguments: [
          tx.object(this.treasuryCapId),
          tx.object(coinId),
        ],
      });

      const result = await this.client.signAndExecuteTransaction({
        signer: this.keypair,
        transaction: tx,
      });

      console.log(`   TX Digest: ${result.digest}`);
      console.log('   ✅ Transaction executed');

      return { success: true, digest: result.digest };
    } catch (error) {
      console.error('❌ Burn error:', error?.message || error);
      throw error;
    }
  }
}

export default new SuiService();
