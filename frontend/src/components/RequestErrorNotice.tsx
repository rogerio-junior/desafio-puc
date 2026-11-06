import { ApiError, isUnavailableError } from '../api/client/ApiError';
import Notice from './Notice';

type RequestErrorNoticeProps = {
  title: string;
  error: unknown;
  unavailableDescription?: string;
};

// Diferencia o sistema fora do ar (rede ou 5xx) de uma requisição recusada pela API (4xx).
function RequestErrorNotice({
  title,
  error,
  unavailableDescription = 'Tente novamente em alguns minutos.',
}: RequestErrorNoticeProps) {
  if (isUnavailableError(error)) {
    return (
      <Notice
        type="error"
        title={title}
        message="O sistema está indisponível"
        description={unavailableDescription}
      />
    );
  }

  const { status, detail } = error as ApiError;

  return (
    <Notice
      type="error"
      title={title}
      message="A API recusou a requisição"
      description={detail ? `Detalhe: ${detail}` : `A API respondeu com o status ${status}.`}
    />
  );
}

export default RequestErrorNotice;
