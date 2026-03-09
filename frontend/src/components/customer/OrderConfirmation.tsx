import { useState } from 'react';
import { motion } from 'framer-motion';
import type { OrderTrackingResponse } from '../../types/order';
import styles from './OrderConfirmation.module.css';

interface Props {
  tracking: OrderTrackingResponse;
  onClose: () => void;
}

export default function OrderConfirmation({ tracking, onClose }: Props) {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    await navigator.clipboard.writeText(tracking.trackingCode);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <motion.div
      className={styles.overlay}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      onClick={onClose}
    >
      <motion.div
        className={styles.modal}
        initial={{ scale: 0.8, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        exit={{ scale: 0.8, opacity: 0 }}
        transition={{ type: 'spring', damping: 20 }}
        onClick={(e) => e.stopPropagation()}
      >
        <motion.div
          className={styles.checkmark}
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          transition={{ type: 'spring', delay: 0.2, stiffness: 200 }}
        >
          &#10004;&#65039;
        </motion.div>
        <h2 className={styles.title}>Order Placed!</h2>
        <p className={styles.message}>
          Your pizza is on its way. Use the tracking code below to check your order status.
        </p>
        <div className={styles.codeLabel}>Tracking Code</div>
        <div className={styles.codeBox}>
          <span className={styles.code}>{tracking.trackingCode}</span>
          <button className={styles.copyButton} onClick={handleCopy}>
            {copied ? 'Copied!' : 'Copy'}
          </button>
        </div>
        <button className={styles.closeButton} onClick={onClose}>
          Got it!
        </button>
      </motion.div>
    </motion.div>
  );
}
