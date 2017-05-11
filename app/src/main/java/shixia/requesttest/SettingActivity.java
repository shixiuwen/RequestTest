package shixia.requesttest;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import shixia.requesttest.utils.SpUtils;
import shixia.requesttest.view.ApiView;
import shixia.requesttest.view.ParamsView;

/**
 * Created by ShiXiuwen on 2017/5/10.
 * <p>
 * Email:shixiuwen1991@yeah.net
 * Description:配置项，数据量不大，直接使用SP存储，没用到数据库
 */

public class SettingActivity extends AppCompatActivity {

    private Button btnPlusApi;
    private LinearLayout llApiMap;
    private RadioGroup rgDefaultRequestType;
    private RadioButton rbGet;
    private RadioButton rbPost;
    private Button btnPlusParams;
    private LinearLayout llParamsMap;
    private RadioGroup rgIsParse;
    private RadioButton rbYes;
    private RadioButton rbNo;
    private Button btnSave;

    private List<ApiView> apiViewList = new ArrayList<>();
    private List<ParamsView> paramsViewList = new ArrayList<>();

    private boolean isGet = false;      //默认post
    private boolean isParse = false;    //默认不自动复制

    private int apiCount = 0;
    private int paramsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btnPlusApi = (Button) findViewById(R.id.btn_plus_api);
        llApiMap = (LinearLayout) findViewById(R.id.ll_api_map);
        rgDefaultRequestType = (RadioGroup) findViewById(R.id.rg_default_request_type);
        rbGet = (RadioButton) findViewById(R.id.rb_get);
        rbPost = (RadioButton) findViewById(R.id.rb_post);
        btnPlusParams = (Button) findViewById(R.id.btn_plus_params);
        llParamsMap = (LinearLayout) findViewById(R.id.ll_params_map);
        rgIsParse = (RadioGroup) findViewById(R.id.rg_is_parse);
        rbYes = (RadioButton) findViewById(R.id.rb_yes);
        rbNo = (RadioButton) findViewById(R.id.rb_no);
        btnSave = (Button) findViewById(R.id.btn_save);

        initData();

        //添加默认api
        btnPlusApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiView apiView = new ApiView(SettingActivity.this);
                llApiMap.addView(apiView);
                apiViewList.add(apiView);
                apiCount++;
                setApiViewListener(apiView);
            }
        });

        btnPlusParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParamsView paramsView = new ParamsView(SettingActivity.this);
                llParamsMap.addView(paramsView);
                paramsViewList.add(paramsView);
                paramsCount++;

                paramsView.setOnDeleteClickListener(new ParamsView.OnDeleteParamsClickListener() {
                    @Override
                    public void onDeleteParamsClick() {
                        llParamsMap.removeView(paramsView);
                        paramsViewList.remove(paramsView);
                        paramsCount--;
                    }
                });
            }
        });

        rgDefaultRequestType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                isGet = checkedId == R.id.rb_get;
            }
        });

        rgIsParse.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                isParse = checkedId == R.id.rb_yes;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.保存api数据
                if (!saveApiData()) {
                    return;
                }
                //2.保存params数据
                if (!saveParamsData()) {
                    return;
                }
                //3.保存默认请求方式数据
                saveDefaultRequestTypeData();
                //4.保存是否复制到剪切板数据
                saveIsParseData();

                setResult(0);

                finish();
            }
        });

    }

    private void initData() {

        apiCount = SpUtils.getInt(this, SpUtils.API_COUNT, 0);
        paramsCount = SpUtils.getInt(this, SpUtils.PARAMS_COUNT, 0);

        for (int i = 0; i < apiCount; i++) {
            String api = SpUtils.getString(this, SpUtils.API_INDEX_ + i, "");
            ApiView apiView = new ApiView(SettingActivity.this);
            apiView.setEtApi(api);
            llApiMap.addView(apiView);
            apiViewList.add(apiView);
            setApiViewListener(apiView);

            if (TextUtils.equals(api, SpUtils.getString(SettingActivity.this, SpUtils.API_SELECTED, ""))) {
                apiView.setChecked(true);
            }
        }

        //默认参数
        for (int i = 0; i < paramsCount; i++) {
            String api = SpUtils.getString(this, SpUtils.PARAMS_KEY_VALUE_ + i, "");
            ParamsView paramsView = new ParamsView(SettingActivity.this);
            paramsView.setDelVisible(true);
            paramsView.setKeyAndValue(api);
            llParamsMap.addView(paramsView);
            paramsViewList.add(paramsView);
            setParamsViewListener(paramsView);
        }


        //默认请求方式
        String requestType = SpUtils.getString(this, SpUtils.DEFAILT_REQUEST_TYPE, "POST");
        if (TextUtils.equals(requestType, "POST")) {
            rgDefaultRequestType.check(R.id.rb_post);
        } else {
            rgDefaultRequestType.check(R.id.rb_get);
        }

        //是否复制到剪切板
        String isAuto = SpUtils.getString(this, SpUtils.AUTO_SAVE_TO_PLATE, "NO");
        if (TextUtils.equals(isAuto, "YES")) {
            rgIsParse.check(R.id.rb_yes);
        } else {
            rgIsParse.check(R.id.rb_no);
        }
    }

    private void setApiViewListener(final ApiView apiView) {
        apiView.setOnDeleteClickListener(new ApiView.OnApiClickListener() {
            @Override
            public void onDeleteApiClick() {
                llApiMap.removeView(apiView);
                apiViewList.remove(apiView);
                apiCount--;
                if (TextUtils.equals(apiView.getApi(), SpUtils.getString(SettingActivity.this, SpUtils.API_SELECTED, "null"))) {
                    SpUtils.put(SettingActivity.this, SpUtils.API_SELECTED, "");
                }
            }

            @Override
            public void onChangeSelectedApiClick(boolean isChecked) {
                if (isChecked) {
                    if (TextUtils.isEmpty(apiView.getApi())) {
                        apiView.setChecked(false);
                        Toast.makeText(SettingActivity.this, "请先输入api前缀", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SpUtils.put(SettingActivity.this, SpUtils.API_SELECTED, apiView.getApi());
                    for (ApiView view : apiViewList) {
                        if (view != apiView) {
                            view.setChecked(false);
                        }
                    }
                }
            }
        });
    }

    private void setParamsViewListener(final ParamsView paramsView) {
        paramsView.setOnDeleteClickListener(new ParamsView.OnDeleteParamsClickListener() {
            @Override
            public void onDeleteParamsClick() {
                llParamsMap.removeView(paramsView);
                paramsViewList.remove(paramsView);
                paramsCount--;
            }
        });
    }

    private boolean saveApiData() {
        for (int i = 0; i < apiCount; i++) {
            ApiView apiView = apiViewList.get(i);
            if (TextUtils.isEmpty(apiView.getApi())) {
                Toast.makeText(this, "请删除空的api前缀", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        for (int i = 0; i < apiCount; i++) {
            ApiView apiView = apiViewList.get(i);
            String api = apiView.getApi();
            SpUtils.put(this, SpUtils.API_INDEX_ + i, api);
        }
        SpUtils.put(this, SpUtils.API_COUNT, apiCount);
        return true;
    }

    private boolean saveParamsData() {
        for (int i = 0; i < paramsCount; i++) {
            ParamsView paramsView = paramsViewList.get(i);
            Map<String, String> params = paramsView.getParams();
            if (TextUtils.isEmpty(params.get("key"))) {
                Toast.makeText(this, "请删除键值为空的队列", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        for (int i = 0; i < paramsCount; i++) {
            ParamsView paramsView = paramsViewList.get(i);
            Map<String, String> params = paramsView.getParams();
            if (TextUtils.isEmpty(params.get("key"))) {
                break;
            }
            String value = TextUtils.isEmpty(params.get("value")) ? "null" : params.get("value");
            SpUtils.put(this, SpUtils.PARAMS_KEY_VALUE_ + i, params.get("key") + "_" + value);
        }
        SpUtils.put(this, SpUtils.PARAMS_COUNT, paramsCount);
        return true;
    }

    private void saveDefaultRequestTypeData() {
        SpUtils.put(this, SpUtils.DEFAILT_REQUEST_TYPE, isGet ? "GET" : "POST");
    }

    private void saveIsParseData() {
        SpUtils.put(this, SpUtils.AUTO_SAVE_TO_PLATE, isParse ? "YES" : "NO");
    }
}
