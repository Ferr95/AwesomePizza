import { useSearchParams } from 'react-router-dom';
import OrderTracker from '../components/customer/OrderTracker';

export default function TrackPage() {
  const [params] = useSearchParams();
  const code = params.get('code') || undefined;

  return (
    <div style={{ minHeight: 'calc(100vh - 72px)', paddingTop: '2rem' }}>
      <OrderTracker initialCode={code} />
    </div>
  );
}
