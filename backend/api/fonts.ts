import type { VercelRequest, VercelResponse } from '@vercel/node';

/**
 * Fonts API endpoint
 * Returns list of available custom fonts for keyboard
 * Edge cached for 24 hours
 */
export default async function handler(
  req: VercelRequest,
  res: VercelResponse,
) {
  // Set CORS headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, OPTIONS');
  
  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  if (req.method !== 'GET') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  try {
    // Fetch fonts.json from public folder
    const fontsUrl = `${process.env.VERCEL_URL ? `https://${process.env.VERCEL_URL}` : 'http://localhost:3000'}/fonts.json`;
    const response = await fetch(fontsUrl);
    
    if (!response.ok) {
      throw new Error('Failed to fetch fonts');
    }
    
    const fonts = await response.json();

    // Set cache headers (24 hours)
    res.setHeader('Cache-Control', 's-maxage=86400, stale-while-revalidate');
    
    return res.status(200).json(fonts);
    
  } catch (error) {
    console.error('Fonts API error:', error);
    return res.status(500).json({ 
      error: 'Internal server error',
      message: error instanceof Error ? error.message : 'Unknown error'
    });
  }
}
