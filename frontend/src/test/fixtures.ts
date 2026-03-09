import type { PizzaResponse } from '../types/pizza';
import type { OrderResponse, OrderTrackingResponse } from '../types/order';
import { OrderStatus } from '../types/enums';

export const mockPizzas: PizzaResponse[] = [
  { id: '1', name: 'Margherita', description: 'Tomato sauce, mozzarella, fresh basil', price: 8.5 },
  { id: '2', name: 'Pepperoni', description: 'Tomato sauce, mozzarella, pepperoni', price: 10.0 },
  { id: '3', name: 'Quattro Formaggi', description: 'Mozzarella, gorgonzola, parmesan, fontina', price: 11.5 },
];

export const mockOrderTracking: OrderTrackingResponse = {
  trackingCode: 'ABC12345',
  status: OrderStatus.PENDING,
  createdAt: '2026-03-08T10:00:00',
  updatedAt: '2026-03-08T10:00:00',
};

export const mockOrderResponse: OrderResponse = {
  id: 'order-1',
  trackingCode: 'ABC12345',
  customerName: 'Mario',
  status: OrderStatus.PENDING,
  createdAt: '2026-03-08T10:00:00',
  updatedAt: '2026-03-08T10:00:00',
  items: [
    { pizzaId: '1', pizzaName: 'Margherita', quantity: 2, price: 8.5 },
    { pizzaId: '2', pizzaName: 'Pepperoni', quantity: 1, price: 10.0 },
  ],
};

export const mockOrderQueue: OrderResponse[] = [
  mockOrderResponse,
  {
    id: 'order-2',
    trackingCode: 'DEF67890',
    customerName: null,
    status: OrderStatus.PENDING,
    createdAt: '2026-03-08T10:05:00',
    updatedAt: '2026-03-08T10:05:00',
    items: [
      { pizzaId: '3', pizzaName: 'Quattro Formaggi', quantity: 1, price: 11.5 },
    ],
  },
];
