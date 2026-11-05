import { useEffect, useState } from 'react';

const INTERVAL_MS = 40;
const TOTAL_STEPS = 18;

function shouldAnimate(target: number) {
  const reducedMotion = window.matchMedia(
    '(prefers-reduced-motion: reduce)',
  ).matches;

  return target > 1 && !reducedMotion;
}

// Conta de 0 até target quando o componente monta.
export function useCountUp(target: number) {
  const [value, setValue] = useState(() => (shouldAnimate(target) ? 0 : target));

  useEffect(() => {
    if (!shouldAnimate(target)) return;

    const step = Math.ceil(target / TOTAL_STEPS);
    let current = 0;

    const timer = setInterval(() => {
      current = Math.min(target, current + step);
      setValue(current);
      if (current >= target) clearInterval(timer);
    }, INTERVAL_MS);

    return () => clearInterval(timer);
  }, [target]);

  return value;
}
