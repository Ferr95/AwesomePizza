import { OrderStatus } from '../../types/enums';
import styles from './StatusBadge.module.css';

const STATUS_LABELS: Record<OrderStatus, string> = {
  [OrderStatus.PENDING]: 'Pending',
  [OrderStatus.IN_PROGRESS]: 'In Progress',
  [OrderStatus.READY]: 'Ready',
  [OrderStatus.PICKED_UP]: 'Picked Up',
};

const STATUS_ICONS: Record<OrderStatus, string> = {
  [OrderStatus.PENDING]: '⏳',
  [OrderStatus.IN_PROGRESS]: '🔥',
  [OrderStatus.READY]: '✅',
  [OrderStatus.PICKED_UP]: '🎉',
};

interface Props {
  status: OrderStatus;
}

export default function StatusBadge({ status }: Props) {
  return (
    <span className={`${styles.badge} ${styles[status]}`}>
      {STATUS_ICONS[status]} {STATUS_LABELS[status]}
    </span>
  );
}
