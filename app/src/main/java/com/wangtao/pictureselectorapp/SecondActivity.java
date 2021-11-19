package com.wangtao.pictureselectorapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Filter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cmcy.medialib.utils.MediaSelector;
import com.xiaosu.lib.permission.OnRequestPermissionsCallBack;
import com.xiaosu.lib.permission.PermissionCompat;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.bzcoder.mediapicker.SmartMediaPicker;
import me.bzcoder.mediapicker.config.MediaPickerEnum;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG ="SecondActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.load).setOnClickListener(this);
        initPermission();
    }
    private void initPermission()
    {
        PermissionCompat.create(this)
                .permissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .explain("相机解释", "存储解释","录音解释")
                .retry(true)
                .callBack(new OnRequestPermissionsCallBack() {
                    @Override
                    public void onGrant() {
                        // todo 权限授权成功回调
                    }

                    @Override
                    public void onDenied(String permission, boolean retry) {
                        // todo 权限授权失败回调
                    }
                })
                .build()
                .request();
    }
    @Override
    public void onClick(View v) {
        //selectPic();
        selectVideo();
    }
    private final int REQUEST_CODE_CHOOSE_PHOTO_ALBUM = 1;
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO_ALBUM && resultCode == RESULT_OK) {
            //图片路径 同样视频地址也是这个 根据requestCode
            List<Uri> pathList = Matisse.obtainResult(data);
            for (Uri _Uri : pathList) {

                Log.e(TAG, _Uri.getPath());
            }
        }
    }*/

    void selectPic()
    {
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
                .capture(true)  // 使用相机，和 captureStrategy 一起使用
                .captureStrategy(new CaptureStrategy(true, "com.wangtao.pictureselectorapp"))
//        R.style.Matisse_Zhihu (light mode)
//        R.style.Matisse_Dracula (dark mode)
                .theme(R.style.Matisse_Dracula)
                .countable(true)
                .maxSelectable(1)
                .addFilter(new com.zhihu.matisse.filter.Filter() {
                    @Override
                    protected Set<MimeType> constraintTypes() {
                        return new HashSet<MimeType>() {{
                            add(MimeType.PNG);
                        }};
                    }


                    @Override
                    public IncapableCause filter(Context context, Item item) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(item.getContentUri());
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeStream(inputStream, null, options);
                            int width = options.outWidth;
                            int height = options.outHeight;

//                            if (width >= 500)
//                                return new IncapableCause("宽度超过500px");

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }


                        return null;
                    }
                })
//                .gridExpectedSize((int) getResources().getDimension(R.dimen.imageSelectDimen))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.87f)
                .imageEngine(new GlideLoadEngine())
                .forResult(REQUEST_CODE_CHOOSE_PHOTO_ALBUM);
    }

    public void selectVideo(){
         /*SmartMediaPicker.builder(this)
                //最大图片选择数目
                .withMaxImageSelectable(5)
                //最大视频选择数目
                .withMaxVideoSelectable(1)
                //图片选择器是否显示数字
                .withCountable(true)
                //最大视频长度
                .withMaxVideoLength(15 * 1000)
                //最大视频文件大小 单位MB
                .withMaxVideoSize(1)
                //最大图片高度 默认1920
                .withMaxHeight(1920)
                //最大图片宽度 默认1920
                .withMaxWidth(1920)
                //最大图片大小 单位MB
                .withMaxImageSize(5)
                //设置图片加载引擎
                .withImageEngine(new Glide4Engine())
                //弹出类别，默认弹出底部选择栏，也可以选择单独跳转
                .withMediaPickerType(MediaPickerEnum.BOTH)
                .build()
                .show();*/
       /* MediaSelector.get()

                .showCamera(true)//默认显示，可以不用设置

                .setSelectMode(MediaSelector.MODE_MULTI)//默认多选

                .setMaxCount(20)//默认最多选择5张，设置单选后此设置无效

                .setMediaType(MediaSelector.VIDEO)//默认选择图片

                //.setDefaultList(imageAdapter.getSelect())//默认选中的图片/视频

                .setListener(new MediaSelector.MediaSelectorListener() {
                    @Override
                    public void onMediaResult(List<String> resultList) {

                    }
                }).jump(this);//选择完成的回调, (可以设置回调或者用onActivityResult方式接收)*/
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1000);
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MediaSelector.REQUEST_IMAGE && resultCode == RESULT_OK){
            List resultList = data.getStringArrayListExtra(MediaSelector.EXTRA_RESULT);

            Log.e("TAG", "size-->" + resultList.size());

        }else if(requestCode == 1000 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            MediaExtraBean bean = new MediaUtils().getRingDuring(this, uri);
            Log.e(TAG,"thumbPath="+bean.thumbPath+"  imagePath="+bean.imagePath+"  localPath="+bean.localPath);
        }

    }


}