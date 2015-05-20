package com.carrotcreative.cream.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectSizeUtil
{
    private ObjectSizeUtil() {}

    public static int getObjectSize(Serializable object)
    {
        //keeping it one as in worst case every entry will at least have value one.
        int size = 1;
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
            oos.close();
            size = baos.size();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return size;
    }
}
