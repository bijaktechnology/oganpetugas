package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bijak.techno.oganlopian.R;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.HashMap;

public class MenumainActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener
{
    SliderLayout sliderLayout ;
    HashMap<String, String> HashMapForURL ;
    HashMap<String, Integer> HashMapForLocalRes ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menumain);
        sliderLayout = (SliderLayout)findViewById(R.id.slider);
        AddImagesUrlOnline();
        for(String name : HashMapForURL.keySet()){
            TextSliderView textSliderView = new TextSliderView(MenumainActivity.this);
            textSliderView
                    .description(name)
                    .image(HashMapForURL.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra",name);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(6000);
        sliderLayout.addOnPageChangeListener(MenumainActivity.this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    public void AddImagesUrlOnline(){

        HashMapForURL = new HashMap<String, String>();

        HashMapForURL.put("Anti Hoax", "https://diskominfo.purwakartakab.go.id/panel/assets/images/64902f14bc5c3c9694bd6e9e36838d69.png");
        HashMapForURL.put("Smart City", "https://diskominfo.purwakartakab.go.id/panel/assets/images/40a4c3f6779acfbf1ff4d7db490de0d7.png");
        HashMapForURL.put("Call Center 112", "https://diskominfo.purwakartakab.go.id/panel/assets/images/10d670b398b9b5b85067685bc298ba44.png");
        //HashMapForURL.put("Froyo", "http://androidblog.esy.es/images/froyo-4.png");
        //HashMapForURL.put("GingerBread", "http://androidblog.esy.es/images/gingerbread-5.png");
    }
}
