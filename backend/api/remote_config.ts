import type { VercelRequest, VercelResponse } from '@vercel/node';

export default function handler(req: VercelRequest, res: VercelResponse) {
  if (req.method !== 'GET') {
    res.status(405).json({ error: 'Method not allowed' });
    return;
  }

  res.status(200).json({
    showPaywall: true,
    nativeAdsInterval: 6,
    appOpenEnabled: true,
    rewardedForPro: true,
    experiments: {
      themeGrid: 'A',
      paywall: 'A'
    }
  });
}
