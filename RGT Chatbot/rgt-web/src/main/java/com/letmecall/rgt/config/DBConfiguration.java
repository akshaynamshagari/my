package com.letmecall.rgt.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DBConfiguration {

	@Value("${entitymanager.packagesToScan}")
	private String packagesToScan;

	@Value("${spring.jpa.database-platform}")
	private String dialet;

	@Value("${spring.jpa.properties.hibernate.format_sql}")
	private String formatSql;

	@Value("${spring.jpa.properties.hibernate.show-sql}")
	private String showSql;

	@Value("${spring.jpa.properties.hibernate.generate_statistics}")
	private String generateStatistics;
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		DataSource dataSource = customDataSource();
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(dataSource);
		entityManagerFactory.setPackagesToScan(packagesToScan);
		entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", dialet);
		jpaProperties.put("hibernate.format_sql", formatSql);
		jpaProperties.put("hibernate.show_sql", showSql);
		jpaProperties.put("hibernate.generate_statistics", generateStatistics);

		entityManagerFactory.setJpaProperties(jpaProperties);
		return entityManagerFactory;
	}

	@Bean(name = "transactionManager")
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		jpaTransactionManager.setJpaDialect(new HibernateJpaDialect());
		return jpaTransactionManager;
	}

	@Bean(name = "customDataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource customDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public AuditorAware<String> auditorAware() {
		return new AuditorAwareImpl();
	}

}
