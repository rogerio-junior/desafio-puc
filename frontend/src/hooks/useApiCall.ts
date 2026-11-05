import { useCallback, useRef, useState } from 'react';

export type ApiCallState<TData, TError = unknown> =
  | { status: 'initial' }
  | { status: 'loading' }
  | { status: 'success'; data: TData }
  | { status: 'failed'; error: TError };

export function useApiCall<TArgs extends unknown[], TData>(
  request: (...args: TArgs) => Promise<TData>,
) {
  const [state, setState] = useState<ApiCallState<TData>>({ status: 'initial' });
  // Identifica a chamada mais recente: a resposta de uma chamada antiga é ignorada.
  const latestCallId = useRef(0);

  const call = useCallback(
    async (...args: TArgs) => {
      const callId = ++latestCallId.current;
      setState({ status: 'loading' });

      try {
        const data = await request(...args);
        if (callId === latestCallId.current) {
          setState({ status: 'success', data });
        }
      } catch (error) {
        if (callId === latestCallId.current) {
          setState({ status: 'failed', error });
        }
      }
    },
    [request],
  );

  const reset = useCallback(() => {
    latestCallId.current++;
    setState({ status: 'initial' });
  }, []);

  return { state, call, reset };
}
