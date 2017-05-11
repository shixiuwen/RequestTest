package shixia.requesttest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import shixia.requesttest.http.BaseHttpApi;
import shixia.requesttest.http.BaseHttpRequest;
import shixia.requesttest.utils.SpUtils;
import shixia.requesttest.view.LoadingDialog;
import shixia.requesttest.view.ParamsView;

public class MainActivity extends AppCompatActivity {

    private Context context = this;

    private static final int JSON_INDENT = 4;

    private Button btnSetting;
    private EditText etApi;
    private RadioGroup rgRequestType;
    private LinearLayout llParams;
    private Button btnPlusParams;
    private LinearLayout llParamsMap;
    private Button btnRequest;
    private Button btnClear;

    private LinearLayout llResult;
    private TextView tvResult;

    private List<ParamsView> paramsViewList = new ArrayList<>();

    private int paramsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSetting = (Button) findViewById(R.id.btn_setting);
        etApi = (EditText) findViewById(R.id.et_api);
        rgRequestType = (RadioGroup) findViewById(R.id.rg_request_type);
        llParams = (LinearLayout) findViewById(R.id.ll_params);
        btnPlusParams = (Button) findViewById(R.id.btn_plus_params);
        llParamsMap = (LinearLayout) findViewById(R.id.ll_params_map);
        btnRequest = (Button) findViewById(R.id.btn_request);
        btnClear = (Button) findViewById(R.id.btn_clear);

        llResult = (LinearLayout) findViewById(R.id.ll_result);
        tvResult = (TextView) findViewById(R.id.tv_result);

        initDate();

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        rgRequestType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rb_get) {
                    llParams.setVisibility(View.GONE);
                } else {
                    llParams.setVisibility(View.VISIBLE);
                }
            }
        });

        btnPlusParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParamsView paramsView = new ParamsView(context);
                llParamsMap.addView(paramsView);
                paramsViewList.add(paramsView);

                paramsView.setOnDeleteClickListener(new ParamsView.OnDeleteParamsClickListener() {
                    @Override
                    public void onDeleteParamsClick() {
                        llParamsMap.removeView(paramsView);
                        paramsViewList.remove(paramsView);
                    }
                });
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sApi = etApi.getText().toString();
                if (TextUtils.isEmpty(sApi)) {
                    Toast.makeText(context, "请输入接口", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoadingDialog.getInstance(context, "加载中……").show();

                BaseHttpApi api = new BaseHttpApi();

                if (rgRequestType.getCheckedRadioButtonId() == R.id.rb_get) {
                    api.get(sApi);
                } else {
                    ArrayMap<String, String> map = new ArrayMap<>();
                    for (ParamsView paramsView : paramsViewList) {
                        Map<String, String> params = paramsView.getParams();
                        String key = params.get("key");
                        String value = params.get("value");
                        if (!TextUtils.isEmpty(key)) {
                            map.put(key, value);
                        }
                    }

                    BaseHttpRequest request = new BaseHttpRequest(map);
                    api.setRequest(request);
                }

                api.setOnJsonHttpResponseListener(new BaseHttpApi.OnJsonHttpResponseListener() {
                    @Override
                    public void onFailure(Exception e, String s) {
                        Log.e("MainActivity", s);
                        LoadingDialog.getInstance(context, "加载中……").dismiss();
                        llResult.setVisibility(View.VISIBLE);
                        tvResult.setText(s);
                    }

                    @Override
                    public void onSuccessful(String rawJsonString) {
                        Log.e("MainActivity", rawJsonString);
                        LoadingDialog.getInstance(context, "加载中……").dismiss();

                        String message;

                        //json样式显示
                        try {
                            if (rawJsonString.startsWith("{")) {
                                JSONObject jsonObject = new JSONObject(rawJsonString);
                                message = jsonObject.toString(JSON_INDENT);
                            } else if (rawJsonString.startsWith("[")) {
                                JSONArray jsonArray = new JSONArray(rawJsonString);
                                message = jsonArray.toString(JSON_INDENT);
                            } else {
                                message = rawJsonString;
                            }
                        } catch (JSONException e) {
                            message = rawJsonString;
                        }

                        llResult.setVisibility(View.VISIBLE);
                        tvResult.setText(message);
                    }
                });
                try {
                    api.post(etApi.getText().toString());
                } catch (IllegalArgumentException e) {
                    Toast.makeText(MainActivity.this, "请输入正确的api地址！", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(context, "加载中……").dismiss();
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResult.setText("");
            }
        });

    }

    private void initDate() {
        String apiSelected = SpUtils.getString(this, SpUtils.API_SELECTED, "");
        if (!TextUtils.isEmpty(apiSelected)) {
            etApi.setText(apiSelected);
        }

        String defaultRequestType = SpUtils.getString(this, SpUtils.DEFAILT_REQUEST_TYPE, "POST");
        if (TextUtils.equals(defaultRequestType, "POST")) {
            rgRequestType.check(R.id.rb_post);
        } else {
            rgRequestType.check(R.id.rb_get);
        }

        //默认参数

        paramsCount = SpUtils.getInt(this, SpUtils.PARAMS_COUNT, 0);
        paramsViewList.clear();
        int childCount = llParamsMap.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (i != 0) {
                llParamsMap.removeView(llParamsMap.getChildAt(i));
            }
        }

        if (paramsCount == 0) {
            ParamsView paramsView = new ParamsView(MainActivity.this);
            paramsView.setDelVisible(false);
            llParamsMap.addView(paramsView);
            paramsViewList.add(paramsView);
            setParamsViewListener(paramsView);
        } else {
            for (int i = 0; i < paramsCount; i++) {
                String api = SpUtils.getString(this, SpUtils.PARAMS_KEY_VALUE_ + i, "");
                Log.e("api", api);
                ParamsView paramsView = new ParamsView(MainActivity.this);
                paramsView.setDelVisible(true);
                paramsView.setKeyAndValue(api);
                llParamsMap.addView(paramsView);
                paramsViewList.add(paramsView);
                setParamsViewListener(paramsView);
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initDate();
    }
}
