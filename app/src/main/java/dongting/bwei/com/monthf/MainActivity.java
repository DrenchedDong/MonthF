package dongting.bwei.com.monthf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import dongting.bwei.com.baidulocationlib.MainLibActivity;
/**
 * 作者:${董婷}
 * 日期:2017/6/30
 * 描述:
 */
public class MainActivity extends Activity {

    private ViewPager vp;
    private List<ImageView> vpList;
    private List<ImageView> docList;
    private LinearLayout line;
    private Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vp = (ViewPager)findViewById(R.id.viewpager);
        line = (LinearLayout)findViewById(R.id.linear);
        bt = (Button) findViewById(R.id.bt_enter);

        //初始化轮播图
        initVp();

        ViewpagerAdapter viewpagerAdapter = new ViewpagerAdapter(vpList);
        vp.setAdapter(viewpagerAdapter);

        initDoc();


        //viewpager滑动监听
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                for(int i=0;i<docList.size();i++){
                    if(i==position){
                        docList.get(i).setImageResource(R.drawable.dot_focus);
                    }else{
                        docList.get(i).setImageResource(R.drawable.dot_normal);
                    }
                }

                if (position==vpList.size()-1) {
                    bt.setVisibility(View.VISIBLE);
                }else {
                    bt.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MainLibActivity.class));
                finish();
            }
        });

    }

    private void initVp(){
        vpList = new ArrayList<ImageView>();

      ImageView imageView =new ImageView(this);
        imageView.setImageResource(R.drawable.c);
imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        for(int i=0;i<2;i++){
            vpList.add(imageView);
        }
    }

    private void initDoc(){
        docList = new ArrayList<ImageView>();

        for (int i=0;i<vpList.size();i++){
            ImageView imageView =new ImageView(this);
            if(i==0){
                imageView.setImageResource(R.drawable.dot_focus);
            }else{
                imageView.setImageResource(R.drawable.dot_normal);
            }

            LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,0,5,0);
            imageView.setLayoutParams(layoutParams);

            docList.add(imageView);

            line.addView(imageView);

        }
    }
}
