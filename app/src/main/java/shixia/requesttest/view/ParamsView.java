package shixia.requesttest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Map;

import shixia.requesttest.R;

/**
 * Created by ShiXiuwen on 2017/5/9.
 * <p>
 * Email:shixiuwen1991@yeah.net
 * Description:
 */

public class ParamsView extends LinearLayout {

    private Context context;

    private EditText etKey;
    private EditText etValue;
    private Button btnDeleteParams;

    public ParamsView(Context context) {
//        super(context);
        this(context, null);
        this.context = context;
    }

    public ParamsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ParamsView);
        boolean isVisible = typedArray.getBoolean(R.styleable.ParamsView_pv_is_plus_visible, true);
        //删除按钮是否可见
        if (isVisible) {
            btnDeleteParams.setVisibility(VISIBLE);
        } else {
            btnDeleteParams.setVisibility(INVISIBLE);
        }
        typedArray.recycle();
    }

    private void initView(Context context) {

        View view = View.inflate(context, R.layout.view_params, this);

        etKey = (EditText) view.findViewById(R.id.et_key);
        etValue = (EditText) view.findViewById(R.id.et_value);
        btnDeleteParams = (Button) view.findViewById(R.id.btn_delete_params);

        btnDeleteParams.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteParamsClickListener != null) {
                    onDeleteParamsClickListener.onDeleteParamsClick();
                }
            }
        });

    }

    public void setDelVisible(boolean visible) {
        if (visible) {
            btnDeleteParams.setVisibility(VISIBLE);
        } else {
            btnDeleteParams.setVisibility(INVISIBLE);
        }
    }

    public void setKeyAndValue(String api) {
        String[] split = api.split("_");
        if (TextUtils.isEmpty(split[0])) {
            return;
        }
        if(split.length==2){
            etKey.setText(split[0]);
            etValue.setText(split[1]);
        }
    }

    public Map<String, String> getParams() {
        Map<String, String> map = new ArrayMap<>();
        map.put("key", etKey.getText().toString());
        map.put("value", etValue.getText().toString());
        return map;
    }

    /**
     * ######################## 回调接口 ######################
     */

    private OnDeleteParamsClickListener onDeleteParamsClickListener;

    public void setOnDeleteClickListener(OnDeleteParamsClickListener onDeleteClickListener) {
        this.onDeleteParamsClickListener = onDeleteClickListener;
    }

    public interface OnDeleteParamsClickListener {
        void onDeleteParamsClick();
    }
}
