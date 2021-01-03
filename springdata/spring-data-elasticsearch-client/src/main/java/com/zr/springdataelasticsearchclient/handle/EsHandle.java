package com.zr.springdataelasticsearchclient.handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zr.springdataelasticsearchclient.pojo.User;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.ArrayList;

public class EsHandle {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

        // 1、创建索引
    void testCreateIndex() throws IOException {

        // 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("test_index");
        // 客户端执行：创建索引的请求
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

        System.out.println("索引: " + createIndexResponse);
    }

        // 2、判断索引是否存在
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("test_index");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);

        System.out.println(exists);
    }

        // 3、删除索引
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("test_index");
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);

        System.out.println(response);
    }

        // 1、测试添加一个文档
    void testAddDocument() throws IOException {

        User user = new User("陆", 19);

        String userJson = objectMapper.writeValueAsString(user);


        IndexRequest request = new IndexRequest("person");
        request.source(userJson, XContentType.JSON);
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        System.out.println(indexResponse);
    }

        // 2、测试获取文档：判断是否存在指定的文档
    void tesIsExists() throws IOException {
        GetRequest Request = new GetRequest("person", "fswRhnEBWRRkPpZFKbAZ");
        boolean exists = client.exists(Request, RequestOptions.DEFAULT);
        System.out.println("是否存在：" + exists);
    }

        // 3、获取一个文档信息
    void testGetDocument() throws IOException {
        GetRequest Request = new GetRequest("person", "fswRhnEBWRRkPpZFKbAZ");
        GetResponse documentFields = client.get(Request, RequestOptions.DEFAULT);

        System.out.println(documentFields); // {"_index":"person","_type":"_doc","_id":"fswRhnEBWRRkPpZFKbAZ","_version":1,"_seq_no":0,"_primary_term":1,"found":true,"_source":{"name":"谢","age":20}}
        System.out.println(documentFields.getSourceAsString()); // {"name":"谢","age":20}
    }

        // 4、更新一个文档信息
    void updateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("person", "fswRhnEBWRRkPpZFKbAZ");
        User user = new User("谢谢", 21);
        updateRequest.doc(objectMapper.writeValueAsString(user), XContentType.JSON);
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
    }

        // 5、删除一个文档
    void deleteDocument() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("person", "fswRhnEBWRRkPpZFKbAZ");
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);

        System.out.println(deleteResponse);
        System.out.println(deleteResponse.status());  //
    }

        // 6、批量操作，这里只演示批量插入
    void bulkInsert() throws IOException {
        ArrayList<Object> userList = new ArrayList<>();
        userList.add(new User("谢1", 19));
        userList.add(new User("谢2", 19));
        userList.add(new User("谢3", 19));

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        for (int i = 0; i < userList.size(); i++) {
            bulkRequest.add(new IndexRequest("person").source(objectMapper.writeValueAsString(userList.get(i)), XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures());  // 是否失败，false代表成功
    }

        // 查询
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("person");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 精确查询
        //TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "陆");
        //sourceBuilder.query(termQueryBuilder);

        // 查询所有
        MatchAllQueryBuilder allQueryBuilder = QueryBuilders.matchAllQuery();
        sourceBuilder.query(allQueryBuilder);

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(objectMapper.writeValueAsString(searchResponse.getHits()));
        System.out.println("========================");
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }

    }
}
