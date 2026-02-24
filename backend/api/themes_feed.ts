import type { VercelRequest, VercelResponse } from '@vercel/node';
import { readJsonFromBlob, readLocalThemesFallback } from '../lib/themeSource.js';

export default async function handler(req: VercelRequest, res: VercelResponse) {
  if (req.method !== 'GET') {
    res.status(405).json({ error: 'Method not allowed' });
    return;
  }

  try {
    const fromBlob = await readJsonFromBlob('themes/trending.json');
    res.status(200).json(fromBlob);
  } catch {
    const fallback = await readLocalThemesFallback();
    res.status(200).json(fallback);
  }
}
