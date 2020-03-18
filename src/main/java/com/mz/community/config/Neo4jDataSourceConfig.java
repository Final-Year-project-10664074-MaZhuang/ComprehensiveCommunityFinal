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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
@Configuration
@MapperScan(basePackages = "com.mz.community.dao.neo4jMapper",sqlSessionFactoryRef = "Neo4jSqlSessionFactory")
public class Neo4jDataSourceConfig {
    @Bean(name = "Neo4jDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.neo4j")
    public DataSource getNeo4jDataSource(){
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "Neo4jSqlSessionFactory")
    public SqlSessionFactory Neo4jSqlSessionFactory(@Qualifier("Neo4jDataSource") DataSource dataSource,
                                                    MybatisProperties mybatisProperties) throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage(mybatisProperties.getTypeAliasesPackage());
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/neo4j/*.xml"));
        return bean.getObject();
    }
    @Bean(name = "Neo4jSqlSessionTemplate")
    public SqlSessionTemplate Neo4jSqlSessionTemplate(@Qualifier("Neo4jSqlSessionFactory") SqlSessionFactory sessionFactory){
        return new SqlSessionTemplate(sessionFactory);
    }
}
