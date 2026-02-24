import { NextApiRequest, NextApiResponse } from 'next';

/**
 * GET /api/fonts_catalog
 * 
 * Returns list of available keyboard fonts
 * 
 * Features:
 * - Free and PRO fonts
 * - Font categories
 * - Download URLs for custom fonts
 * - Edge cached (24 hours)
 */
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  // Only allow GET
  if (req.method !== 'GET') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  try {
    // Fetch static fonts catalog from public directory
    const fontsUrl = `${process.env.VERCEL_URL || 'http://localhost:3000'}/fonts/fonts_catalog.json`;
    const response = await fetch(fontsUrl);
    
    if (!response.ok) {
      throw new Error('Failed to fetch fonts catalog');
    }
    
    const fontsData = await response.json();
    
    // Set aggressive caching for CDN (24 hours)
    res.setHeader('Cache-Control', 's-maxage=86400, stale-while-revalidate=43200');
    res.setHeader('CDN-Cache-Control', 'public, max-age=86400');
    res.setHeader('Vercel-CDN-Cache-Control', 'public, max-age=86400');
    
    return res.status(200).json(fontsData);
    
  } catch (error) {
    console.error('Error fetching fonts catalog:', error);
    
    // Return fallback minimal fonts list
    return res.status(500).json({
      fonts: [
        {
          id: 'roboto',
          name: 'Roboto',
          category: 'sans-serif',
          requiresPro: false,
          source: 'system',
          description: 'Default system font'
        }
      ],
      categories: [
        {
          id: 'sans-serif',
          name: 'Sans Serif',
          description: 'Clean and modern fonts'
        }
      ]
    });
  }
}

export const config = {
  runtime: 'edge',
};
