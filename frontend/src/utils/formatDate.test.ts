import { describe, it, expect } from 'vitest';
import { formatDate } from './formatDate';

describe('formatDate', () => {
  it('formats ISO date string to readable format', () => {
    const formatted = formatDate('2026-03-08T14:30:00');
    expect(formatted).toContain('Mar');
    expect(formatted).toContain('8');
  });

  it('includes time in the output', () => {
    const formatted = formatDate('2026-03-08T14:30:00');
    // Should contain hour and minute
    expect(formatted).toMatch(/\d{1,2}:\d{2}/);
  });
});
