import { useState, useCallback } from 'react';
import { motion } from 'framer-motion';
import { trackOrder } from '../../api/orderApi';
import type { OrderTrackingResponse } from '../../types/order';
import { OrderStatus } from '../../types/enums';
import { usePolling } from '../../hooks/usePolling';
import { formatDate } from '../../utils/formatDate';
import styles from './OrderTracker.module.css';

const STEPS: { status: OrderStatus; label: string; icon: string }[] = [
  { status: OrderStatus.PENDING, label: 'Pending', icon: '&#128203;' },
  { status: OrderStatus.IN_PROGRESS, label: 'Preparing', icon: '&#128293;' },
  { status: OrderStatus.READY, label: 'Ready', icon: '&#9989;' },
  { status: OrderStatus.PICKED_UP, label: 'Picked Up', icon: '&#127881;' },
];

const STATUS_INDEX: Record<OrderStatus, number> = {
  [OrderStatus.PENDING]: 0,
  [OrderStatus.IN_PROGRESS]: 1,
  [OrderStatus.READY]: 2,
  [OrderStatus.PICKED_UP]: 3,
};

interface Props {
  initialCode?: string;
}

export default function OrderTracker({ initialCode }: Props) {
  const [code, setCode] = useState(initialCode || '');
  const [activeCode, setActiveCode] = useState(initialCode || '');

  const fetcher = useCallback(() => trackOrder(activeCode), [activeCode]);
  const { data: tracking, loading, error } = usePolling<OrderTrackingResponse>(
    fetcher,
    5000,
    activeCode.length > 0
  );

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (code.trim()) setActiveCode(code.trim().toUpperCase());
  };

  const currentIndex = tracking ? STATUS_INDEX[tracking.status] : -1;
  const progressWidth = tracking
    ? `${(currentIndex / (STEPS.length - 1)) * 100}%`
    : '0%';

  return (
    <section className={styles.section}>
      <h2 className={styles.title}>Track Your Order</h2>
      <form className={styles.searchBox} onSubmit={handleSubmit}>
        <input
          className={styles.input}
          type="text"
          placeholder="Enter tracking code"
          value={code}
          onChange={(e) => setCode(e.target.value)}
        />
        <button className={styles.searchButton} type="submit" disabled={!code.trim()}>
          Track
        </button>
      </form>

      {activeCode && !loading && error && (
        <div className={styles.notFound}>Order not found. Check your tracking code.</div>
      )}

      {tracking && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <div className={styles.timeline}>
            <div className={styles.timelineProgress} style={{ width: progressWidth }} />
            {STEPS.map((step, i) => {
              const isCompleted = i < currentIndex;
              const isActive = i === currentIndex;
              return (
                <div key={step.status} className={styles.step}>
                  <motion.div
                    className={`${styles.stepDot} ${isActive ? styles.stepActive : ''} ${isCompleted ? styles.stepCompleted : ''}`}
                    initial={false}
                    animate={isActive ? { scale: [1, 1.15, 1] } : {}}
                    transition={{ repeat: Infinity, duration: 2 }}
                    dangerouslySetInnerHTML={{ __html: step.icon }}
                  />
                  <span className={`${styles.stepLabel} ${isActive ? styles.stepLabelActive : ''}`}>
                    {step.label}
                  </span>
                </div>
              );
            })}
          </div>
          <div className={styles.updatedAt}>
            Last updated: {formatDate(tracking.updatedAt)}
          </div>
        </motion.div>
      )}
    </section>
  );
}
