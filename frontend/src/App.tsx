import { Routes, Route } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { CartProvider } from './context/CartContext';
import Navbar from './components/layout/Navbar';
import CustomerPage from './pages/CustomerPage';
import TrackPage from './pages/TrackPage';
import PizzaioloPage from './pages/PizzaioloPage';

export default function App() {
  return (
    <CartProvider>
      <Navbar />
      <Routes>
        <Route path="/" element={<CustomerPage />} />
        <Route path="/track" element={<TrackPage />} />
        <Route path="/pizzaiolo" element={<PizzaioloPage />} />
      </Routes>
      <Toaster
        position="bottom-center"
        toastOptions={{
          style: {
            fontFamily: 'var(--font-body)',
            borderRadius: 'var(--radius-md)',
          },
        }}
      />
    </CartProvider>
  );
}
