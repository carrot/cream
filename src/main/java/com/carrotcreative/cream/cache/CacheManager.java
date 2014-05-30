package com.carrotcreative.cream.cache;

import android.content.Context;

import com.carrotcreative.cream.tasks.ReadSerializableTask;
import com.carrotcreative.cream.tasks.WriteSerializableTask;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Caching Strategy
 *  -> Cache files when they are loaded from the API
 *  -> Flag each type of file with a specific expiration, EXPIRATION_DAYS away
 *  -> When the expiration date hits, we'll try to pull from the API again
 *  -> if we have network availability
 *          -> Hit the API
 *          -> If that somehow ended up failing
 *              -> Hit the cache with no regard to expiration
 *     else
 *          ->Hit the cache with no regard to expiration
 *
 *  -> Cache will be cleaned up after the file is TRASH_DAYS past expiration
 *     to prevent the cache from filling up too much.
 *
 *  -> Expired files will also be replaced in the event there is a successful
 *     API call to the same file.
 */
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

    private File mRootDir;
    public Context mContext;

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
        final File[] matchingFiles = getMatchingFiles(directory, prefix + "-.*");
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
        deleteAllByPrefix(prefix, directory);
        writeSerializable(content, file, cb);
    }

    /**
     * This goes and deletes files that are expired by trashDays
     * //TODO don't forget to run this somewhere
     */
    public void runTrashCleanup(File cleanupDir, String fileExtension, long trashMinutes)
    {
        File[] allFiles = cleanupDir.listFiles();
        for(File f : allFiles)
        {
            long trashDate = getFileTrashDate(f, fileExtension, trashMinutes);
            if(f.isFile() && (System.currentTimeMillis() > trashDate))
            {
                f.delete();
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
    private void deleteAllByPrefix(String prefix, File directory)
    {
        final File[] matchingFiles = getMatchingFiles(directory, prefix + "-.*");
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
        String expirationString = f.getName().replaceFirst(".*-", "").replace("."+ extension, "");
        long expiration = Long.parseLong(expirationString);
        return expiration;
    }

    private long getExpirationEpochMinutes(long minutes)
    {
        long diff = 1000 * 60 * minutes;
        return System.currentTimeMillis() + diff;
    }

    private static File[] getMatchingFiles(File root, String regex) {
        if(!root.isDirectory()) {
            throw new IllegalArgumentException(root+" is no directory.");
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