import styles from './Notice.module.css';

type NoticeType = 'error' | 'info';

type NoticeProps = {
  type: NoticeType;
  title: string;
  message: string;
  description: string;
};

function Notice({ type, title, message, description }: NoticeProps) {
  return (
    <div className={`rise ${styles.notice} ${styles[type]}`}>
      <p className={styles.title}>{title}</p>
      <div className={styles.rule} />
      <h2 className={styles.message}>{message}</h2>
      <p className={styles.description}>{description}</p>
    </div>
  );
}

export default Notice;
