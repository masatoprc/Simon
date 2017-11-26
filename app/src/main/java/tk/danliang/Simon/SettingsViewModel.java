package tk.danliang.Simon;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by i8796 on 11/23/2017.
 */

public class SettingsViewModel extends ViewModel implements Observer {
    private static final String TAG = SettingsViewModel.class.getName();
    // Private Vars
    private MutableLiveData<Integer> mButtons;
    private MutableLiveData<Simon.Diff> mDifficulty;
    private Simon mModel;

    // Initialize persistent data
    public void init(){
        if (mModel == null){
            mModel = Simon.getInstance();
            mModel.addObserver(this);
        }

        if (mButtons == null){
            mButtons = new MutableLiveData<Integer>();
            mButtons.setValue(mModel.getNumButtons());
        }

        if (mDifficulty == null){
            mDifficulty = new MutableLiveData<Simon.Diff>();
            mDifficulty.setValue(mModel.getDiff());
        }

        mModel.initObservers();
    }

    // Get Number of Buttons Value
    public  MutableLiveData<Integer> getButtons() {
        return mButtons;
    }

    // Get Difficulty Value
    public  MutableLiveData<Simon.Diff> getDifficulty() {
        return mDifficulty;
    }

    // Change Values
    public void changeButtons(int _buttons){
        mModel.init(_buttons, mModel.getDiff());
    }
    // Increment Values
    public void changeDifficulty(String _diff){
        Simon.Diff diff;
        if (_diff.equals("Easy")) {
            diff = Simon.Diff.EASY;
        } else if (_diff.equals("Normal")) {
            diff = Simon.Diff.NORMAL;
        } else {
            diff = Simon.Diff.HARD;
        }
        mModel.init(mModel.getNumButtons(), diff);
    }

    @Override
    public void update(Observable observable, Object o) {
        mButtons.setValue(mModel.getNumButtons());
        mDifficulty.setValue(mModel.getDiff());
    }
}
