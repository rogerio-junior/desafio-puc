import { useCallback, useRef, useState } from 'react';

// TODO: Simplificar declaração e criação
export type ApiCallState<TData, TError = unknown> =
  | { status: 'initial'; isInitial: true; isLoading: false; isSuccess: false; isFailed: false }
  | { status: 'loading'; isInitial: false; isLoading: true; isSuccess: false; isFailed: false }
  | { status: 'success'; isInitial: false; isLoading: false; isSuccess: true; isFailed: false; data: TData }
  | { status: 'failed'; isInitial: false; isLoading: false; isSuccess: false; isFailed: true; error: TError };

const INITIAL_STATE = {
  status: 'initial',
  isInitial: true,
  isLoading: false,
  isSuccess: false,
  isFailed: false,
} as const;

const LOADING_STATE = {
  status: 'loading',
  isInitial: false,
  isLoading: true,
  isSuccess: false,
  isFailed: false,
} as const;

export function useApiCall<TArgs extends unknown[], TData>(
  request: (...args: TArgs) => Promise<TData>,
) {
  const [state, setState] = useState<ApiCallState<TData>>(INITIAL_STATE);
  // Identifica a chamada mais recente: a resposta de uma chamada antiga é ignorada.
  const latestCallId = useRef(0);

  const call = useCallback(
    async (...args: TArgs) => {
      const callId = ++latestCallId.current;
      setState(LOADING_STATE);

      try {
        const data = await request(...args);
        if (callId === latestCallId.current) {
          setState({ status: 'success', isInitial: false, isLoading: false, isSuccess: true, isFailed: false, data });
        }
      } catch (error) {
        if (callId === latestCallId.current) {
          setState({ status: 'failed', isInitial: false, isLoading: false, isSuccess: false, isFailed: true, error });
        }
      }
    },
    [request],
  );

  const reset = useCallback(() => {
    latestCallId.current++;
    setState(INITIAL_STATE);
  }, []);

  return { state, call, reset };
}
