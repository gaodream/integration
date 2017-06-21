package com.integration.bigdata.mongodb;

import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * @author gaogao
 *
 */
public class MongoAccess {
	
	MongoClient mongoClient = null;
	MongoDatabase db = null;
	
	@Before
	public void init(){
		System.out.println("====init====");
		//1.获取mongo链接对象
		mongoClient = new MongoClient(Arrays.asList(new ServerAddress("192.168.1.124", 27017)));
		db = mongoClient.getDatabase("admin");
	}
	
	@After
	public void destory(){
		System.out.println("====destory====");
		mongoClient.close();
		
	}
	
	
	//1.创建集合和获取集合
	@Test
	public void testCreate(){
	
	/*	CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions();
		createCollectionOptions.storageEngineOptions()
		db.createCollection("ygkg", createCollectionOptions );*/
		db.createCollection("ygkg");
        MongoCollection<Document> collection = db.getCollection("ygkg");
        //collection.drop();
        System.out.println("1.创建集合成功"+collection.getNamespace());
        
        //查询所有集合名称
        //db.listCollectionNames();
        //查询所有集合
       // db.listCollections();
	}
	
	
	//2.插入单个对象和集合对象
	@Test
	public void testInsert(){ 
		
/*		MongoCollection<UserDocument> collection = db.getCollection("ygkg",UserDocument.class);
        UserDocument document = new UserDocument("zhangsan","张三","13951884271");
        document.put("zhangsan", "张三");
        collection.insertOne(document);*/
        MongoCollection<Document> collection = db.getCollection("ygkg");
        Document document = new Document();
        document.put("zhangsan", "张三");
		//Document.parse(userDocument.toJson());
		//Document.parse(UserDocument.toJson(),decoder);
      //  CodecCache.
		collection.insertOne(document);
        
      /*  List<UserDocument> userList = new ArrayList<UserDocument>();
        userList.add(new  UserDocument("zhangsan","张三","13951884271"));
        userList.add(new  UserDocument("lisi","李四","13951884272"));
        collection.insertMany(userList);*/
	}
	
	//3.检索所有文档
	@Test
	public void testFind(){ 
		//获得集合时可以指定Document的具体类型
		MongoCollection<UserDocument> collection = db.getCollection("ygkg", UserDocument.class);
		Bson filter = new UserDocument();//UserDocument本身就实现Bson接口，此处是过滤条件
		FindIterable<UserDocument> iterator = collection.find(filter);//collection提供丰富的函数对数据的操作
		//iterator.filter(filter);
		MongoCursor<UserDocument> cursor = iterator.iterator();
		while(cursor.hasNext()){
			//System.err.println(cursor.next().getUserName());
			System.err.println(cursor.next().get("zhangsan"));
		}
	}
	


}
