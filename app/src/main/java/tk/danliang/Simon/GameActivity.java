package tk.danliang.Simon;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GameActivity extends AppCompatActivity implements Observer{

    private Simon mModel;
    TextView score;
    TextView gameMessage;
    Button startGameButton;
    int delayInterval;
    int delay;
    List<Button>  mButtons;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        mButtons = new ArrayList<>();
        setContentView(R.layout.activity_game);
        if (mModel == null){
            mModel = Simon.getInstance();
            mModel.setContext(this);
            mModel.addObserver(this);
        }
        ((TextView)findViewById(R.id.score)).setText(String.valueOf(mModel.getScore()));
        delayInterval = mModel.getPerAnimDuration();
        delay = -delayInterval;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        for (int i = 0; i < mModel.getNumButtons(); i++) {
            Button button = new Button(this);
            button.setBackgroundResource(R.drawable.button_shape);
            button.setText(String.valueOf(i + 1));
            button.setTextSize(30);
            button.setClickable(false);
            final int butIndex = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.isClickable()) {
                        mModel.verifyButton(butIndex);
                    }
                }
            });
            int dps = 60;
            final float scale = getResources().getDisplayMetrics().density;
            int pixels = (int) (dps * scale + 0.5f);
            LinearLayout.LayoutParams buttonLayoutParam = new LinearLayout.LayoutParams(pixels, pixels);
            buttonLayoutParam.weight = 1.0f;
            buttonLayoutParam.gravity = Gravity.CENTER_HORIZONTAL;
            buttonLayoutParam.bottomMargin = (int) (8 * scale + 0.5f);
            button.setLayoutParams(buttonLayoutParam);
            button.setGravity(Gravity.CENTER);
            mButtons.add(button);
            ((ViewGroup) findViewById(R.id.buttonPanel)).addView(button);
        }
        startGameButton = findViewById(R.id.start_a_new_game_button);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mModel.newRound();
                view.setVisibility(View.INVISIBLE);
            }
        });

        score = findViewById(R.id.score);
        gameMessage = findViewById(R.id.game_message);
    }

    @Override
    public void update(Observable observable, Object o) {
        score.setText(String.valueOf(mModel.getScore()));
        gameMessage.setText(mModel.getMessage());
        Simon.State curState = mModel.getState();
        delayInterval = mModel.getPerAnimDuration();
        switch (curState) {
            case COMPUTER:
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gameMessage.setText(getString(R.string.your_turn));
                            for (Button but : mButtons) {
                                but.setClickable(true);
                            }
                        }
                    }, delayInterval * mModel.getLength());
                while (mModel.getState() == Simon.State.COMPUTER) {
                    delay += delayInterval;
                    final int index = mModel.nextButton();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            AnimationSet animationSet = new AnimationSet(true);
                            Animation shake = AnimationUtils
                                    .loadAnimation(getBaseContext(), R.anim.shake);
                            Animation translate = AnimationUtils
                                    .loadAnimation(getBaseContext(), R.anim.translate);
                            shake.setDuration(mModel.getPerAnimCycleDuration());
                            translate.setDuration(mModel.getPerAnimCycleDuration());
                            animationSet.addAnimation(shake);
                            animationSet.addAnimation(translate);
                            Button curButton = mButtons.get(index);
                            curButton.setAnimation(animationSet);
                            curButton.startAnimation(animationSet);
                        }
                    }, delay);
                }
                break;
            case HUMAN:
                break;
            case WIN:
            case LOSE:
                for (Button but : mButtons) {
                    but.setClickable(false);
                }
                startGameButton.setVisibility(View.VISIBLE);
                delay = -mModel.getPerAnimDuration();
                break;
            default:
        }
    }
}
