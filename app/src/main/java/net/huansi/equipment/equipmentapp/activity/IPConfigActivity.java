package net.huansi.equipment.equipmentapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import net.huansi.equipment.equipmentapp.R;
import net.huansi.equipment.equipmentapp.activity.move_cloth.ClothRegisterActivity;
import net.huansi.equipment.equipmentapp.entity.HsWebInfo;
import net.huansi.equipment.equipmentapp.entity.VersionEntity;
import net.huansi.equipment.equipmentapp.listener.WebListener;
import net.huansi.equipment.equipmentapp.util.NewRxjavaWebUtils;
import net.huansi.equipment.equipmentapp.util.OthersUtil;
import net.huansi.equipment.equipmentapp.util.PermissionsChecker;
import net.huansi.equipment.equipmentapp.util.SPHelper;
import net.huansi.equipment.equipmentapp.widget.LoadProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static net.huansi.equipment.equipmentapp.constant.Constant.PERMISSIONS;
import static net.huansi.equipment.equipmentapp.util.SPHelper.IP_KEY;
import static net.huansi.equipment.equipmentapp.util.SPHelper.ROLE_CODE_KEY;
import static net.huansi.equipment.equipmentapp.util.SPHelper.USER_NO_KEY;

/**
 * Created by shanz on 2017/3/10.
 */

public class IPConfigActivity extends BaseActivity {
    @BindView(R.id.spIPConfig)
    Spinner spIPConfig;
//    @BindView(R.id.btnIPConfigOk)
//    Button btnIPConfigOk;
    private PermissionsChecker mPermissionsChecker;
    private List<String> mLoginAreaList;
    private ArrayAdapter<String> mLoginAdapter;
    private int REQUEST_INSTALL_CODE=1;
    public int SHOW_ERROR=404;
    private LoadProgressDialog dialog;
    @Override
    protected int getLayoutId() {
        return R.layout.ip_config_activity;
    }

    @Override
    public void init() {
        //etIPConfig.setText(SPHelper.getLocalData(getApplicationContext(),IP_KEY,String.class.getName(),"").toString());
        mPermissionsChecker = new PermissionsChecker(this);
        dialog=new LoadProgressDialog(this);
        mLoginAreaList=new ArrayList<>();
        mLoginAreaList.add("苏州");
        mLoginAreaList.add("越南");
        mLoginAdapter=new ArrayAdapter<>(getApplicationContext(),R.layout.string_item,R.id.text,mLoginAreaList);
        spIPConfig.setAdapter(mLoginAdapter);
//        for (int i=0;i<mLoginAreaList.size();i++){
//            spIPConfig.setSelection(i);
//        }
    }

    @OnClick(R.id.btnIPConfigOk)
    void saveIP(){
        switch (mLoginAreaList.get(spIPConfig.getSelectedItemPosition())){
            case "苏州":
                //SPHelper.saveLocalData(getApplicationContext(),IP_KEY,"10.17.215.203:8064",String.class.getName());//越南
                SPHelper.saveLocalData(getApplicationContext(),IP_KEY,"10.17.111.23:8064",String.class.getName());//苏州
                break;
            case "越南":
                SPHelper.saveLocalData(getApplicationContext(),IP_KEY,"10.17.215.203:8064",String.class.getName());//越南
                //SPHelper.saveLocalData(getApplicationContext(),IP_KEY,"10.17.111.23:8064",String.class.getName());//苏州
                break;
        }
        //startMainActivity();

        //检查版本更新
        String localVersionName = OthersUtil.getLocalVersionName(this);
        OthersUtil.showLoadDialog(dialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this,"")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(getApplicationContext(),"spAPPGet_APPMaxVersion", ""
                                        ,String.class.getName(),false,"版本获取成功");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()), getApplicationContext(), dialog, new WebListener() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                Log.e("TAG","successVersion="+hsWebInfo.json);
                String json = hsWebInfo.json;
                OthersUtil.dismissLoadDialog(dialog);
                VersionEntity versionEntity = JSON.parseObject(json, VersionEntity.class);
                List<VersionEntity.DATABean> data = versionEntity.getDATA();
                int version = Integer.parseInt(data.get(0).getVERSION());
                int localVersion = OthersUtil.getLocalVersion(getApplicationContext());
                if (localVersion<version){
                    AlertDialog.Builder builder = new AlertDialog.Builder(IPConfigActivity.this);// 自定义对话框
                    builder.setIcon(R.drawable.app_icon);
                    builder.setTitle("检测到新的更新");
                    builder.setCancelable(false);
                    builder.setMessage(data.get(0).getUPDATELOG().toString());
                    builder.setPositiveButton("确认更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            downLoadApk();
                        }
                    })
                            .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startMainActivity();
                                }
                            }).create();
                    builder.show();
                }else {
                    startMainActivity();
                }
            }
            @Override
            public void error(HsWebInfo hsWebInfo) {
                Log.e("TAG","errorVersion="+hsWebInfo.json);
                OthersUtil.ToastMsg(IPConfigActivity.this,"数据传输失败检查网络连接");
            }
        });


    }
    private void downLoadApk() {
        //显示下载进度
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();
        //访问网络下载apk
        new Thread(new DownloadApk(dialog)).start();
    }


    /**
     * 下载完成,提示用户安装
     */
    private void installApk(File file) {
        //调用系统安装程序
        Uri data;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            //6.0以后
            data=FileProvider.getUriForFile(this, "net.huansi.equipment.equipmentapp.fileprovider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else {
            data = Uri.fromFile(file);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        this.startActivity(intent);


//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        startActivityForResult(intent, REQUEST_INSTALL_CODE);
    }
    private void startMainActivity() {
        String userNo=SPHelper.getLocalData(getApplicationContext(),USER_NO_KEY,String.class.getName(),"").toString();
        String roleCode=SPHelper.getLocalData(getApplicationContext(),ROLE_CODE_KEY,String.class.getName(),"").toString();
        Log.e("TAG","userNo="+userNo);
        Log.e("TAG","roleCode="+roleCode);
        Intent intent=new Intent();
        if(userNo.isEmpty()||roleCode.isEmpty()) {
            intent.setClass( this,LoginActivity.class);
        }else {
            intent.setClass( this,MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
            return;
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, 0, PERMISSIONS);
    }


    /**
     * 访问网络下载apk
     */
    public class DownloadApk implements Runnable {
        private ProgressDialog dialog;
        InputStream is;
        FileOutputStream fos;

        public DownloadApk(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            String url = OthersUtil.encodeUrl("http://10.17.111.23:8064/apk/CN_Equipment.apk");
            Request request = new Request.Builder().get().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.d("TAG", "开始下载apk");
                    //获取内容总长度
                    long contentLength = response.body().contentLength();
                    //设置最大值
                    dialog.setMax((int) contentLength);
                    dialog.setIcon(R.drawable.app_icon);
                    //保存到sd卡

                    File apkFile = new File(Environment.getExternalStorageDirectory().getPath(), "CN_Equipment" + ".apk");
                    fos = new FileOutputStream(apkFile);
                    //获得输入流
                    is = response.body().byteStream();
                    //定义缓冲区大小
                    byte[] bys = new byte[1024];
                    int progress = 0;
                    int len = -1;
                    while ((len = is.read(bys)) != -1) {
                        try {
                            Thread.sleep(1);
                            fos.write(bys, 0, len);
                            fos.flush();
                            progress += len;
                            //设置进度
                            dialog.setProgress(progress);
                        } catch (InterruptedException e) {
                            Message msg = Message.obtain();
                            msg.what = SHOW_ERROR;
                            msg.obj = "ERROR:10002解析错误";
                            handler.sendMessage(msg);
                        }
                    }
                    //下载完成,提示用户安装
                    installApk(apkFile);
                }
            } catch (IOException e) {
                Message msg = Message.obtain();
                msg.what = SHOW_ERROR;
                msg.obj = "ERROR:10003网络错误";
                handler.sendMessage(msg);
            } finally {
                //关闭io流
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    is = null;
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fos = null;
                }
            }
            dialog.dismiss();
        }

        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    /** 提示错误    */
                    case 404:
                        Toast.makeText(getApplicationContext(), msg.obj+"", Toast.LENGTH_LONG).show();
                        break;

                    default:
                        break;
                }
            }
        };
        /**
         * 下载完成,提示用户安装
         */
//    private void installApk(File file) {
//        //调用系统安装程序
//        Intent intent = new Intent();
//        intent.setAction("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        startActivityForResult(intent, REQUEST_INSTALL_CODE);
//    }
    }
}
