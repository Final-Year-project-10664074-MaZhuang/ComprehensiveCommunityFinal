package com.mz.community.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.mz.community.dao.mysqlMapper",sqlSessionFactoryRef = "MySqlSessionFactory")
public class MysqlDataSourceConfig {
    @Bean(name = "MysqlDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource getMysqlDataSource(){
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "MySqlSessionFactory")
    @Primary
    public SqlSessionFactory MySqlSessionFactory(@Qualifier("MysqlDataSource") DataSource dataSource,
                                                 MybatisProperties mybatisProperties) throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setConfiguration(mybatisProperties.getConfiguration());
        bean.setTypeAliasesPackage(mybatisProperties.getTypeAliasesPackage());
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/mysql/*.xml"));
        return bean.getObject();
    }
    @Bean(name = "MySqlSessionTemplate")
    @Primary
    public SqlSessionTemplate MySqlSessionTemplate(@Qualifier("MySqlSessionFactory") SqlSessionFactory sessionFactory){
        return new SqlSessionTemplate(sessionFactory);
    }
}
