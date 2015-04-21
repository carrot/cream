package com.carrotcreative.cream.cache;

import android.content.Context;

import com.carrotcreative.cream.tasks.ReadSerializableTask;
import com.carrotcreative.cream.tasks.WriteSerializableTask;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.regex.Pattern;

public class CacheManager {

    //=======================================
    //============== Singleton ==============
    //=======================================

    private static CacheManager sInstance;

    public static CacheManager getInstance(Context context){
        if(sInstance == null){
            sInstance = new CacheManager(context);
        }
        return sInstance;
    }

    //===================================
    //============== Class ==============
    //===================================

    private final File mRootDir;
    private final Context mContext;

    private CacheManager(Context context)
    {
        mContext = context;
        mRootDir = context.getCacheDir();
    }

    //============================================
    //================== Cache ===================
    //============================================

    public void readSerializable(String directoryString, String fileExtension, String prefix, boolean regardForExpiration,
                                 ReadSerializableTask.ReadSerializableCallback cb)
    {
        File directory = new File(mRootDir, directoryString);

        //Finding the file
        final File[] matchingFiles = getMatchingFiles(directory, prefix, fileExtension);
        for(File f : matchingFiles)
        {
            long expiration = getFileExpiration(f, fileExtension);

            //If it's not expired, or we have no regard for expiration
            if(!(System.currentTimeMillis() > expiration) || !regardForExpiration) {
                readSerializable(f, cb);
                return;
            }
        }
        cb.failure(null);
    }

    public void writeSerializable(String directoryString, long expirationMinutes, String fileExtension, String prefix, Serializable content, WriteSerializableTask.WriteSerializableCallback cb)
    {
        File directory = new File(mRootDir, directoryString);

        long expiration = getExpirationEpochMinutes(expirationMinutes);
        String fileString = prefix + "-" + expiration + "." + fileExtension;
        File file = new File(directory, fileString);
        deleteAllByPrefix(prefix, directory, fileExtension);
        writeSerializable(content, file, cb);
    }

    /**
     * This goes and deletes files that are expired by trashDays
     */
    public void runTrashCleanup(String directoryString, String fileExtension, long trashMinutes)
    {
        File cleanupDir = new File(mRootDir, directoryString);
        File[] allFiles = cleanupDir.listFiles();

        //http://docs.oracle.com/javase/1.5.0/docs/api/java/io/File.html#listFiles%28%29
        if(allFiles != null){
            for(File f : allFiles)
            {
                if(f.toString().endsWith(fileExtension))
                {
                    long trashDate = getFileTrashDate(f, fileExtension, trashMinutes);
                    if(f.isFile() && (System.currentTimeMillis() > trashDate))
                    {
                        f.delete();
                    }
                }
            }
        }
    }

    //==============================================
    //============== Helper Functions ==============
    //==============================================

    /**
     * Only to be ran by write functions.
     *
     * We're writing a fresh object, so obviously
     * we want to delete all of the old ones.
     */
    private void deleteAllByPrefix(String prefix, File directory, String fileExtension)
    {
        final File[] matchingFiles = getMatchingFiles(directory, prefix, fileExtension);
        for(File f : matchingFiles)
        {
            f.delete();
        }
    }

    private long getFileTrashDate(File f, String extension, long trashMinutes)
    {
        long fileExpiration = getFileExpiration(f, extension);
        long diff = 1000 * 60 * trashMinutes;
        return fileExpiration + diff;
    }

    private long getFileExpiration(File f, String extension)
    {
        String expirationString = f.getName().replaceFirst(".*-", "").replace("." + extension, "");
        return Long.parseLong(expirationString);
    }

    private long getExpirationEpochMinutes(long minutes)
    {
        long diff = 1000 * 60 * minutes;
        return System.currentTimeMillis() + diff;
    }

    private static File[] getMatchingFiles(File root, String prefix, String fileExtension) {
        String regex = prefix + "-.*" + "." + fileExtension;
        if(!root.isDirectory()) {
            root.mkdir();
            return new File[0];
        }
        final Pattern p = Pattern.compile(regex); // careful: could also throw an exception!
        return root.listFiles(new FileFilter(){
            @Override
            public boolean accept(File file) {
                return p.matcher(file.getName()).matches();
            }
        });
    }

    private static void writeSerializable(Serializable obj, File file,
                                          WriteSerializableTask.WriteSerializableCallback cb) {

        WriteSerializableTask task = new WriteSerializableTask(obj, file, cb);
        Void[] voidArray = new Void[0];
        task.execute(voidArray);
    }

    private void readSerializable(File file, ReadSerializableTask.ReadSerializableCallback cb) {

        ReadSerializableTask task = new ReadSerializableTask(cb, file);
        Void[] voidArray = new Void[0];
        task.execute(voidArray);
    }

}