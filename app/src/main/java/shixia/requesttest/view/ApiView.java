package shixia.requesttest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import shixia.requesttest.R;

/**
 * Created by ShiXiuwen on 2017/5/9.
 * <p>
 * Email:shixiuwen1991@yeah.net
 * Description:
 */

public class ApiView extends LinearLayout {

    private Context context;

    private EditText etApi;
    private Button btnDeleteApi;
    private RadioButton radioButton;

    public ApiView(Context context) {
//        super(context);
        this(context, null);
        this.context = context;
    }

    public ApiView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ApiView);
        boolean isVisible = typedArray.getBoolean(R.styleable.ApiView_av_is_plus_visible, true);
        boolean isChecked = typedArray.getBoolean(R.styleable.ApiView_av_is_checked, false);
        //删除按钮是否可见
        if (isVisible) {
            btnDeleteApi.setVisibility(VISIBLE);
        } else {
            btnDeleteApi.setVisibility(INVISIBLE);
        }

        radioButton.setChecked(isChecked);

        typedArray.recycle();
    }

    private void initView(Context context) {

        View view = View.inflate(context, R.layout.view_api, this);

        etApi = (EditText) view.findViewById(R.id.et_api);
//        etApi.setText(context.getString(R.string.api_hint));
        btnDeleteApi = (Button) view.findViewById(R.id.btn_delete_api);
        radioButton = (RadioButton) view.findViewById(R.id.rb_check);

        btnDeleteApi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onApiClickListener != null) {
                    onApiClickListener.onDeleteApiClick();
                }
            }
        });

        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onApiClickListener != null) {
                    onApiClickListener.onChangeSelectedApiClick(isChecked);
                }
            }
        });

    }

    public void setChecked(boolean isChecked) {
        radioButton.setChecked(isChecked);
    }

    public void setDelVisible(boolean visible) {
        if (visible) {
            btnDeleteApi.setVisibility(VISIBLE);
        } else {
            btnDeleteApi.setVisibility(INVISIBLE);
        }
    }

    public String getApi() {
        return etApi.getText().toString();
    }

    public void setEtApi(String api) {
        etApi.setText(api);
    }

    /**
     * ######################## 回调接口 ######################
     */

    private OnApiClickListener onApiClickListener;

    public void setOnDeleteClickListener(OnApiClickListener onApiClickListener) {
        this.onApiClickListener = onApiClickListener;
    }

    public interface OnApiClickListener {
        void onDeleteApiClick();

        void onChangeSelectedApiClick(boolean isChecked);
    }
}
