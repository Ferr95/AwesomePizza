import client from './client';
import type { PizzaResponse } from '../types/pizza';

export const getAllPizzas = async (): Promise<PizzaResponse[]> => {
  const { data } = await client.get<PizzaResponse[]>('/pizzas');
  return data;
};

export const getPizzaById = async (id: string): Promise<PizzaResponse> => {
  const { data } = await client.get<PizzaResponse>(`/pizzas/${id}`);
  return data;
};
