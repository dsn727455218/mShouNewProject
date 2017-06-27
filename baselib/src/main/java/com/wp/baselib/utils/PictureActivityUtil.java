package com.wp.baselib.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.webkit.ValueCallback;
import android.widget.Toast;

import com.wp.baselib.Config;
import com.wp.baselib.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 拍照工具类
 * <p>方法：initCutSize裁剪设置（可选）</p>
 * <p>图片入口方法：doPickPhotoAction启动图片和相册筛选</p>
 * <p>调用该类须从在activity的onActivityResult获取返回数据，回调数据实例：</p>
 * <pre>
 * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 * 		super.onActivityResult(requestCode, resultCode, data);
 * 		if (resultCode == RESULT_OK) {
 * 			switch (requestCode) {
 * 			case PictureActivityUtil.CAMERA_WITH_DATA: // 拍照返回
 * 				PictureActivityUtil.doCropPhoto(UserInfoActivity.this, PictureActivityUtil.CAPTURE_IMAGE_TARGET_PATH);
 * 				break;
 * 			case PictureActivityUtil.PHOTO_PICKED_WITH_DATA: // 选择相册返回
 * 				String path = PictureActivityUtil.getPickPhotoPath(UserInfoActivity.this, data);
 * 				PictureActivityUtil.doCropPhoto(UserInfoActivity.this, path);
 * 				break;
 * 			case PictureActivityUtil.PHOTO_CROP: // 裁剪图片返回
 * 				// 获取图片裁剪后路径
 * 				String cropPath = Uri.parse(data.getAction()).getPath();
 * 				break;
 *            }
 * }
 *    }
 * 	</pre>
 *
 * @author summer
 * @date 2014年4月30日 下午3:17:26
 */
public class PictureActivityUtil {
    private static final String TAG = "PictureActivityUtil";
    /**
     * 获取图片压缩后保存的路径
     */
    private static final String COMPRESS_IMAGE_TARGET_DIR = FileUtil.getSDRootPath() + Config.CACHE_FILEDIR_PIC;
    /**
     * 获取照相机保存图片路径
     */
    public static final String CAPTURE_IMAGE_TARGET_PATH = FileUtil.getSDRootPath() + Config.CACHE_FILEDIR_PIC + "caputure_temp.jpg";

    public static final String CROP_IMAGE_TARGET_PATH = FileUtil.getSDRootPath() + Config.CACHE_FILEDIR_PIC;

    /**
     * 用来标识请求照相功能的requestCode
     */
    public static final int CAMERA_WITH_DATA = 168;
    /**
     * 用来标识请求相册的requestCode
     */
    public static final int PHOTO_PICKED_WITH_DATA = 169;
    /**
     * 用来标示请求图片裁剪 requestCode
     */
    public static final int PHOTO_CROP = 170;

    private static int cut_w; // 裁剪宽度
    private static int cut_h; // 裁剪长度
    private static int mAspectX, mAspectY; //裁剪比例
    private Context mContext;

    public PictureActivityUtil(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 设置裁剪图片尺寸
     *
     * @param w
     * @param h
     */
    public static void initCutSize(int w, int h) {
        cut_w = w;
        cut_h = h;
    }

    /**
     * 设置裁剪图片尺寸和比例
     *
     * @param w       尺寸
     * @param h
     * @param aspectX 比例
     * @param aspectY
     */
    public static void initCutSize(int w, int h, int aspectX, int aspectY) {
        cut_w = w;
        cut_h = h;
        mAspectX = aspectX;
        mAspectX = aspectY;
    }

    static ValueCallback<Uri> mUploadMsg;
    static ValueCallback<Uri[]> mUploadMsgs;

    public static void restoreUploadMsg() {
        if (mUploadMsg != null) {
            mUploadMsg.onReceiveValue(null);
            mUploadMsg = null;
        }
        if (mUploadMsgs != null) {
            mUploadMsgs.onReceiveValue(null);
            mUploadMsgs = null;
        }
    }


    /**
     * 拍照获取图片
     *
     * @param context
     */
    public static void doTakePhoto(Activity context) {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
            ToastUtil.showToast("没有找到SD卡或者正在使用请关闭usb连接模式");
            return;
        }
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(CAPTURE_IMAGE_TARGET_PATH)));
            context.startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (Exception e) {
            if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA}, 0);
            }

        }


    }


    /**
     * 调用相册查看图片
     *
     * @param context
     */
    public static void doPickPhotoFromGallery(Activity context) {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
            ToastUtil.showToast("没有找到SD卡或者正在使用请关闭usb连接模式");
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            context.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (Exception e) {
            Toast.makeText(context, R.string.photoPickerNotFoundText1, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    /**
     * 获取选择图片路径
     *
     * @param activity
     * @param data
     * @return path
     */
    public static String getPickPhotoPath(Activity activity, Intent data) {
        String path = "";
        Uri imageuri = data.getData();
        if (null != imageuri && imageuri.getScheme().compareTo("file") == 0) {
            path = imageuri.toString().replace("file://", "");
        } else {
            if (imageuri != null) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = activity.managedQuery(imageuri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    path = cursor.getString(column_index);
                }
            }
        }
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 压缩图片到指定大小
     *
     * @param srcPath    返回压缩后图片路径
     * @param targetPath 目标路径
     * @param maxSize    图片允许最大空间 单位：KB(有一定的出入)
     * @return 成功返回true，否则返回false
     */
    private static boolean compressImage(String srcPath, String targetPath, int maxSize) {
        //解决部分手机旋转问题
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath);
        int degree = readPictureDegree(srcPath);
        if (bitmap == null)
            return false;
        bitmap = toturn(bitmap, degree);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap = comp(bitmap);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            int bitmapSize = b.length;
            File file = new File(targetPath);
            FileOutputStream out = new FileOutputStream(file);
            if (bitmapSize > 1024 * 100) {
                // 将字节换成KB
                double mid = bitmapSize / 1024;
                // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
                int options = 100;
                while (mid > maxSize) {
                    baos.reset();
                    if (options <= 0) {
                        options = 100;
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    b = baos.toByteArray();
                    mid = b.length / 1024;
                    options -= 10;
                }
                baos.flush();
                baos.close();
                double i = mid / maxSize;
                bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i), bitmap.getHeight() / Math.sqrt(i));
                if (mid < maxSize) {
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, options == 100 ? options : options + 10, out)) {
                        out.flush();
                        out.close();
                    }
                }
            } else {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                    out.flush();
                    out.close();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 压缩图片到指定大小
     *
     * @param bitmap  源bitmap
     * @param maxSize 图片允许最大空间 单位：KB
     * @return
     */
    public static Bitmap compressImage(Bitmap bitmap, int maxSize) {
        Bitmap resBitmap = bitmap;
        // 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        // 将字节换成KB
        double mid = b.length / 1024;
        // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        int options = 100;
        System.out.println("======压缩之前的图片的bitmap大小：" + mid + "kb");
        while (mid > maxSize) {
            baos.reset();
            resBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            byte[] bytes = baos.toByteArray();
            mid = bytes.length / 1024;
            options -= 10;
        }
        System.out.println("=======压缩之后的图片的bitmap大小：" + mid + "kb");

        double i = mid / maxSize;
        return zoomImage(resBitmap, resBitmap.getWidth() / Math.sqrt(i), resBitmap.getHeight() / Math.sqrt(i));
    }

    /***
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    private static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        if (width <= 0 || height <= 0) {
            return bgimage;
        }
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    /**
     *
     * @param context
     * @param resourcePath图片路径
     *
     */
    /**
     * 开始压缩图片线程 要上传图片放到回调里去处理
     *
     * @param resourcePath 原图地址
     * @param maxSize      压多大 单位是kb
     * @param icr          回调接口
     */
    public void startCrompressImageTask( String resourcePath, int maxSize, ICompressResult icr) {
        CompressImageTask imageTask = new CompressImageTask( icr, maxSize);
        imageTask.execute(resourcePath);
    }

    class CompressImageTask extends AsyncTask<String, Void, String> {

        ICompressResult icr;
        int maxSize;

        public CompressImageTask( ICompressResult icr, int maxSize) {
            this.icr = icr;
            this.maxSize = maxSize;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            AlertUtil.closeProgressDialog();
            if (result != null && icr != null) {
                icr.onSuccus(result);
                this.cancel(true);
            } else {
                ToastUtil.showToast("图片压缩失败");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlertUtil.showProgressDialog(mContext, "", "正在压缩图片...");
        }

        @Override
        protected String doInBackground(String... params) {
            String path = params[0];
            if (path == null) {
                return null; // 裁剪失败
            }
            String compressPath = getCompressPath();
            boolean isSuccess = PictureActivityUtil.compressImage(path, compressPath, maxSize);
            if (isSuccess) {
                return compressPath;
            }
            return null;
        }

    }

    public static String getCompressPath() {
        return COMPRESS_IMAGE_TARGET_DIR + ".compress_temp.jpg";
    }

//    public static String getCompressPath(Context context) {
//        return context.getCacheDir() + "/compress/compress_temp.jpg";
//    }

    /**
     * 获取图片裁剪路径
     *
     * @return
     */
    public static String getCropPath() {
        return CROP_IMAGE_TARGET_PATH + System.currentTimeMillis() + "_crop_temp.jpg";
    }

    /**
     * 压缩回调接口
     *
     * @author summer
     */
    public interface ICompressResult {
        /**
         * 压缩成功才调用该方法
         *
         * @param path 压缩后的图片路径
         */
        void onSuccus(String path);
    }

    /**
     *   * 读取照片exif信息中的旋转角度
     *   * @param path 照片路径
     *   * @return角度
     *   
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return degree;
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param img
     * @return
     */
    public static Bitmap toturn(Bitmap img, int x) {
        Matrix matrix = new Matrix();
        matrix.postRotate(+x); /*翻转90度*/
        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }

    /**
     * 带处理角度问题的方法
     *
     * @param context
     * @param mImageCaptureUri
     * @return
     */
    private Bitmap getBitmap(Context context, Uri mImageCaptureUri) {

        // 不管是拍照还是选择图片每张图片都有在数据中存储也存储有对应旋转角度orientation值
        // 所以我们在取出图片是把角度值取出以便能正确的显示图片,没有旋转时的效果观看

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(mImageCaptureUri, null, null, null, null);// 根据Uri从数据库中找
        if (cursor != null) {
            cursor.moveToFirst();// 把游标移动到首位，因为这里的Uri是包含ID的所以是唯一的不需要循环找指向第一个就是了
            String filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取图片路
            String orientation = cursor.getString(cursor.getColumnIndex("orientation"));// 获取旋转的角度
            cursor.close();
            if (filePath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);//根据Path读取资源图片
                int angle = 0;
                if (orientation != null && !"".equals(orientation)) {
                    angle = Integer.parseInt(orientation);
                }
                if (angle != 0) {
                    // 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
                    Matrix m = new Matrix();
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    m.setRotate(angle); // 旋转angle度
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片

                }
                return bitmap;
            }
        }
        return null;
    }

    /**
     * 图片按比例大小压缩方法（根据路径获取图片并压缩）：
     *
     * @param srcPath
     * @return
     */
    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;
    }

    /**
     * 图片按比例大小压缩方法（根据Bitmap图片压缩）：
     */
    private static Bitmap comp(Bitmap image) {
        ByteArrayOutputStream baos = null;
        Bitmap bitmap = null;
        try {
            baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
                baos.reset();//重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            //开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            float hh = 800f;//这里设置高度为800f
            float ww = 480f;//这里设置宽度为480f
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;//be=1表示不缩放
            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0)
                be = 1;
            newOpts.inSampleSize = be;//设置缩放比例
            //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            isBm = new ByteArrayInputStream(baos.toByteArray());
            bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return bitmap;
    }

    @NonNull
    public static File roateBitmap(String filePath) {
        File file = new File(filePath);
        int degree = PictureActivityUtil.readPictureDegree(file.getAbsolutePath());
        if (degree != 0) {
            Bitmap bitmap = PictureActivityUtil.toturn(BitmapFactory.decodeFile(file.getAbsolutePath()), degree);
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                    try {
                        bitmap.recycle();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
