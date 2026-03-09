import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import PizzaCard from './PizzaCard';
import { mockPizzas } from '../../test/fixtures';

describe('PizzaCard', () => {
  it('displays pizza name, description, and price', () => {
    render(<PizzaCard pizza={mockPizzas[0]} index={0} onAddToCart={vi.fn()} />);

    expect(screen.getByText('Margherita')).toBeInTheDocument();
    expect(screen.getByText('Tomato sauce, mozzarella, fresh basil')).toBeInTheDocument();
    expect(screen.getByText(/8\.50/)).toBeInTheDocument();
  });

  it('calls onAddToCart with the pizza when Add button is clicked', async () => {
    const onAddToCart = vi.fn();
    const user = userEvent.setup();

    render(<PizzaCard pizza={mockPizzas[0]} index={0} onAddToCart={onAddToCart} />);

    await user.click(screen.getByText('+ Add'));

    expect(onAddToCart).toHaveBeenCalledOnce();
    expect(onAddToCart).toHaveBeenCalledWith(mockPizzas[0]);
  });

  it('renders different pizzas correctly', () => {
    render(<PizzaCard pizza={mockPizzas[1]} index={1} onAddToCart={vi.fn()} />);

    expect(screen.getByText('Pepperoni')).toBeInTheDocument();
    expect(screen.getByText(/10\.00/)).toBeInTheDocument();
  });
});
