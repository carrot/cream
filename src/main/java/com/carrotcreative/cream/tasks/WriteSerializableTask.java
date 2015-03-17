package com.carrotcreative.cream.tasks;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class WriteSerializableTask extends AsyncTask<Void, Void, Void> {

    private WriteSerializableCallback mCallback;
    private Serializable mObject;
    private File mFile;

    public WriteSerializableTask(Serializable obj, File file, WriteSerializableCallback callback){
        mCallback = callback;
        mObject = obj;
        mFile = file;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Exception exception = null;
        FileOutputStream fos  = null;
        ObjectOutputStream oos  = null;
        boolean keep = true;

        try {
            fos = new FileOutputStream(mFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(mObject);
        } catch (Exception e) {
            keep = false;
            exception = e;
        } finally {
            try {
                if (oos != null)   oos.close();
                if (fos != null)   fos.close();
                if (!keep) //noinspection ResultOfMethodCallIgnored
                    mFile.delete();
            } catch (Exception e) { exception = e; }
        }

        if(keep)
        {
            mCallback.success();
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

    public interface WriteSerializableCallback {
        void success();
        void failure(Exception error);
        void always();
    }

}
