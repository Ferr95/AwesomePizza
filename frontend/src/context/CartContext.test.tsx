import { renderHook, act } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { CartProvider, useCart } from './CartContext';
import { mockPizzas } from '../test/fixtures';

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <CartProvider>{children}</CartProvider>
);

describe('CartContext', () => {
  it('starts with an empty cart', () => {
    const { result } = renderHook(() => useCart(), { wrapper });
    expect(result.current.items).toEqual([]);
    expect(result.current.totalItems).toBe(0);
    expect(result.current.totalPrice).toBe(0);
  });

  it('adds a pizza to the cart', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => result.current.addItem(mockPizzas[0]));

    expect(result.current.items).toHaveLength(1);
    expect(result.current.items[0].pizza.name).toBe('Margherita');
    expect(result.current.items[0].quantity).toBe(1);
    expect(result.current.totalItems).toBe(1);
    expect(result.current.totalPrice).toBe(8.5);
  });

  it('increments quantity when adding the same pizza twice', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => result.current.addItem(mockPizzas[0]));
    act(() => result.current.addItem(mockPizzas[0]));

    expect(result.current.items).toHaveLength(1);
    expect(result.current.items[0].quantity).toBe(2);
    expect(result.current.totalItems).toBe(2);
    expect(result.current.totalPrice).toBe(17.0);
  });

  it('adds different pizzas as separate items', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => result.current.addItem(mockPizzas[0]));
    act(() => result.current.addItem(mockPizzas[1]));

    expect(result.current.items).toHaveLength(2);
    expect(result.current.totalItems).toBe(2);
    expect(result.current.totalPrice).toBe(18.5);
  });

  it('updates item quantity', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => result.current.addItem(mockPizzas[0]));
    act(() => result.current.updateQuantity('1', 5));

    expect(result.current.items[0].quantity).toBe(5);
    expect(result.current.totalItems).toBe(5);
    expect(result.current.totalPrice).toBe(42.5);
  });

  it('removes item when quantity is set to 0', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => result.current.addItem(mockPizzas[0]));
    act(() => result.current.updateQuantity('1', 0));

    expect(result.current.items).toHaveLength(0);
  });

  it('removes a specific item', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => result.current.addItem(mockPizzas[0]));
    act(() => result.current.addItem(mockPizzas[1]));
    act(() => result.current.removeItem('1'));

    expect(result.current.items).toHaveLength(1);
    expect(result.current.items[0].pizza.name).toBe('Pepperoni');
  });

  it('clears the entire cart', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => result.current.addItem(mockPizzas[0]));
    act(() => result.current.addItem(mockPizzas[1]));
    act(() => result.current.setCustomerName('Mario'));
    act(() => result.current.clear());

    expect(result.current.items).toHaveLength(0);
    expect(result.current.customerName).toBe('');
    expect(result.current.isOpen).toBe(false);
  });

  it('sets customer name', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => result.current.setCustomerName('Luigi'));

    expect(result.current.customerName).toBe('Luigi');
  });

  it('toggles cart open/closed', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    expect(result.current.isOpen).toBe(false);
    act(() => result.current.toggleCart());
    expect(result.current.isOpen).toBe(true);
    act(() => result.current.toggleCart());
    expect(result.current.isOpen).toBe(false);
  });

  it('throws error when used outside CartProvider', () => {
    expect(() => {
      renderHook(() => useCart());
    }).toThrow('useCart must be used within CartProvider');
  });
});
