package com.wp.baselib.utils;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

/**
 * 缓存工具类
 *
 * @author summer
 *         时间： 2013-8-15 下午3:11:50
 */
public class CacheUtil {

    Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
    Context context;
    /**
     * 设置缓存路径默认  sdcard/cachedata
     */
    public static final String CACHEDATAPATH = "/cachedata/";
    public long CACHE_TIME = 10 * 60000;//默认缓存失效时间

    public CacheUtil(Context context) {
        this.context = context;
    }

    /**
     * 判断缓存是否失效
     *
     * @param cachefile
     * @return
     */
    public boolean isCacheDataFailure(String cachefile) {
        boolean failure = false;
        File data = new File(FileUtil.getCachePath(context, CACHEDATAPATH), cachefile);
        if (data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
            failure = true;
        else if (!data.exists())
            failure = true;
        return failure;
    }

    /**
     * 将对象保存到内存缓存中
     *
     * @param key
     * @param value
     */
    public synchronized void setMemCache(String key, Object value) {
        memCacheRegion.put(key, value);
    }

    /**
     * 从内存缓存中获取对象
     *
     * @param key
     * @return
     */
    public synchronized Object getMemCache(String key) {
        return memCacheRegion.get(key);
    }

    /**
     * 保存磁盘缓存
     *
     * @param key
     * @param value
     * @throws IOException
     */
    public synchronized void setDiskCache(String key, String value) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(FileUtil.getCachePath(context, CACHEDATAPATH), key));
            fos.write(value.getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取磁盘缓存数据
     *
     * @param key
     * @return
     */
    public synchronized String getDiskCache(String key) {
        BufferedInputStream bis = null;
        try {
            File file = new File(FileUtil.getCachePath(context, CACHEDATAPATH), key);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                byte[] datas = new byte[1024];
                StringWriter sw = new StringWriter();
                int length = -1;
                while ((length = bis.read(datas)) != -1) {
                    sw.write(new String(datas, 0, length));
                }
                return sw.toString();
            }
        } catch (FileNotFoundException f) {
            f.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null)
                    bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 保存对象
     *
     * @param ser
     * @param file
     * @throws IOException
     */
    public synchronized boolean saveObject(Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(new File(FileUtil.getCachePath(context, CACHEDATAPATH), file));
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取对象
     *
     * @param file
     * @return
     * @throws IOException
     */
    public Object readObject(String file) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        BufferedInputStream bis = null;

        try {
            fis = new FileInputStream(new File(FileUtil.getCachePath(context, CACHEDATAPATH), file));
            bis = new BufferedInputStream(fis);
            ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 处理key过长
     * 获取MD5加密过的key
     *
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String keyMd5(String key) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] oldBytes = key.getBytes();
        md.update(oldBytes, 0, oldBytes.length);
        byte[] bytes = md.digest();
        int i;
        StringBuilder buf = new StringBuilder("");
        for (byte aByte : bytes) {
            i = aByte;
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }
}
