package tv.wanzi.demo.retrofit.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import tv.wanzi.demo.retrofit.R;
import tv.wanzi.demo.retrofit.databinding.ActivityMainBinding;
import tv.wanzi.demo.retrofit.utils.LogUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mBinding.btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("abcd test");
            }
        });
    }
}
