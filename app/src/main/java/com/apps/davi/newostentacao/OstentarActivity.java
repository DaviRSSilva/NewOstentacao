package com.apps.davi.newostentacao;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class OstentarActivity extends Activity {

    private GestureDetector gDetector;
    ViewGroup root;
    ImageView notaDeCem;
    Button about;

    float orgX;
    float orgY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_ostentar);
        gDetector = new GestureDetector(this, new OstentarGestureDetector());
        root = (ViewGroup) findViewById(R.id.ostentar_root);
        notaDeCem = (ImageView) findViewById(R.id.notadecem);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.funk);
        about = (Button)findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(getResources().getString(R.string.about));
            }
        });
        notaDeCem.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (!alreadyGotInformation) {
                    int[] location = new int[2];
                    notaDeCem.getLocationOnScreen(location);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int offsetY = displayMetrics.heightPixels - root.getMeasuredHeight();

                    orgX = location[0];
                    orgY = location[1] - offsetY;
                    alreadyGotInformation = true;
                }

                notaDeCem.setClickable(true);

                return gDetector.onTouchEvent(event);
            }
        });

    }

    boolean alreadyGotInformation = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ostentar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class OstentarGestureDetector implements GestureDetector.OnGestureListener {


        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }


        int MIN_DIST = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float e1Y = e1.getY();
            float e2Y = e2.getY();

            float distY = e2Y - e1Y;

            float time = Math.abs(distY / velocityY);

            final float stopY = orgY + distY;

            if (distY < 0) {
                if (distY < -50) {
                    //Fling Up
                    final ImageView view = new ImageView(getApplication());
                    view.setLayoutParams(notaDeCem.getLayoutParams());
                    view.setImageDrawable(getResources().getDrawable(R.drawable.notarodada));
                    root.addView(view);
                    ObjectAnimator flingAnimator = ObjectAnimator.ofFloat(view, "translationY", orgY, -2000);
                    startPlayingMusic();
                    flingAnimator.setDuration(500);
                    flingAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.destroyDrawingCache();
                            root.removeView(view);
                            root.bringChildToFront(about);
//                            ObjectAnimator flingAnimator = ObjectAnimator.ofFloat(view, "translationY", -(stopY*10), orgY);
//                            flingAnimator.setDuration(100);
//                            flingAnimator.start();

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    flingAnimator.start();
                } else {
                    showToast("Deslize o dedo em uma distância maior!");
                }
            }


            return true;
        }


    }

    private Toast mCurrentToast;

    public void showToast(String message) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
            mCurrentToast = null;
        } else {
            mCurrentToast = Toast.makeText(OstentarActivity.this, message, Toast.LENGTH_SHORT);
            mCurrentToast.show();
        }
    }

    android.os.Handler mHandler = new android.os.Handler();
    Runnable stopPlaying = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
        }
    };

    MediaPlayer mediaPlayer;

    private synchronized void startPlayingMusic() {
        mHandler.removeCallbacks(stopPlaying);
        mHandler.postDelayed(stopPlaying, 2000);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

    }
}
