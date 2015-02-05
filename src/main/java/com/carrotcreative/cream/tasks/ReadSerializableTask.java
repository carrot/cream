package com.carrotcreative.cream.tasks;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class ReadSerializableTask extends AsyncTask<Void, Void, Serializable> {

    private ReadSerializableCallback mCallback;
    private File mFileToRead;
    private Exception mException;

    public ReadSerializableTask(ReadSerializableCallback callback, File toRead){
        mCallback = callback;
        mFileToRead = toRead;
    }

    @Override
    protected Serializable doInBackground(Void... params)
    {
        Serializable serializableObject = null;
        FileInputStream fis = null;
        ObjectInputStream is = null;

        try {
            fis = new FileInputStream(mFileToRead);
            is = new ObjectInputStream(fis);
            serializableObject = (Serializable) is.readObject();
        } catch(Exception e) {
            mException = e;
        } finally {
            try {
                if (fis != null)   fis.close();
                if (is != null)   is.close();
            } catch (Exception e) { /* Do nothing */ }
        }

        return serializableObject;
    }

    protected void onPostExecute(Serializable obj)
    {
        super.onPostExecute(obj);

        if(obj != null)
            mCallback.success(obj);
        else
            mCallback.failure(mException);

        mCallback.always();
    }

    public interface ReadSerializableCallback {
        void success(Serializable object);
        void failure(Exception error);
        void always();
    }

}