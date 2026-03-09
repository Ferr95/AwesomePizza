import { useEffect, useState, useRef } from 'react';
import { getAllPizzas } from '../../api/pizzaApi';
import type { PizzaResponse } from '../../types/pizza';
import { useCart } from '../../context/CartContext';
import PizzaCard from './PizzaCard';
import styles from './PizzaCarousel.module.css';

export default function PizzaCarousel() {
  const [pizzas, setPizzas] = useState<PizzaResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const scrollRef = useRef<HTMLDivElement>(null);
  const { addItem } = useCart();

  useEffect(() => {
    getAllPizzas()
      .then(setPizzas)
      .finally(() => setLoading(false));
  }, []);

  const scroll = (direction: 'left' | 'right') => {
    if (!scrollRef.current) return;
    const amount = 300;
    scrollRef.current.scrollBy({
      left: direction === 'left' ? -amount : amount,
      behavior: 'smooth',
    });
  };

  if (loading) {
    return (
      <div className={styles.loading}>
        <div className={styles.spinner} />
      </div>
    );
  }

  return (
    <section className={styles.section}>
      <div className={styles.header}>
        <h1 className={styles.title}>
          Our <span className={styles.titleAccent}>Menu</span>
        </h1>
        <p className={styles.subtitle}>Handcrafted with love, baked to perfection</p>
      </div>
      <div className={styles.carouselWrapper}>
        <button className={`${styles.arrowButton} ${styles.arrowLeft}`} onClick={() => scroll('left')}>
          &#8249;
        </button>
        <div className={styles.carousel} ref={scrollRef}>
          {pizzas.map((pizza, i) => (
            <PizzaCard key={pizza.id} pizza={pizza} index={i} onAddToCart={addItem} />
          ))}
        </div>
        <button className={`${styles.arrowButton} ${styles.arrowRight}`} onClick={() => scroll('right')}>
          &#8250;
        </button>
      </div>
    </section>
  );
}
