import { http, createConfig } from 'wagmi'

// Custom chain config pentru Anvil local
export const anvilLocal = {
  id: 31337,
  name: 'Anvil Local',
  nativeCurrency: {
    decimals: 18,
    name: 'Ethereum',
    symbol: 'ETH',
  },
  rpcUrls: {
    default: { http: ['http://127.0.0.1:8545'] },
    public: { http: ['http://127.0.0.1:8545'] },
  },
}

export const config = createConfig({
  chains: [anvilLocal],
  transports: {
    [anvilLocal.id]: http(),
  },
})
