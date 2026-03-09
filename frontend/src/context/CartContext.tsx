import { createContext, useContext, useReducer, type ReactNode } from 'react';
import type { PizzaResponse } from '../types/pizza';

export interface CartItem {
  pizza: PizzaResponse;
  quantity: number;
}

interface CartState {
  items: CartItem[];
  customerName: string;
  isOpen: boolean;
}

type CartAction =
  | { type: 'ADD_ITEM'; pizza: PizzaResponse }
  | { type: 'REMOVE_ITEM'; pizzaId: string }
  | { type: 'UPDATE_QUANTITY'; pizzaId: string; quantity: number }
  | { type: 'SET_CUSTOMER_NAME'; name: string }
  | { type: 'CLEAR' }
  | { type: 'TOGGLE_CART' }
  | { type: 'SET_CART_OPEN'; open: boolean };

interface CartContextValue extends CartState {
  dispatch: React.Dispatch<CartAction>;
  totalItems: number;
  totalPrice: number;
  addItem: (pizza: PizzaResponse) => void;
  removeItem: (pizzaId: string) => void;
  updateQuantity: (pizzaId: string, quantity: number) => void;
  setCustomerName: (name: string) => void;
  clear: () => void;
  toggleCart: () => void;
  setCartOpen: (open: boolean) => void;
}

const CartContext = createContext<CartContextValue | null>(null);

function cartReducer(state: CartState, action: CartAction): CartState {
  switch (action.type) {
    case 'ADD_ITEM': {
      const existing = state.items.find((i) => i.pizza.id === action.pizza.id);
      if (existing) {
        return {
          ...state,
          items: state.items.map((i) =>
            i.pizza.id === action.pizza.id ? { ...i, quantity: i.quantity + 1 } : i
          ),
        };
      }
      return { ...state, items: [...state.items, { pizza: action.pizza, quantity: 1 }] };
    }
    case 'REMOVE_ITEM':
      return { ...state, items: state.items.filter((i) => i.pizza.id !== action.pizzaId) };
    case 'UPDATE_QUANTITY':
      if (action.quantity <= 0) {
        return { ...state, items: state.items.filter((i) => i.pizza.id !== action.pizzaId) };
      }
      return {
        ...state,
        items: state.items.map((i) =>
          i.pizza.id === action.pizzaId ? { ...i, quantity: action.quantity } : i
        ),
      };
    case 'SET_CUSTOMER_NAME':
      return { ...state, customerName: action.name };
    case 'CLEAR':
      return { ...state, items: [], customerName: '', isOpen: false };
    case 'TOGGLE_CART':
      return { ...state, isOpen: !state.isOpen };
    case 'SET_CART_OPEN':
      return { ...state, isOpen: action.open };
    default:
      return state;
  }
}

export function CartProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(cartReducer, {
    items: [],
    customerName: '',
    isOpen: false,
  });

  const totalItems = state.items.reduce((sum, i) => sum + i.quantity, 0);
  const totalPrice = state.items.reduce((sum, i) => sum + i.pizza.price * i.quantity, 0);

  const value: CartContextValue = {
    ...state,
    dispatch,
    totalItems,
    totalPrice,
    addItem: (pizza) => dispatch({ type: 'ADD_ITEM', pizza }),
    removeItem: (pizzaId) => dispatch({ type: 'REMOVE_ITEM', pizzaId }),
    updateQuantity: (pizzaId, quantity) => dispatch({ type: 'UPDATE_QUANTITY', pizzaId, quantity }),
    setCustomerName: (name) => dispatch({ type: 'SET_CUSTOMER_NAME', name }),
    clear: () => dispatch({ type: 'CLEAR' }),
    toggleCart: () => dispatch({ type: 'TOGGLE_CART' }),
    setCartOpen: (open) => dispatch({ type: 'SET_CART_OPEN', open }),
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error('useCart must be used within CartProvider');
  return ctx;
}
