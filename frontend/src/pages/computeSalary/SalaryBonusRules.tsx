import styles from './SalaryBonusRules.module.css';

const RULES = [
  {
    description:
      'Funcionários contratados por tempo indeterminado, com aniversário no mês',
    value: '+ R$ 200,00',
  },
  {
    description:
      'Funcionários contratados por tempo indeterminado ou a prazo, homem com mais de 65 anos ou mulher com mais de 60',
    value: '+ 3% do base',
  },
  {
    description: 'Estagiários ou bolsistas, por sábado ou domingo comparecido',
    value: '+ R$ 40,00',
  },
];

function SalaryBonusRules() {
  return (
    <section>
      <h2 className={styles.title}>Adicionais ao salário base</h2>
      <ul className={styles.list}>
        {RULES.map((rule) => (
          <li key={rule.value} className={styles.rule}>
            <span className={styles.description}>{rule.description}</span>
            <span className={styles.value}>{rule.value}</span>
          </li>
        ))}
      </ul>
    </section>
  );
}

export default SalaryBonusRules;
