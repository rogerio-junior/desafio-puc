import styles from './Pagination.module.css';

type PaginationProps = {
  offset: number;
  pageSize: number;
  itemCount: number;
  total: number;
  onChange: (offset: number) => void;
};

function Pagination({ offset, pageSize, itemCount, total, onChange }: PaginationProps) {
  const lastItem = offset + itemCount;

  return (
    <div className={styles.pagination}>
      <span className={styles.range}>
        {offset + 1}–{lastItem} de {total}
      </span>
      <div className={styles.buttons}>
        <button
          type="button"
          className={styles.button}
          disabled={offset === 0}
          onClick={() => onChange(offset - pageSize)}
        >
          Anterior
        </button>
        <button
          type="button"
          className={styles.button}
          disabled={lastItem >= total}
          onClick={() => onChange(offset + pageSize)}
        >
          Próxima
        </button>
      </div>
    </div>
  );
}

export default Pagination;
