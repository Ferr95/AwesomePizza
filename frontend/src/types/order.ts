import { OrderStatus } from './enums';

export interface OrderItemRequest {
  pizzaId: string;
  quantity: number;
}

export interface CreateOrderRequest {
  customerName?: string;
  items: OrderItemRequest[];
}

export interface UpdateOrderStatusRequest {
  status: OrderStatus;
}

export interface OrderItemResponse {
  pizzaId: string;
  pizzaName: string;
  quantity: number;
  price: number;
}

export interface OrderResponse {
  id: string;
  trackingCode: string;
  customerName: string | null;
  status: OrderStatus;
  createdAt: string;
  updatedAt: string;
  items: OrderItemResponse[];
}

export interface OrderTrackingResponse {
  trackingCode: string;
  status: OrderStatus;
  createdAt: string;
  updatedAt: string;
}
