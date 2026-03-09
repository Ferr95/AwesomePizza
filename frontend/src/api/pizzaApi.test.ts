import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getAllPizzas, getPizzaById } from './pizzaApi';
import client from './client';
import { mockPizzas } from '../test/fixtures';

vi.mock('./client');

describe('pizzaApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getAllPizzas', () => {
    it('fetches all pizzas from /pizzas', async () => {
      vi.mocked(client.get).mockResolvedValue({ data: mockPizzas });

      const result = await getAllPizzas();

      expect(client.get).toHaveBeenCalledWith('/pizzas');
      expect(result).toEqual(mockPizzas);
      expect(result).toHaveLength(3);
    });
  });

  describe('getPizzaById', () => {
    it('fetches a single pizza by id', async () => {
      vi.mocked(client.get).mockResolvedValue({ data: mockPizzas[0] });

      const result = await getPizzaById('1');

      expect(client.get).toHaveBeenCalledWith('/pizzas/1');
      expect(result.name).toBe('Margherita');
    });
  });
});
