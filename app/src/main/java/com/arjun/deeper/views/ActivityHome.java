package com.arjun.deeper.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.arjun.deeper.R;
import com.arjun.deeper.events.BackpressEvent;
import com.arjun.deeper.singletons.GameStateSingleton;
import com.arjun.deeper.views.play.FragmentPlay;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityHome extends AppCompatActivity {

    private final boolean HIDE_NAV_BARS = false;

    @BindView(R.id.root_view)
    protected ViewGroup rootView;

    enum FragmentId {
        PLAY
    }

    private final Handler hideHandler = new Handler();
    private final Runnable hideRunnable = () -> {
        // Delayed removal of status and navigation bar
        hideStatusBar();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateView();
        loadFragment(FragmentId.PLAY);
    }

    private void inflateView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    private void loadFragment(FragmentId fragmentId) {
        switch (fragmentId) {
            case PLAY:
                FragmentPlay fragmentPlay = new FragmentPlay();
                getSupportFragmentManager().beginTransaction().replace(R.id.root_view, fragmentPlay).commit();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HIDE_NAV_BARS) delayedHide();
    }

    @Override
    public void onBackPressed() {
        if (GameStateSingleton.getInstance().getGameState() != GameStateSingleton.GameState.MENU) {
            EventBus.getDefault().post(new BackpressEvent());
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide() {

        // Some older devices needs a small delay between UI widget updates and a change of the status and navigation bar.
        int UI_ANIMATION_DELAY = 300;

        hideHandler.removeCallbacks(hideRunnable);
        hideHandler.postDelayed(hideRunnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    public void hideStatusBar() {

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
