import client from './client';
import type { OrderResponse, UpdateOrderStatusRequest } from '../types/order';

export const getOrderQueue = async (): Promise<OrderResponse[]> => {
  const { data } = await client.get<OrderResponse[]>('/pizzaiolo/orders');
  return data;
};

export const getCurrentOrder = async (): Promise<OrderResponse | null> => {
  const response = await client.get<OrderResponse>('/pizzaiolo/orders/current', {
    validateStatus: (status) => status === 200 || status === 204,
  });
  return response.status === 204 ? null : response.data;
};

export const updateOrderStatus = async (
  orderId: string,
  request: UpdateOrderStatusRequest
): Promise<OrderResponse> => {
  const { data } = await client.put<OrderResponse>(`/pizzaiolo/orders/${orderId}/status`, request);
  return data;
};
