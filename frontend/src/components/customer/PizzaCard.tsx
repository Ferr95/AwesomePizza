import { motion } from 'framer-motion';
import type { PizzaResponse } from '../../types/pizza';
import { formatCurrency } from '../../utils/formatCurrency';
import styles from './PizzaCard.module.css';

const GRADIENTS = [styles.gradient1, styles.gradient2, styles.gradient3, styles.gradient4, styles.gradient5];
const EMOJIS = ['🍕', '🍕', '🧀', '🌶️', '🫒'];

interface Props {
  pizza: PizzaResponse;
  index: number;
  onAddToCart: (pizza: PizzaResponse) => void;
}

export default function PizzaCard({ pizza, index, onAddToCart }: Props) {
  return (
    <motion.div
      className={styles.card}
      initial={{ opacity: 0, y: 30 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: '-40px' }}
      transition={{ duration: 0.4, delay: index * 0.1 }}
    >
      <div className={`${styles.imageSection} ${GRADIENTS[index % GRADIENTS.length]}`}>
        <motion.span
          className={styles.pizzaEmoji}
          whileHover={{ scale: 1.2, rotate: 10 }}
          transition={{ type: 'spring', stiffness: 300 }}
        >
          {EMOJIS[index % EMOJIS.length]}
        </motion.span>
      </div>
      <div className={styles.content}>
        <h3 className={styles.name}>{pizza.name}</h3>
        <p className={styles.description}>{pizza.description}</p>
        <div className={styles.footer}>
          <span className={styles.price}>{formatCurrency(pizza.price)}</span>
          <motion.button
            className={styles.addButton}
            whileTap={{ scale: 0.9 }}
            onClick={() => onAddToCart(pizza)}
          >
            + Add
          </motion.button>
        </div>
      </div>
    </motion.div>
  );
}
