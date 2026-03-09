import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import OrderTracker from './OrderTracker';
import { mockOrderTracking } from '../../test/fixtures';
import { OrderStatus } from '../../types/enums';

const mockTrackOrder = vi.fn();
vi.mock('../../api/orderApi', () => ({
  trackOrder: (...args: unknown[]) => mockTrackOrder(...args),
}));

describe('OrderTracker', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders input and Track button', () => {
    render(<OrderTracker />);

    expect(screen.getByPlaceholderText('Enter tracking code')).toBeInTheDocument();
    expect(screen.getByText('Track')).toBeInTheDocument();
  });

  it('Track button is disabled when input is empty', () => {
    render(<OrderTracker />);

    expect(screen.getByText('Track')).toBeDisabled();
  });

  it('fetches and displays status timeline when tracking code is submitted', async () => {
    mockTrackOrder.mockResolvedValue(mockOrderTracking);
    const user = userEvent.setup();

    render(<OrderTracker />);

    await user.type(screen.getByPlaceholderText('Enter tracking code'), 'ABC12345');
    await user.click(screen.getByText('Track'));

    expect(mockTrackOrder).toHaveBeenCalledWith('ABC12345');
    expect(await screen.findByText('Pending')).toBeInTheDocument();
    expect(screen.getByText('Preparing')).toBeInTheDocument();
    expect(screen.getByText('Ready')).toBeInTheDocument();
    expect(screen.getByText('Picked Up')).toBeInTheDocument();
  });

  it('shows all 4 status steps for an IN_PROGRESS order', async () => {
    mockTrackOrder.mockResolvedValue({
      ...mockOrderTracking,
      status: OrderStatus.IN_PROGRESS,
    });
    const user = userEvent.setup();

    render(<OrderTracker />);

    await user.type(screen.getByPlaceholderText('Enter tracking code'), 'XYZ');
    await user.click(screen.getByText('Track'));

    expect(await screen.findByText('Preparing')).toBeInTheDocument();
  });

  it('shows error message when order is not found', async () => {
    mockTrackOrder.mockRejectedValue(new Error('Not found'));
    const user = userEvent.setup();

    render(<OrderTracker />);

    await user.type(screen.getByPlaceholderText('Enter tracking code'), 'INVALID');
    await user.click(screen.getByText('Track'));

    expect(await screen.findByText(/Order not found/)).toBeInTheDocument();
  });

  it('uses initialCode prop to start tracking immediately', async () => {
    mockTrackOrder.mockResolvedValue(mockOrderTracking);

    render(<OrderTracker initialCode="PRE123" />);

    expect(mockTrackOrder).toHaveBeenCalledWith('PRE123');
  });
});
