import { useCallback } from 'react';
import { AnimatePresence } from 'framer-motion';
import { getOrderQueue } from '../api/pizzaioloApi';
import { usePolling } from '../hooks/usePolling';
import type { OrderResponse } from '../types/order';
import { OrderStatus } from '../types/enums';
import OrderCard from '../components/pizzaiolo/OrderCard';
import styles from './PizzaioloPage.module.css';

export default function PizzaioloPage() {
  const fetcher = useCallback(() => getOrderQueue(), []);
  const { data: orders, loading, refetch } = usePolling<OrderResponse[]>(fetcher, 3000);

  if (loading) {
    return (
      <div className={styles.loading}>
        <div className={styles.spinner} />
      </div>
    );
  }

  const currentOrder = orders?.find((o) => o.status === OrderStatus.IN_PROGRESS) || null;
  const pendingOrders = orders?.filter((o) => o.status === OrderStatus.PENDING) || [];
  const readyOrders = orders?.filter((o) => o.status === OrderStatus.READY) || [];
  const queue = [...pendingOrders, ...readyOrders];

  return (
    <div className={styles.page}>
      <h1 className={styles.title}>Pizzaiolo Dashboard</h1>
      <p className={styles.subtitle}>Manage your order queue</p>

      <div className={styles.currentSection}>
        <h2 className={styles.sectionTitle}>Now Preparing</h2>
        {currentOrder ? (
          <OrderCard order={currentOrder} isCurrent onStatusUpdate={refetch} />
        ) : (
          <div className={styles.idle}>
            <span className={styles.idleIcon}>&#128168;</span>
            No order in progress. Pick the next one from the queue below.
          </div>
        )}
      </div>

      <div>
        <h2 className={styles.sectionTitle}>
          Order Queue ({queue.length})
        </h2>
        {queue.length === 0 ? (
          <div className={styles.emptyQueue}>
            No orders waiting. Time for a break!
          </div>
        ) : (
          <div className={styles.queueList}>
            <AnimatePresence>
              {queue.map((order) => (
                <OrderCard key={order.id} order={order} onStatusUpdate={refetch} />
              ))}
            </AnimatePresence>
          </div>
        )}
      </div>
    </div>
  );
}
