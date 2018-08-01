package com.yuan.lock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.yuan.lock.widget.PatternLockView;

public class MainActivity extends AppCompatActivity {

    private PatternLockView patternLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        patternLockView=findViewById(R.id.lock_view);

        patternLockView.setPatternLockViewListener(new PatternLockView.PatternLockViewListener() {
            @Override
            public void onLockEnd(int[] values) {
                StringBuffer sb=new StringBuffer();
                for(int i=0;i<values.length;i++)
                    sb.append(values[i]);
                Toast.makeText(MainActivity.this,sb.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
