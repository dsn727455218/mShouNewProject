package com.wp.baselib.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.MeasureSpec;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 综合工具类【APP包管理、加密/解密、view转换bitmap、copy到剪切板】等
 *
 * @author summer
 * @description
 * @date 2014年4月9日 下午2:04:35
 */
public class ToolsUtil {

    public static String CopyString(String sou, int startIndex, int len) {
        String s = "";
        if (sou.length() > startIndex) {
            if (sou.length() >= (startIndex + len)) {
                s = sou.substring(startIndex, startIndex + len);
            } else {
                s = sou.substring(startIndex, sou.length());
            }
        }
        return s;
    }

    // 根据行号取得字串内容
    public static String getFieldListStringByIndex(String[] fieldList, int index) {
        String res = "";
        if (fieldList.length > index) {
            res = fieldList[index].trim();
        }
        return res;
    }

    // 根据行号取得整型数据，为空时返回缺省值

    public static int getFieldListStringByIndexToInt(String[] fieldList, int index, int defaultValue) {
        int res = defaultValue;
        if (fieldList.length <= index) {
            res = defaultValue;
        }
        if ("".equals(fieldList[index].trim())) {
            res = defaultValue;
        } else {
            res = Integer.parseInt(fieldList[index].trim(), 10);
        }
        return res;
    }

    /*
     * 从字串列表中取出对应的值
     */
    public static String getStringFromArrayList(String[] list, String key) {
        String res = "";
        for (String value : list) {
            String[] sa = value.split("=");
            if (sa.length >= 2) {
                if (sa[0].equals(key)) {
                    res = sa[1];
                    break;
                }
            }
        }
        return res;
    }

    /*
     * 从字串中取出对应的分隔信息序号值
     */
    public static String getStringFromSplit(String v, String key, int index) {
        String res = "";
        if (index >= 0) {
            String[] list = v.split(key);
            if (list.length > index) {
                res = list[index];
            }
        }
        return res;
    }

    /**
     * 安装apk文件
     *
     * @param apkFile
     * @param context
     */
    public static void installAPK(File apkFile, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
        try {
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
        }
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 卸载apk文件
     *
     * @param packageName
     * @param context
     */
    public static void uninstallAPK(String packageName, Context context) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri data = Uri.parse("package:" + packageName);
        intent.setData(data);
        context.startActivity(intent);
    }

    public static int StrToIntDef(String s, int defaultValue) {
        int res = defaultValue;
        try {
            res = Integer.parseInt(s, 10);
        } catch (Exception e) {
            res = defaultValue;
        }
        return res;
    }

    private static long lastClickTime;

    /**
     * 判断在一定时间内范围点击重复点击
     *
     * @param milliseconds 毫秒数
     */
    public static boolean isFastDoubleClick(int milliseconds) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < milliseconds) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     * @throws InvalidAlgorithmParameterException
     * @throws Exception
     */
    public static String encode(String key, String data) {
        if (data == null)
            return null;
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec("12345678".getBytes()));
            byte[] bytes = cipher.doFinal(data.getBytes());
            return byte2hex(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES算法，解密
     *
     * @param data 待解密字符串
     * @param key  解密私钥，长度不能够小于8位
     * @return 解密后的字节数组
     * @throws Exception 异常
     */
    public static String decode(String key, String data) {
        if (data == null)
            return null;
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec("12345678".getBytes()));
            return new String(cipher.doFinal(hex2byte(data.getBytes())));
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * 二行制转字符串
     *
     * @param b
     * @return
     */
    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        if (b == null)
            return "";
        int length = b.length;
        for (int n = 0; n < length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

    private static byte[] hex2byte(byte[] b) {
        int length = b.length;
        if ((length % 2) != 0)
            throw new IllegalArgumentException();
        byte[] b2 = new byte[length / 2];
        for (int n = 0; n < length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * 通过ids生成JSONArray
     *
     * @param ids
     * @return
     */
    public static JSONArray createJsonArrayByIds(int[] ids) {
        JSONArray jsonArray = new JSONArray();
        int length = ids.length;
        for (int i = 0; i < length; i++) {
            try {
                jsonArray.put(i, ids[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    /**
     * 格式化id数组
     *
     * @param ids
     * @return
     */
    public static JSONArray formatIdsToJsonObject(int[] ids) {
        JSONArray param = new JSONArray();
        try {
            int count = ids.length;
            for (int i = 0; i < count; i++) {
                param.put(i, ids[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param;
    }

    /**
     * 格式化id数组
     *
     * @param ids
     * @return
     */
    public static JSONArray formatIdsToJsonObject(String[] ids) {
        JSONArray param = new JSONArray();
        try {
            int count = ids.length;
            for (int i = 0; i < count; i++) {
                param.put(i, ids[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param;
    }

    /**
     * jsonArray转换成数组
     *
     * @param array
     * @return
     * @throws JSONException
     */
    public static int[] formatJSONArrayToIds(JSONArray array) throws JSONException {
        int length = array.length();
        int[] ids = new int[length];
        for (int i = 0; i < length; i++) {
            ids[i] = array.getInt(i);
        }
        return ids;
    }

    /**
     * 获取本地安装的应用 不包含系统的应用
     *
     * @param Context 上下文
     * @return
     */
    public static ArrayList<PackageInfo> getLocalAppList(Context context) {

        PackageManager manager = context.getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);
        ArrayList<PackageInfo> packageInfos = new ArrayList<PackageInfo>();
        for (PackageInfo packageInfo : pkgList) {
            int flags = packageInfo.applicationInfo.flags;
            int icon = packageInfo.applicationInfo.icon;
            if (icon != 0) { //获取有图标的APP及有运行LAUNCHER的
                if ((flags & ApplicationInfo.FLAG_SYSTEM) <= 0) //获取 非系统的应用
                {
                    packageInfos.add(packageInfo);
                } else if ((flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {// 本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
                    packageInfos.add(packageInfo);
                }

            }
        }
        return packageInfos;
    }

    /**
     * 将视图转换为bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    /**
     * copy字符串到剪贴板
     *
     * @param context
     * @param res_icon 成功的图标
     * @param str      待复制的字符串
     * @param msg      自定义消息
     */
    public static void copyClipBoard(final Context context, final int res_icon, String str, final String msg) {

        if (VERSION.SDK_INT >= 11) {
            ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            AlertUtil.waringToast(context, res_icon, msg);
            cmb.setText(str);
        } else {
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(str);
            AlertUtil.waringToast(context, res_icon, msg);
        }

    }

    /**
     * 将实体转化Hashmap
     *
     * @param bean 待转化的实体类
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static HashMap<String, String> convertBeanToMap(Object bean) throws IllegalArgumentException, IllegalAccessException {
        Class<?> object = bean.getClass();
        HashMap<String, String> data = new HashMap<String, String>();
        //父类字段
        Field[] superFields = object.getSuperclass().getDeclaredFields();
        for (Field field : superFields) {
            field.setAccessible(true);
            if (field.get(bean) != null) {
                data.put(field.getName(), String.valueOf(field.get(bean)));
            }
        }

        //子类字段
        Field[] fields = object.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.get(bean) != null) {
                data.put(field.getName(), String.valueOf(field.get(bean)));
            }
        }
        return data;
    }
}
