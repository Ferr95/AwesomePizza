import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import OrderCard from './OrderCard';
import { mockOrderResponse } from '../../test/fixtures';
import { OrderStatus } from '../../types/enums';

vi.mock('../../api/pizzaioloApi', () => ({
  updateOrderStatus: vi.fn().mockResolvedValue({}),
}));

describe('OrderCard', () => {
  const onStatusUpdate = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('displays tracking code and customer name', () => {
    render(<OrderCard order={mockOrderResponse} onStatusUpdate={onStatusUpdate} />);

    expect(screen.getByText('#ABC12345')).toBeInTheDocument();
    expect(screen.getByText('Mario')).toBeInTheDocument();
  });

  it('displays order items with quantities', () => {
    render(<OrderCard order={mockOrderResponse} onStatusUpdate={onStatusUpdate} />);

    expect(screen.getByText('Margherita')).toBeInTheDocument();
    expect(screen.getByText('Pepperoni')).toBeInTheDocument();
  });

  it('shows "Start Preparing" button for PENDING orders', () => {
    render(<OrderCard order={mockOrderResponse} onStatusUpdate={onStatusUpdate} />);

    expect(screen.getByText('Start Preparing')).toBeInTheDocument();
  });

  it('shows "Mark Ready" button for IN_PROGRESS orders', () => {
    const inProgressOrder = { ...mockOrderResponse, status: OrderStatus.IN_PROGRESS };
    render(<OrderCard order={inProgressOrder} onStatusUpdate={onStatusUpdate} />);

    expect(screen.getByText('Mark Ready')).toBeInTheDocument();
  });

  it('shows "Mark Picked Up" button for READY orders', () => {
    const readyOrder = { ...mockOrderResponse, status: OrderStatus.READY };
    render(<OrderCard order={readyOrder} onStatusUpdate={onStatusUpdate} />);

    expect(screen.getByText('Mark Picked Up')).toBeInTheDocument();
  });

  it('shows no action button for PICKED_UP orders', () => {
    const pickedUpOrder = { ...mockOrderResponse, status: OrderStatus.PICKED_UP };
    render(<OrderCard order={pickedUpOrder} onStatusUpdate={onStatusUpdate} />);

    expect(screen.queryByText('Start Preparing')).not.toBeInTheDocument();
    expect(screen.queryByText('Mark Ready')).not.toBeInTheDocument();
    expect(screen.queryByText('Mark Picked Up')).not.toBeInTheDocument();
  });

  it('calls updateOrderStatus and onStatusUpdate when action button is clicked', async () => {
    const { updateOrderStatus } = await import('../../api/pizzaioloApi');
    const user = userEvent.setup();

    render(<OrderCard order={mockOrderResponse} onStatusUpdate={onStatusUpdate} />);

    await user.click(screen.getByText('Start Preparing'));

    expect(updateOrderStatus).toHaveBeenCalledWith('order-1', { status: 'IN_PROGRESS' });
    expect(onStatusUpdate).toHaveBeenCalledOnce();
  });

  it('displays "Anonymous" when customerName is null', () => {
    const anonOrder = { ...mockOrderResponse, customerName: null };
    render(<OrderCard order={anonOrder} onStatusUpdate={onStatusUpdate} />);

    expect(screen.getByText('Anonymous')).toBeInTheDocument();
  });
});
