package my.test.hotfixdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,HotFixTest.HotTest(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.fix).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotFixUtil.DownDex(MainActivity.this,"HotFixTest.dex");
            }
        });
    }
}
