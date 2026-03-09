import { describe, it, expect } from 'vitest';
import { formatCurrency } from './formatCurrency';

describe('formatCurrency', () => {
  it('formats whole numbers with two decimals', () => {
    expect(formatCurrency(10)).toMatch(/10\.00/);
  });

  it('formats decimal prices correctly', () => {
    expect(formatCurrency(8.5)).toMatch(/8\.50/);
  });

  it('formats zero', () => {
    expect(formatCurrency(0)).toMatch(/0\.00/);
  });

  it('includes EUR symbol', () => {
    const formatted = formatCurrency(10);
    expect(formatted).toMatch(/€/);
  });
});
