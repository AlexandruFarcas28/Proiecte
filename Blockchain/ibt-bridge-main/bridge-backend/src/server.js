import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import ethService from './ethereum.service.js';
import suiService from './sui.service.js';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3001;

app.use(cors());
app.use(express.json());

// Helper functions pentru validare
function isValidEthAddress(address) {
  return /^0x[a-fA-F0-9]{40}$/.test(address);
}

function isValidSuiAddress(address) {
  return /^0x[a-fA-F0-9]{64}$/.test(address);
}

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ 
    status: 'ok', 
    timestamp: new Date().toISOString(),
    network: 'localnet'
  });
});

// Bridge endpoint
app.post('/bridge', async (req, res) => {
  try {
    const { sourceChain, destinationChain, amount, userAddress, ethAddress, suiAddress } = req.body;
    
    console.log('\n🌉 ================================');
    console.log('   Bridge Request');
    console.log('   ================================');
    console.log(`   ${sourceChain} → ${destinationChain}`);
    console.log(`   Amount: ${amount} IBT`);
    console.log(`   User: ${userAddress}`);
    console.log('   ================================');
    
    let burnResult, mintResult;
    
    if (sourceChain === 'ethereum' && destinationChain === 'sui') {
      // ETH → SUI
      if (!isValidEthAddress(userAddress)) {
        return res.status(400).json({ error: 'Invalid Ethereum address' });
      }
      
      // Burn pe Ethereum
      burnResult = await ethService.burn(userAddress, amount);
      
      const suiRecipient = suiAddress || process.env.SUI_DEPLOYER_ADDRESS || '0x6a2d504b05a202c749cd7ccb06fa92ced0becf55b3b7b909d8257f9566a604bc';
      mintResult = await suiService.mint(suiRecipient, amount);
      
    } else if (sourceChain === 'sui' && destinationChain === 'ethereum') {
      // SUI → ETH
      
      // Folosim adresa Ethereum hardcodată sau din request
      const ethRecipient = ethAddress || '0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266';
      
      if (!isValidEthAddress(ethRecipient)) {
        return res.status(400).json({ error: 'Invalid Ethereum destination address' });
      }
      
      // Mint pe Ethereum
      mintResult = await ethService.mint(ethRecipient, amount);
      
      
      burnResult = { success: true, note: 'SUI burn skipped for demo (requires coin object)' };
      
    } else {
      return res.status(400).json({ error: 'Invalid chain combination' });
    }
    
    console.log('\n✅ Bridge completed successfully!\n');
    
    res.json({
      success: true,
      burn: burnResult,
      mint: mintResult
    });
    
  } catch (error) {
    console.error('\n❌ Bridge error:', error.message);
    res.status(500).json({ error: error.message });
  }
});

// Get balance endpoint
app.get('/balance/:chain/:address', async (req, res) => {
  try {
    const { chain, address } = req.params;
    
    let balance;
    if (chain === 'ethereum') {
      if (!isValidEthAddress(address)) {
        return res.status(400).json({ error: 'Invalid Ethereum address' });
      }
      balance = await ethService.getBalance(address);
    } else if (chain === 'sui') {
      // Sui balance check would require fetching coin objects
      // For demo, return placeholder
      balance = '0';
    } else {
      return res.status(400).json({ error: 'Invalid chain' });
    }
    
    res.json({ balance, chain, address });
  } catch (error) {
    console.error('Balance error:', error.message);
    res.status(500).json({ error: error.message });
  }
});

// Start server
app.listen(PORT, () => {
  console.log('\n🚀 ================================');
  console.log('   IBT Bridge Backend Started');
  console.log('   ================================');
  console.log(`   Port: ${PORT}`);
  console.log(`   Environment: LOCALNET`);
  console.log(`   Ethereum: Anvil (127.0.0.1:8545)`);
  console.log(`   Sui: Local (127.0.0.1:9000)`);
  console.log('   ================================\n');
});
