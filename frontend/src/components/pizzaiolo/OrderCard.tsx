import { useState } from 'react';
import { motion } from 'framer-motion';
import type { OrderResponse } from '../../types/order';
import { OrderStatus } from '../../types/enums';
import { updateOrderStatus } from '../../api/pizzaioloApi';
import { formatDate } from '../../utils/formatDate';
import { formatCurrency } from '../../utils/formatCurrency';
import StatusBadge from './StatusBadge';
import styles from './OrderCard.module.css';

const NEXT_STATUS: Partial<Record<OrderStatus, { status: OrderStatus; label: string; className: string }>> = {
  [OrderStatus.PENDING]: { status: OrderStatus.IN_PROGRESS, label: 'Start Preparing', className: styles.startButton },
  [OrderStatus.IN_PROGRESS]: { status: OrderStatus.READY, label: 'Mark Ready', className: styles.readyButton },
  [OrderStatus.READY]: { status: OrderStatus.PICKED_UP, label: 'Mark Picked Up', className: styles.pickupButton },
};

interface Props {
  order: OrderResponse;
  isCurrent?: boolean;
  onStatusUpdate: () => void;
}

export default function OrderCard({ order, isCurrent, onStatusUpdate }: Props) {
  const [updating, setUpdating] = useState(false);
  const nextAction = NEXT_STATUS[order.status];

  const handleAction = async () => {
    if (!nextAction) return;
    setUpdating(true);
    try {
      await updateOrderStatus(order.id, { status: nextAction.status });
      onStatusUpdate();
    } finally {
      setUpdating(false);
    }
  };

  return (
    <motion.div
      className={`${styles.card} ${isCurrent ? styles.cardCurrent : ''}`}
      layout
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
    >
      <div className={styles.header}>
        <span className={styles.trackingCode}>#{order.trackingCode}</span>
        <StatusBadge status={order.status} />
      </div>
      <div className={styles.meta}>
        <span className={styles.customerName}>
          {order.customerName || 'Anonymous'}
        </span>
        <span>{formatDate(order.createdAt)}</span>
      </div>
      <ul className={styles.itemsList}>
        {order.items.map((item, i) => (
          <li key={i} className={styles.item}>
            <span className={styles.itemName}>{item.pizzaName}</span>
            <span className={styles.itemQty}>
              x{item.quantity} &middot; {formatCurrency(item.price * item.quantity)}
            </span>
          </li>
        ))}
      </ul>
      {nextAction && (
        <div className={styles.footer}>
          <div />
          <button
            className={`${styles.actionButton} ${nextAction.className}`}
            onClick={handleAction}
            disabled={updating}
          >
            {updating ? 'Updating...' : nextAction.label}
          </button>
        </div>
      )}
    </motion.div>
  );
}
