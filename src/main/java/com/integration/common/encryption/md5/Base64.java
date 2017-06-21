package com.integration.common.encryption.md5;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

import sun.misc.BASE64Decoder;

@SuppressWarnings("restriction")
public class Base64 
{
	public static BufferedReader decode(String b64string) throws Exception
    {
        return new BufferedReader(
                   new InputStreamReader(
                       MimeUtility.decode(
                            new ByteArrayInputStream(
                                b64string.getBytes()), "base64")));
    }

    public static String decodeAsString(String b64string) throws Exception
    {
        if (b64string == null)
        {
            return b64string;
        }

        BASE64Decoder decoder = new BASE64Decoder();
        String returnString = new String(decoder.decodeBuffer(b64string));

        
        return returnString.trim();
    }

    public static ByteArrayOutputStream encode(String plaintext)
            throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] in = plaintext.getBytes();
        ByteArrayOutputStream inStream = new ByteArrayOutputStream();
        inStream.write(in, 0, in.length);
        // pad
        if ((in.length % 3 ) == 1)
        {
            inStream.write(0);
            inStream.write(0);
        }
        else if((in.length % 3 ) == 2)
        {
            inStream.write(0);
        }
        inStream.writeTo( MimeUtility.encode(out, "base64")  );
        return out;
    }

}
