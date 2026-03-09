import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import PizzaCarousel from './PizzaCarousel';
import { CartProvider } from '../../context/CartContext';

vi.mock('../../api/pizzaApi', () => ({
  getAllPizzas: vi.fn().mockResolvedValue([
    { id: '1', name: 'Margherita', description: 'Tomato sauce, mozzarella, fresh basil', price: 8.5 },
    { id: '2', name: 'Pepperoni', description: 'Tomato sauce, mozzarella, pepperoni', price: 10.0 },
    { id: '3', name: 'Quattro Formaggi', description: 'Mozzarella, gorgonzola, parmesan, fontina', price: 11.5 },
  ]),
}));

function renderWithCart(ui: React.ReactElement) {
  return render(<CartProvider>{ui}</CartProvider>);
}

describe('PizzaCarousel', () => {
  it('renders menu title', async () => {
    renderWithCart(<PizzaCarousel />);

    expect(await screen.findByText('Menu')).toBeInTheDocument();
  });

  it('fetches and displays all pizzas', async () => {
    renderWithCart(<PizzaCarousel />);

    expect(await screen.findByText('Margherita')).toBeInTheDocument();
    expect(screen.getByText('Pepperoni')).toBeInTheDocument();
    expect(screen.getByText('Quattro Formaggi')).toBeInTheDocument();
  });

  it('displays pizza prices', async () => {
    renderWithCart(<PizzaCarousel />);

    await screen.findByText('Margherita');
    expect(screen.getByText(/8\.50/)).toBeInTheDocument();
    expect(screen.getByText(/10\.00/)).toBeInTheDocument();
  });

  it('shows Add button for each pizza', async () => {
    renderWithCart(<PizzaCarousel />);

    await screen.findByText('Margherita');
    const addButtons = screen.getAllByText('+ Add');
    expect(addButtons).toHaveLength(3);
  });
});
