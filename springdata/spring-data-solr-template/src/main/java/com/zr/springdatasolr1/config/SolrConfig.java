package com.zr.springdatasolr1.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;

@Configuration
public class SolrConfig {
    @Value("${spring.data.solr.host}")
    private String solrHost;

    @Value("${spring.data.solr.core}")
    private String solrCore;

    /**
     * 配置SolrTemplate
     */
    @Bean
    public SolrTemplate solrTemplate() {
        HttpSolrServer solrServer = new HttpSolrServer(solrHost);
        SolrTemplate template = new SolrTemplate(solrServer);
        template.setSolrCore(solrCore);
        // 2018-03-12晚更新，此处不需要加这句话，反而加了之后会导致solr域和实体类字段不同名时，
        // 导致无法将值映射到实体类字段中，此时会抛出异常，所以这里不需要加下面这句话
        // template.setSolrConverter(new SolrJConverter());
        return template;
    }

}
