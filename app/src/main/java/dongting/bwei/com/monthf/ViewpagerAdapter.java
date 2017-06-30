package dongting.bwei.com.monthf;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;
/**
 * 作者:${董婷}
 * 日期:2017/6/30
 * 描述:
 */
public class ViewpagerAdapter extends PagerAdapter {
    List<ImageView> ivs;
 
    public ViewpagerAdapter(List<ImageView> ivs){
        this.ivs=ivs;
    }
    @Override
    public int getCount() {
 
        return ivs.size();
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
 
        return view==object;
    }
 
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = ivs.get(position);

        /*imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handler.removeCallbacksAndMessages(null);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        handler.removeCallbacksAndMessages(null);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        handler.sendEmptyMessageDelayed(0, 2000);
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.sendEmptyMessageDelayed(0, 2000);

                        break;

                    default:
                        break;
                }
                //返回true 自己处理
                return true;
            }
        });*/

        container.addView(imageView);
 
        return imageView;
    }
 
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}