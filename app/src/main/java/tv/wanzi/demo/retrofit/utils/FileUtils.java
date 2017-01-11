package tv.wanzi.demo.retrofit.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Cache;
import tv.wanzi.demo.retrofit.MainApplication;

/**
 * Created by drawf on 17/1/2.
 * ------------------------------
 * ($rootDir)
 * +- /data                -> Environment.getDataDirectory()
 * |   |
 * |   |   ($appDataDir)
 * |   +- data/tv.wanzi.demo.retrofit
 * |       |
 * |       |   ($filesDir)
 * |       +- files            -> Context.getFilesDir() / Context.getFileStreamPath("")
 * |       |       |
 * |       |       +- file1    -> Context.getFileStreamPath("file1")
 * |       |   ($cacheDir)
 * |       +- cache            -> Context.getCacheDir()
 * |       |
 * |       +- app_$name        ->(Context.getDir(String name, int mode)
 * |
 * |   ($rootDir)
 * +- /storage/sdcard0     -> Environment.getExternalStorageDirectory()
 * |                       / Environment.getExternalStoragePublicDirectory("")
 * |
 * +- dir1             -> Environment.getExternalStoragePublicDirectory("dir1")
 * |
 * |   ($appDataDir)
 * +- Android/data/tv.wanzi.demo.retrofit
 * |
 * |   ($filesDir)
 * +- files        -> Context.getExternalFilesDir("")
 * |   |
 * |   +- file1    -> Context.getExternalFilesDir("file1")
 * |   +- Music    -> Context.getExternalFilesDir(Environment.Music);
 * |   +- Picture  -> ... Environment.Picture
 * |   +- ...
 * |
 * |   ($cacheDir)
 * +- cache        -> Context.getExternalCacheDir()
 * |
 * +- ???
 */

public class FileUtils {
    private static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;//10M

    private static boolean hasSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static Cache getOkHttpCache() {
        File responses = getEFDDir("responses");
        if (responses == null) return null;

        return new Cache(responses, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE);
    }


    /**
     * 获取外部存储files下的一个目录
     *
     * @param name
     * @return
     */
    private static File getEFDDir(String name) {
        if (!hasSDCardMounted()) return null;

        Context context = MainApplication.getContext();
        return context.getExternalFilesDir(name);
    }

    /**
     * 获取可用存储空间
     *
     * @param path
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static long getUsableSpace(File path) {
        if (path == null) return -1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        } else {
            if (!path.exists()) return 0;

            final StatFs stats = new StatFs(path.getPath());
            return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
        }
    }


    /**
     * 单个资源文件拷贝到外部存储目录
     *
     * @param filePath 被拷贝的文件路径 e.g. "image/a.jpg"
     * @return 拷贝后的File对象
     * @throws IOException
     */
    public static File copyAssetFile2EFD(String filePath) throws IOException {
        if (!hasSDCardMounted()) return null;
        Context context = MainApplication.getContext();

        File filesDir = context.getExternalFilesDir(null);///storage/sdcard0/Android/data/PackageName/files
        File destFile = new File(filesDir, filePath);
        Files.createParentDirs(destFile);

        if (!destFile.exists()) {
            InputStream inputStream = context.getAssets().open(filePath);
            FileOutputStream outputStream = new FileOutputStream(destFile);
            ByteStreams.copy(inputStream, outputStream);

            outputStream.close();
            inputStream.close();

        }
        return destFile;
    }

    /**
     * 获取外部存储目录的File对象
     *
     * @param filePath 文件路径 e.g. "image/a.jpg"
     * @return 目标文件存在返回File对象，否则返回null
     */
    public static File getEFDFile(String filePath) {
        if (!hasSDCardMounted()) return null;
        Context context = MainApplication.getContext();

        File filesDir = context.getExternalFilesDir(null);///storage/sdcard0/Android/data/PackageName/files
        File destFile = new File(filesDir, filePath);
        if (destFile.exists() && destFile.isFile()) {
            return destFile;
        }
        return null;
    }

}
