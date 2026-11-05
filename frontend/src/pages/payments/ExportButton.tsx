import Button from '../../components/Button';
import styles from './ExportButton.module.css';

const LABELS = {
  idle: 'Exportar CSV',
  loading: 'Exportando…',
};

type ExportButtonProps = {
  isLoading: boolean;
  disabledReason: string;
  onClick: () => void;
};

function ExportButton({ isLoading, disabledReason, onClick }: ExportButtonProps) {
  return (
    <span className={styles.wrapper}>
      <Button
        type="button"
        variant="outline"
        disabled={isLoading || disabledReason !== ''}
        labels={LABELS}
        activeLabel={isLoading ? 'loading' : 'idle'}
        onClick={onClick}
      />
      {disabledReason && <span className={styles.tooltip}>{disabledReason}</span>}
    </span>
  );
}

export default ExportButton;
