import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useCart } from '../../context/CartContext';
import { createOrder } from '../../api/orderApi';
import { formatCurrency } from '../../utils/formatCurrency';
import type { OrderTrackingResponse } from '../../types/order';
import OrderConfirmation from './OrderConfirmation';
import styles from './Cart.module.css';

export default function Cart() {
  const {
    items, customerName, isOpen, totalItems, totalPrice,
    toggleCart, setCartOpen, updateQuantity, removeItem, setCustomerName, clear,
  } = useCart();
  const [submitting, setSubmitting] = useState(false);
  const [confirmation, setConfirmation] = useState<OrderTrackingResponse | null>(null);

  const handleOrder = async () => {
    if (items.length === 0) return;
    setSubmitting(true);
    try {
      const result = await createOrder({
        customerName: customerName.trim() || undefined,
        items: items.map((i) => ({ pizzaId: i.pizza.id, quantity: i.quantity })),
      });
      setConfirmation(result);
      clear();
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      {/* Floating cart button */}
      <motion.button
        className={styles.cartButton}
        onClick={toggleCart}
        whileTap={{ scale: 0.9 }}
        initial={{ scale: 0 }}
        animate={{ scale: 1 }}
        transition={{ type: 'spring', delay: 0.5 }}
      >
        &#128722;
        {totalItems > 0 && <span className={styles.badge}>{totalItems}</span>}
      </motion.button>

      {/* Cart drawer */}
      <AnimatePresence>
        {isOpen && (
          <>
            <motion.div
              className={styles.overlay}
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setCartOpen(false)}
            />
            <motion.div
              className={styles.drawer}
              initial={{ x: '100%' }}
              animate={{ x: 0 }}
              exit={{ x: '100%' }}
              transition={{ type: 'spring', damping: 25, stiffness: 300 }}
            >
              <div className={styles.header}>
                <h2 className={styles.headerTitle}>Your Order</h2>
                <button className={styles.closeButton} onClick={() => setCartOpen(false)}>
                  &#10005;
                </button>
              </div>
              <div className={styles.body}>
                {items.length === 0 ? (
                  <div className={styles.emptyMessage}>
                    <span className={styles.emptyIcon}>&#128722;</span>
                    Your cart is empty.<br />Add some delicious pizzas!
                  </div>
                ) : (
                  items.map((item) => (
                    <div key={item.pizza.id} className={styles.cartItem}>
                      <div className={styles.itemInfo}>
                        <div className={styles.itemName}>{item.pizza.name}</div>
                        <div className={styles.itemPrice}>{formatCurrency(item.pizza.price)}</div>
                      </div>
                      <div className={styles.quantityControls}>
                        <button
                          className={styles.qtyButton}
                          onClick={() =>
                            item.quantity === 1
                              ? removeItem(item.pizza.id)
                              : updateQuantity(item.pizza.id, item.quantity - 1)
                          }
                        >
                          {item.quantity === 1 ? '🗑' : '−'}
                        </button>
                        <span className={styles.quantity}>{item.quantity}</span>
                        <button
                          className={styles.qtyButton}
                          onClick={() => updateQuantity(item.pizza.id, item.quantity + 1)}
                        >
                          +
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>
              {items.length > 0 && (
                <div className={styles.footer}>
                  <input
                    className={styles.nameInput}
                    type="text"
                    placeholder="Your name (optional)"
                    value={customerName}
                    onChange={(e) => setCustomerName(e.target.value)}
                  />
                  <div className={styles.totalRow}>
                    <span className={styles.totalLabel}>Total</span>
                    <span className={styles.totalAmount}>{formatCurrency(totalPrice)}</span>
                  </div>
                  <button
                    className={styles.orderButton}
                    onClick={handleOrder}
                    disabled={submitting}
                  >
                    {submitting ? 'Placing Order...' : 'Place Order'}
                  </button>
                </div>
              )}
            </motion.div>
          </>
        )}
      </AnimatePresence>

      {/* Order confirmation modal */}
      <AnimatePresence>
        {confirmation && (
          <OrderConfirmation
            tracking={confirmation}
            onClose={() => setConfirmation(null)}
          />
        )}
      </AnimatePresence>
    </>
  );
}
