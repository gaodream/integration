package com.integration.bigdata.mongodb;

import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClient;

/**
 * 
 * @author gaogao
 * 对于自定义的 userDocument需要重写或者扩展MongoClient
 */
public class MongoCustomClient extends MongoClient{

/*	private static final CodecRegistry DEFAULT_CODEC_REGISTRY =
            fromProviders(asList(new ValueCodecProvider(),
                    new DBRefCodecProvider(),
                    new DocumentCodecProvider(new DocumentToDBRefTransformer()),
                    new UserDocumentCodecProvider(),
                    new DBObjectCodecProvider(),
                    new BsonValueCodecProvider(),
                    new GeoJsonCodecProvider()));*/
	
	
    public static CodecRegistry getDefaultCodecRegistry() {
    	CodecRegistry codecRegistry = getDefaultCodecRegistry();
        return codecRegistry;
    }
}

