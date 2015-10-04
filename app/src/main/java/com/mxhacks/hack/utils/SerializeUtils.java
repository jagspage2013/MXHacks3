package com.mxhacks.hack.utils;

import com.mxhacks.hack.model.MusicZone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by José Ángel García Salinas on 04/10/15.
 */
public class SerializeUtils {

    public static byte[] serialize(Object m) throws IOException{
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(b);
        objectOutputStream.writeObject(m);
        return b.toByteArray();
    }

    public static Object deserialize(byte[] bytes)throws IOException,ClassNotFoundException{
        ByteArrayInputStream v = new ByteArrayInputStream(bytes);
        ObjectInputStream i = new ObjectInputStream(v);
        return i.readObject();
    }
}
