import { head, list, type ListBlobResultBlob } from '@vercel/blob';
import { promises as fs } from 'node:fs';
import path from 'node:path';

export async function readJsonFromBlob(blobPath: string) {
  await head(blobPath);
  const result = await list({ prefix: blobPath, limit: 1 });
  const blob = result.blobs.find((b: ListBlobResultBlob) => b.pathname === blobPath);

  if (!blob) {
    throw new Error('Blob file not found');
  }

  const response = await fetch(blob.url);
  return response.json();
}

export async function readLocalThemesFallback() {
  const filePath = path.join(process.cwd(), 'public', 'themes', 'trending.json');
  const raw = await fs.readFile(filePath, 'utf8');
  return JSON.parse(raw);
}
