import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getOrderQueue, getCurrentOrder, updateOrderStatus } from './pizzaioloApi';
import client from './client';
import { mockOrderQueue, mockOrderResponse } from '../test/fixtures';
import { OrderStatus } from '../types/enums';

vi.mock('./client');

describe('pizzaioloApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getOrderQueue', () => {
    it('fetches the FIFO order queue', async () => {
      vi.mocked(client.get).mockResolvedValue({ data: mockOrderQueue });

      const result = await getOrderQueue();

      expect(client.get).toHaveBeenCalledWith('/pizzaiolo/orders');
      expect(result).toHaveLength(2);
      expect(result[0].trackingCode).toBe('ABC12345');
    });
  });

  describe('getCurrentOrder', () => {
    it('returns the current order when one exists (200)', async () => {
      vi.mocked(client.get).mockResolvedValue({
        status: 200,
        data: { ...mockOrderResponse, status: OrderStatus.IN_PROGRESS },
      });

      const result = await getCurrentOrder();

      expect(result).not.toBeNull();
      expect(result?.status).toBe('IN_PROGRESS');
    });

    it('returns null when no order is in progress (204)', async () => {
      vi.mocked(client.get).mockResolvedValue({ status: 204, data: '' });

      const result = await getCurrentOrder();

      expect(result).toBeNull();
    });
  });

  describe('updateOrderStatus', () => {
    it('updates order status via PUT', async () => {
      const updated = { ...mockOrderResponse, status: OrderStatus.IN_PROGRESS };
      vi.mocked(client.put).mockResolvedValue({ data: updated });

      const result = await updateOrderStatus('order-1', { status: OrderStatus.IN_PROGRESS });

      expect(client.put).toHaveBeenCalledWith(
        '/pizzaiolo/orders/order-1/status',
        { status: 'IN_PROGRESS' }
      );
      expect(result.status).toBe('IN_PROGRESS');
    });
  });
});
