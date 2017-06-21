package com.integration.bigdata.mongodb;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class UserDocumentCodec implements Codec<UserDocument> {

	@Override
	public void encode(BsonWriter writer, UserDocument value, EncoderContext encoderContext) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<UserDocument> getEncoderClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDocument decode(BsonReader reader, DecoderContext decoderContext) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
