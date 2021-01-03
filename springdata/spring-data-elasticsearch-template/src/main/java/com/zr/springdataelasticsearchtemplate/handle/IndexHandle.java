package com.zr.springdataelasticsearchtemplate.handle;

import com.zr.springdataelasticsearchtemplate.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

public class IndexHandle {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 创建索引规则，相当于简历约束规则
     */
    public void createIndex(){
        // 创建索引，会根据Item类的@Document注解信息来创建
        elasticsearchTemplate.createIndex(Item.class);
        // 配置映射，会根据Item类中的id、Field等字段来自动完成映射
        elasticsearchTemplate.putMapping(Item.class);

    }

    /**
     * 删除索引
     */
    public void deleteIndex() {
        elasticsearchTemplate.deleteIndex("item");
    }

    public boolean getIndex(){
        boolean exists = elasticsearchTemplate.indexExists("item");
        return exists;
    }

    public void addDocument(){
    }
}
