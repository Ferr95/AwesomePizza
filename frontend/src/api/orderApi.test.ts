import { describe, it, expect, vi, beforeEach } from 'vitest';
import { createOrder, trackOrder } from './orderApi';
import client from './client';
import { mockOrderTracking } from '../test/fixtures';

vi.mock('./client');

describe('orderApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('createOrder', () => {
    it('posts order to /orders and returns tracking info', async () => {
      vi.mocked(client.post).mockResolvedValue({ data: mockOrderTracking });

      const request = {
        customerName: 'Mario',
        items: [{ pizzaId: '1', quantity: 2 }],
      };
      const result = await createOrder(request);

      expect(client.post).toHaveBeenCalledWith('/orders', request);
      expect(result.trackingCode).toBe('ABC12345');
      expect(result.status).toBe('PENDING');
    });

    it('sends order without customerName when not provided', async () => {
      vi.mocked(client.post).mockResolvedValue({ data: mockOrderTracking });

      const request = { items: [{ pizzaId: '1', quantity: 1 }] };
      await createOrder(request);

      expect(client.post).toHaveBeenCalledWith('/orders', request);
    });
  });

  describe('trackOrder', () => {
    it('fetches order status by tracking code', async () => {
      vi.mocked(client.get).mockResolvedValue({ data: mockOrderTracking });

      const result = await trackOrder('ABC12345');

      expect(client.get).toHaveBeenCalledWith('/orders/ABC12345');
      expect(result.trackingCode).toBe('ABC12345');
    });
  });
});
