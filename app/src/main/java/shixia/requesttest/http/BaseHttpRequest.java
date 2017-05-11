package shixia.requesttest.http;

import android.support.v4.util.ArrayMap;

/**
 * Created by ShiXiuwen on 2017/5/9.
 * <p>
 * Email:shixiuwen1991@yeah.net
 * Description:
 */

public class BaseHttpRequest {

    private ArrayMap<String, String> paramsMap;

    public BaseHttpRequest(ArrayMap<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }

    ArrayMap<String, String> getParamsMap(){
        return paramsMap;
    }
}
