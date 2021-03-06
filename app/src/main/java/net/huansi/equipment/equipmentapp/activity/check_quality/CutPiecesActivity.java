package net.huansi.equipment.equipmentapp.activity.check_quality;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;


import net.huansi.equipment.equipmentapp.R;
import net.huansi.equipment.equipmentapp.activity.BaseActivity;

import net.huansi.equipment.equipmentapp.adapter.HsBaseAdapter;
import net.huansi.equipment.equipmentapp.entity.CutPiecesData;
import net.huansi.equipment.equipmentapp.entity.CutPiecesDetail;
import net.huansi.equipment.equipmentapp.entity.CutPiecesEntity;
import net.huansi.equipment.equipmentapp.entity.CutPiecesInfo;
import net.huansi.equipment.equipmentapp.entity.CutPiecesUtil;
import net.huansi.equipment.equipmentapp.entity.HsWebInfo;
import net.huansi.equipment.equipmentapp.listener.WebListener;
import net.huansi.equipment.equipmentapp.util.NewRxjavaWebUtils;
import net.huansi.equipment.equipmentapp.util.OthersUtil;
import net.huansi.equipment.equipmentapp.util.ViewHolder;
import net.huansi.equipment.equipmentapp.widget.LoadProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CutPiecesActivity extends BaseActivity {
    private HsWebInfo hsWebInfo=new HsWebInfo();
    private String vatNo="";//根据缸号去重
    private LoadProgressDialog dialog;
    private CutPiecesDetailAdapter mAdapter;
    private CutPiecesDataAdapter dataAdapter;
    private CutPiecesInfoAdapter infoAdapter;
    private List<CutPiecesDetail> mList;
    private List<CutPiecesData> dataList;
    private List<CutPiecesInfo> infoList;
    private CutPiecesDetail cutPiecesDetail;
    private CutPiecesData cutPiecesData;
    private CutPiecesInfo cutPiecesInfo;
    @BindView(R.id.etLaboonNumber)
    EditText etLaboonNumber;
    @BindView(R.id.btnLaboonSearch)
    Button btnLaboonSearch;
    @BindView(R.id.tvFEPO)
    TextView tvFEPO;
    @BindView(R.id.tvITEM)
    TextView tvITEM;
    @BindView(R.id.tvCOLOR)
    TextView tvCOLOR;
    @BindView(R.id.lvLaboonList)
    ListView lvLaboonList;
    @BindView(R.id.lvInfoList1)
    ListView lvInfoList1;
    @BindView(R.id.lvInfoList2)
    ListView lvInfoList2;
    @BindView(R.id.textile_mill_info)
    LinearLayout textile_mill_info;
    @BindView(R.id.MQP_info)
    LinearLayout MQPInfo;
    @BindView(R.id.btn_changeInfo)
    Button btn_changeInfo;
    private int REQUEST_CODE=1;
    private Boolean STATE=true;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_cut_pieces;
    }

    @Override
    public void init() {
        ZXingLibrary.initDisplayOpinion(this);
        setToolBarTitle("验片:品管MQP信息审视");
        dialog=new LoadProgressDialog(this);
        mList=new ArrayList<CutPiecesDetail>();
        dataList=new ArrayList<>();
        infoList=new ArrayList<>();
    }

    @OnClick(R.id.btn_changeInfo)
    void switchImage(){
        if (STATE==true){
            MQPInfo.setVisibility(View.GONE);
            textile_mill_info.setVisibility(View.VISIBLE);
            btn_changeInfo.setText("远纺信息");
            STATE=false;
        }else {
            MQPInfo.setVisibility(View.VISIBLE);
            textile_mill_info.setVisibility(View.GONE);
            btn_changeInfo.setText("本厂信息");
            STATE=true;
        }
    }
    @OnClick(R.id.openCamera)
    void openCamera(){
        Intent intent = new Intent(CutPiecesActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    getLaboonDetail(result);//本厂
                    getLaboonData(result);//外厂
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(CutPiecesActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    @OnClick(R.id.btnLaboonSearch)
    void searchLaboonDetail(){
        final String billNo = etLaboonNumber.getText().toString();
        getLaboonDetail(billNo);
        getLaboonData(billNo);
    }
    private void getLaboonData(final String result) {
        mList.clear();
        dataList.clear();
        infoList.clear();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(CutPiecesActivity.this, hsWebInfo)
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(),
                                        "spAppGetMQP_FEDZ",
                                        "sBillNo="+result,
                                        String.class.getName(),
                                        false,
                                        "helloWorld");
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                ,CutPiecesActivity.this, dialog, new WebListener() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "successData="+hsWebInfo.json);
                        String json = hsWebInfo.json;
                        CutPiecesEntity cutPiecesEntity = JSON.parseObject(json, CutPiecesEntity.class);
                        List<CutPiecesEntity.DATABean> data = cutPiecesEntity.getDATA();
                        tvFEPO.setText(data.get(0).getFEPO());
                        tvITEM.setText(data.get(0).getMATERIALCODE());
                        tvCOLOR.setText(data.get(0).getCOLORCODE());
                        for (int i=0;i< data.size();i++){
                            cutPiecesData=new CutPiecesData();
                            cutPiecesInfo=new CutPiecesInfo();
                            cutPiecesData.CHECKDATE=data.get(i).getCHECKDATE();
                            cutPiecesInfo.CHECKDATE=data.get(i).getCHECKDATE();
                            cutPiecesData.INFODATE=data.get(i).getINFODATE();
                            cutPiecesInfo.INFODATE=data.get(i).getINFODATE();
                            cutPiecesData.VATNO=data.get(i).getVATNO();
                            cutPiecesInfo.VATNO=data.get(i).getVATNO();
                            cutPiecesData.DIMENSIONALSTABILITY_LENGTH=data.get(i).getDIMENSIONALSTABILITY_LENGTH();
                            cutPiecesData.DIMENSIONALSTABILITY_WIDTH=data.get(i).getDIMENSIONALSTABILITY_WIDTH();
                            cutPiecesData.IRONINGSHRINKAGE_LENGTH=data.get(i).getIRONINGSHRINKAGE_LENGTH();
                            cutPiecesData.IRONINGSHRINKAGE_WIDTH=data.get(i).getIRONINGSHRINKAGE_WIDTH();
                            cutPiecesInfo.PNO=data.get(i).getPNO();
                            cutPiecesInfo.FLAW=data.get(i).getFLAW();
                            cutPiecesInfo.CLOTH_LEVEL=data.get(i).getCLOTH_LEVEL();
                            cutPiecesInfo.VATREMARK=data.get(i).getVATREMARK();
                            cutPiecesInfo.VATREMARK2=data.get(i).getVATREMARK2();
                            if (!data.get(i).getVATNO().equalsIgnoreCase(vatNo)){
                                vatNo=data.get(i).getVATNO();
                                dataList.add(cutPiecesData);
                            }
                            infoList.add(cutPiecesInfo);
                        }

                        dataAdapter=new CutPiecesDataAdapter(dataList,CutPiecesActivity.this);
                        infoAdapter=new CutPiecesInfoAdapter(infoList,CutPiecesActivity.this);
                        lvInfoList1.setAdapter(dataAdapter);
                        lvInfoList2.setAdapter(infoAdapter);
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "error外="+hsWebInfo.json);
                        //OthersUtil.showTipsDialog(CutPiecesActivity.this,"无此单号或网络可能有问题！");
                    }
                });

    }
    private void getLaboonDetail(final String result){
        mList.clear();
        dataList.clear();
        infoList.clear();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(CutPiecesActivity.this, hsWebInfo)
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                Log.e("TAG","upupDate");
                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(),
                                        "spAppGetMQP",
                                        "sBillNo="+result,
                                        String.class.getName(),
                                        false,
                                        "helloWorld");
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                ,CutPiecesActivity.this, dialog, new WebListener() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "successNum="+hsWebInfo.json);
                        String json = hsWebInfo.json;
                        CutPiecesUtil cutPiecesUtil = JSON.parseObject(json, CutPiecesUtil.class);
                        List<CutPiecesUtil.DATABean> data = cutPiecesUtil.getDATA();
                        tvFEPO.setText(data.get(0).getFEPO());
                        tvITEM.setText(data.get(0).getMATERIALCODE());
                        tvCOLOR.setText(data.get(0).getCOLORCODE());
                        for (int i=0;i<data.size();i++){
                            cutPiecesDetail=new CutPiecesDetail();
                            cutPiecesDetail.RECEIVEDATE=data.get(i).getRECEIVEDATE();
                            cutPiecesDetail.VATNO=data.get(i).getVATNO();
                            cutPiecesDetail.RESULT=data.get(i).getRESULT();
                            cutPiecesDetail.LEVEL=data.get(i).getLEVEL();
                            cutPiecesDetail.REMARK=data.get(i).getREMARK();
                            cutPiecesDetail.ABNORMALTYPE=data.get(i).getABNORMALTYPE();
                            mList.add(cutPiecesDetail);
                        }
                        mAdapter=new CutPiecesDetailAdapter(mList,CutPiecesActivity.this);
                        lvLaboonList.setAdapter(mAdapter);
                        hintKeyboard();
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "error本="+hsWebInfo.json);
                       // OthersUtil.showTipsDialog(CutPiecesActivity.this,"无此单号或网络可能有问题！");
                    }
                });
    }
    //关闭软键盘
    private void hintKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    private class CutPiecesDetailAdapter extends HsBaseAdapter<CutPiecesDetail> {
        public CutPiecesDetailAdapter(List<CutPiecesDetail> list, Context context) {
            super(list, context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            if (convertView==null)  convertView=mInflater.inflate(R.layout.cut_pieces_detail_item,viewGroup,false);
            TextView receiveDate = ViewHolder.get(convertView, R.id.receiveDate);
            TextView vatNo = ViewHolder.get(convertView, R.id.vatNo);
            TextView result = ViewHolder.get(convertView, R.id.result);
            TextView level = ViewHolder.get(convertView, R.id.level);
            TextView abNormalType = ViewHolder.get(convertView, R.id.abNormalType);
            TextView remark = ViewHolder.get(convertView, R.id.remark);
            CutPiecesDetail cutPiecesDetail = mList.get(position);
            receiveDate.setText(cutPiecesDetail.RECEIVEDATE);
            vatNo.setText(cutPiecesDetail.VATNO);
            result.setText(cutPiecesDetail.RESULT);
            level.setText(cutPiecesDetail.LEVEL);
            abNormalType.setText(cutPiecesDetail.ABNORMALTYPE);
            remark.setText(cutPiecesDetail.REMARK);
            return convertView;
        }
    }


    private class CutPiecesDataAdapter extends HsBaseAdapter<CutPiecesData>{
        public CutPiecesDataAdapter(List<CutPiecesData> list, Context context) {
            super(list, context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            if (convertView==null) convertView=mInflater.inflate(R.layout.cut_pieces_data_item,viewGroup,false);
            TextView checkData=ViewHolder.get(convertView,R.id.checkData);
            TextView infoData=ViewHolder.get(convertView,R.id.infoData);
            TextView vatNo=ViewHolder.get(convertView,R.id.vatNo);
            TextView dimensionalStability_Length=ViewHolder.get(convertView,R.id.dimensionalStability_Length);
            TextView dimensionalStability_Width=ViewHolder.get(convertView,R.id.dimensionalStability_Width);
            TextView ironingShrinkage_Length=ViewHolder.get(convertView,R.id.ironingShrinkage_Length);
            TextView ironingShrinkage_Width=ViewHolder.get(convertView,R.id.ironingShrinkage_Width);
            CutPiecesData cutPiecesData = mList.get(position);
            checkData.setText(cutPiecesData.CHECKDATE);
            infoData.setText(cutPiecesData.INFODATE);
            vatNo.setText(cutPiecesData.VATNO);
            dimensionalStability_Length.setText(cutPiecesData.DIMENSIONALSTABILITY_LENGTH);
            dimensionalStability_Width.setText(cutPiecesData.DIMENSIONALSTABILITY_WIDTH);
            ironingShrinkage_Length.setText(cutPiecesData.IRONINGSHRINKAGE_LENGTH);
            ironingShrinkage_Width.setText(cutPiecesData.IRONINGSHRINKAGE_WIDTH);
            return convertView;
        }
    }

    private class CutPiecesInfoAdapter extends HsBaseAdapter<CutPiecesInfo>{
        public CutPiecesInfoAdapter(List<CutPiecesInfo> list, Context context) {
            super(list, context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            if (convertView==null)  convertView=mInflater.inflate(R.layout.cut_pieces_info_item,viewGroup,false);
            TextView check_data=ViewHolder.get(convertView,R.id.check_data);
            TextView info_data=ViewHolder.get(convertView,R.id.info_data);
            TextView vat_no=ViewHolder.get(convertView,R.id.vat_no);
            TextView PNo=ViewHolder.get(convertView,R.id.PNo);
            TextView FLaw=ViewHolder.get(convertView,R.id.FLaw);
            TextView clothLevel=ViewHolder.get(convertView,R.id.clothLevel);
            TextView remark1=ViewHolder.get(convertView,R.id.remark1);
            TextView remark2=ViewHolder.get(convertView,R.id.remark2);
            CutPiecesInfo cutPiecesInfo = mList.get(position);
            check_data.setText(cutPiecesInfo.CHECKDATE);
            info_data.setText(cutPiecesInfo.INFODATE);
            vat_no.setText(cutPiecesInfo.VATNO);
            PNo.setText(cutPiecesInfo.PNO);
            FLaw.setText(cutPiecesInfo.FLAW);
            clothLevel.setText(cutPiecesInfo.CLOTH_LEVEL);
            remark1.setText(cutPiecesInfo.VATREMARK);
            remark2.setText(cutPiecesInfo.VATREMARK2);
            return convertView;
        }
    }
}
