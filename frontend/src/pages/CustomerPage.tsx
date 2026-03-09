import PizzaCarousel from '../components/customer/PizzaCarousel';
import Cart from '../components/customer/Cart';
import OrderTracker from '../components/customer/OrderTracker';
import styles from './CustomerPage.module.css';

export default function CustomerPage() {
  return (
    <div className={styles.page}>
      <PizzaCarousel />
      <div className={styles.divider}>
        <hr className={styles.dividerLine} />
      </div>
      <OrderTracker />
      <Cart />
    </div>
  );
}
