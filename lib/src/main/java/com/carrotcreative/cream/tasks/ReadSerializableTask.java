package com.carrotcreative.cream.tasks;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class ReadSerializableTask extends AsyncTask<Void, Void, Void> {

    private ReadSerializableCallback mCallback;
    private File mFileToRead;

    public ReadSerializableTask(ReadSerializableCallback callback, File toRead){
        mCallback = callback;
        mFileToRead = toRead;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Exception exception = null;
        Serializable serializableObject = null;
        FileInputStream fis = null;
        ObjectInputStream is = null;

        try {
            fis = new FileInputStream(mFileToRead);
            is = new ObjectInputStream(fis);
            serializableObject = (Serializable) is.readObject();
        } catch(Exception e) {
            exception = e;
        } finally {
            try {
                if (fis != null)   fis.close();
                if (is != null)   is.close();
            } catch (Exception e) { /* Do nothing */ }
        }

        if(serializableObject != null){
            mCallback.success(serializableObject);
        }
        else{
            mCallback.failure(exception);
        }

        return null;
    }

    protected void onPostExecute(Void value)
    {
        super.onPostExecute(value);
        mCallback.always();
    }

    public interface ReadSerializableCallback {
        void success(Serializable object);
        void failure(Exception error);
        void always();
    }

}