import type { VercelRequest, VercelResponse } from '@vercel/node';

const ALLOWED_EVENTS = new Set([
  'app_open',
  'keyboard_enabled',
  'theme_applied',
  'session_time',
  'ad_impression',
  'pro_click'
]);

export default function handler(req: VercelRequest, res: VercelResponse) {
  if (req.method !== 'POST') {
    res.status(405).json({ error: 'Method not allowed' });
    return;
  }

  const body = req.body as { events?: Array<{ name: string; timestamp: number; payload?: Record<string, unknown> }> };
  const events = Array.isArray(body?.events) ? body.events : [];
  const filtered = events.filter((e) => ALLOWED_EVENTS.has(e.name));

  // Privacy-first: no text payloads should ever be accepted.
  res.status(200).json({ accepted: filtered.length, dropped: events.length - filtered.length });
}
