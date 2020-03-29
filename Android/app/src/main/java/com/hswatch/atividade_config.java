package com.hswatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.hswatch.fragments.apresentador_fragment;
import com.hswatch.fragments.listar_fragment;
import com.hswatch.fragments.finalizador_fragment;

import java.util.ArrayList;
import java.util.List;

public class atividade_config extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade_config);
        List<Fragment> list = new ArrayList<>();
        list.add(new apresentador_fragment());
        list.add(new listar_fragment());
        list.add(new finalizador_fragment());

        viewPager = findViewById(R.id.viewer_page);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        viewPager.setAdapter(new viewPagerAdapter(getSupportFragmentManager(), list));
    }

    public void seguir_fragment () throws NullPointerException{
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    public void anterior_fragment() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewPager.getCurrentItem() == 0) {
                finishAffinity();
            } else {
                anterior_fragment();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
