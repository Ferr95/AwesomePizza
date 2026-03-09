import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import StatusBadge from './StatusBadge';
import { OrderStatus } from '../../types/enums';

describe('StatusBadge', () => {
  it('renders Pending status', () => {
    render(<StatusBadge status={OrderStatus.PENDING} />);
    expect(screen.getByText(/Pending/)).toBeInTheDocument();
  });

  it('renders In Progress status', () => {
    render(<StatusBadge status={OrderStatus.IN_PROGRESS} />);
    expect(screen.getByText(/In Progress/)).toBeInTheDocument();
  });

  it('renders Ready status', () => {
    render(<StatusBadge status={OrderStatus.READY} />);
    expect(screen.getByText(/Ready/)).toBeInTheDocument();
  });

  it('renders Picked Up status', () => {
    render(<StatusBadge status={OrderStatus.PICKED_UP} />);
    expect(screen.getByText(/Picked Up/)).toBeInTheDocument();
  });
});
