package shixia.requesttest.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import shixia.requesttest.R;

/**
 * Created by ShiXiuwen on 2017/5/9.
 * <p>
 * Email:shixiuwen1991@yeah.net
 * Description:
 */

public class LoadingDialog extends Dialog {

    private Context context;
    private static LoadingDialog loadingDialog;
    private static TextView tvLoadingMsg;

    private LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static LoadingDialog getInstance(Context context, String message){

        if(loadingDialog == null){
            loadingDialog = new LoadingDialog(context, R.style.loading_dialog);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setCancelable(true);
            setDialogStyle(context);
        }
        tvLoadingMsg.setText(message);
        return loadingDialog;
    }

    /**
     * dialog dismiss
     */
    public static void dismissDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;   //dismiss之后销毁dialog实例
        }
    }

    /**
     * 设置Dialog的动画及外观信息
     * @param context context
     */
    private static void setDialogStyle(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress_view, null);
        ImageView imgProgress = (ImageView) view.findViewById(R.id.img_progress_image);
        tvLoadingMsg = (TextView) view.findViewById(R.id.tv_loading_msg);
        ObjectAnimator animator = ObjectAnimator.ofFloat(imgProgress, "rotation", 0f, 360f);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        loadingDialog.setContentView(view);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        loadingDialog = null;   //dismiss之后销毁dialog实例
    }
}
