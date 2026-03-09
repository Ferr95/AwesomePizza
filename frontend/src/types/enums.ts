export const OrderStatus = {
  PENDING: 'PENDING',
  IN_PROGRESS: 'IN_PROGRESS',
  READY: 'READY',
  PICKED_UP: 'PICKED_UP',
} as const;

export type OrderStatus = (typeof OrderStatus)[keyof typeof OrderStatus];
