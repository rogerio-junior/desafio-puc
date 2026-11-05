import styles from './Pagination.module.css';

type PaginationProps = {
  count: string;
  offset: number;
  pageSize: number;
  total: number;
  onChange: (offset: number) => void;
};

function Pagination({ count, offset, pageSize, total, onChange }: PaginationProps) {
  const page = Math.floor(offset / pageSize) + 1;
  const pages = Math.ceil(total / pageSize);

  return (
    <div className={styles.pagination}>
      <span className={styles.text}>{count}</span>
      {pages > 1 && (
        <div className={styles.pages}>
          <button
            type="button"
            className={styles.button}
            title="Página anterior"
            disabled={offset === 0}
            onClick={() => onChange(Math.max(0, offset - pageSize))}
          >
            <svg width="18" height="18" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M16 10H4M9 5l-5 5 5 5" />
            </svg>
          </button>
          <span className={`${styles.text} ${styles.position}`}>
            Página {page} de {pages}
          </span>
          <button
            type="button"
            className={styles.button}
            title="Próxima página"
            disabled={offset + pageSize >= total}
            onClick={() => onChange(offset + pageSize)}
          >
            <svg width="18" height="18" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M4 10h12M11 5l5 5-5 5" />
            </svg>
          </button>
        </div>
      )}
    </div>
  );
}

export default Pagination;
