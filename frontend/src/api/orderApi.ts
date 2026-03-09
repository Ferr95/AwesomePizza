import client from './client';
import type { CreateOrderRequest, OrderTrackingResponse } from '../types/order';

export const createOrder = async (request: CreateOrderRequest): Promise<OrderTrackingResponse> => {
  const { data } = await client.post<OrderTrackingResponse>('/orders', request);
  return data;
};

export const trackOrder = async (trackingCode: string): Promise<OrderTrackingResponse> => {
  const { data } = await client.get<OrderTrackingResponse>(`/orders/${trackingCode}`);
  return data;
};
