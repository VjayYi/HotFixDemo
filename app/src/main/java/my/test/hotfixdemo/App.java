package my.test.hotfixdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by YiVjay
 * on 2020/4/20
 */
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        HotFixUtil.LoadDex(this);
    }
}
