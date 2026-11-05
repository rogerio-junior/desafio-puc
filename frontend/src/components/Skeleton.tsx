import styles from './Skeleton.module.css';

type SkeletonProps = {
  width: string;
};

function Skeleton({ width }: SkeletonProps) {
  return <div className={styles.skeleton} style={{ width }} />;
}

export default Skeleton;
