package tk.danliang.Simon;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
/**
 * the Model
 */

public class Simon extends Observable{
    // Create static instance of this mModel
    private static final Simon ourInstance = new Simon(4);
    private static final String TAG = "Simon";
    private Context mContext;
    private String message = "";
    private int perAnimDuration = 2000;
    private int perAnimCycleDuration = 100;

    static Simon getInstance()
    {
        return ourInstance;
    }

    enum State { START, COMPUTER, HUMAN, LOSE, WIN };
    enum Diff { EASY, NORMAL, HARD };

    // the game state and score
    private State state;
    private Diff diff;
    private int score;

    // length of sequence
    private int length;
    // number of possible buttons
    private int buttons;

    // the sequence of buttons and current button
    ArrayList<Integer> sequence = new ArrayList<>();
    int index;

    private boolean debug = true;

    /**
     * Model Constructor:
     * - Init member variables
     */
    public void init(int _buttons, Diff _diff) {
        length = 1;
        buttons = _buttons;
        state = State.START;
        diff = _diff;
        score = 0;
        switch (_diff) {
            case EASY:
                perAnimCycleDuration = 200;
                perAnimDuration = 3000;
                break;
            case NORMAL:
                perAnimCycleDuration = 100;
                perAnimDuration = 2000;
                break;
            case HARD:
                perAnimCycleDuration = 80;
                perAnimDuration = 1300;
                break;
        }
        initObservers();
        if (debug) {
            Log.i(TAG, "[DEBUG] starting " + buttons + " button game");
        }
    }

    Simon(int _buttons) {
        init(_buttons, Diff.NORMAL);
    }

    int getNumButtons() { return buttons; }

    Diff getDiff() {return diff;};

    String getMessage (){return message;}

    int getScore() { return score; }

    int getLength() {return length;}

    int getPerAnimDuration() {return perAnimDuration;}

    int getPerAnimCycleDuration() {return perAnimCycleDuration;}

    State getState() { return state; }

    void setContext (Context _context) {mContext = _context;}

    String getStateAsString() {

        switch (getState()) {

            case START:
                return "START";

            case COMPUTER:
                return "COMPUTER";

            case HUMAN:
                return "HUMAN";

            case LOSE:
                return "LOSE";

            case WIN:
                return "WIN";

            default:
                return "Unknown State";
        }
    }

    void newRound() {

        message = mContext.getString(R.string.watch_what_i_do);
        if (debug) {
            Log.i(TAG, "[DEBUG] newRound, Simon::state "
                    + getStateAsString());
        }

        // reset if they lost last time
        if (state == State.LOSE) {
            if (debug) { Log.i(TAG, "[DEBUG] reset length and score after loss"); }
            length = 1;
            score = 0;
        }

        sequence.clear();

        if (debug) { Log.i(TAG, "[DEBUG] new sequence: "); }

        //length = 5;

        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            int b = rand.nextInt(Integer.MAX_VALUE) % buttons;
            sequence.add(b);
        }

        index = 0;
        state = State.COMPUTER;

        initObservers();
    }

    // call this to get next button to show when computer is playing
    int nextButton() {

        if (state != State.COMPUTER) {
            Log.i(TAG, "[WARNING] nextButton called in " + getStateAsString());
            return -1;
        }

        // this is the next button to show in the sequence
        int button = sequence.get(index);

        if (debug) {
            Log.i(TAG, "[DEBUG] nextButton:  index " + index
                    + " button " + (button + 1));
        }

        // advance to next button
        index++;

        // if all the buttons were shown, give
        // the human a chance to guess the sequence
        if (index >= sequence.size()) {
            index = 0;
            state = State.HUMAN;
            initObservers();
        }

        return button;
    }

    boolean verifyButton(int button) {

        message = mContext.getString(R.string.your_turn);
        if (state != State.HUMAN) {
            Log.i(TAG, "[WARNING] verifyButton called in "
                    + getStateAsString());
            return false;
        }

        // did they press the right button?
        boolean correct = (button == sequence.get(index));

        if (debug) {
            Log.i(TAG, "[DEBUG] verifyButton: index " + index
                    + ", pushed " + button
                    + ", sequence " + sequence.get(index));
        }

        // advance to next button
        index++;

        // pushed the wrong buttons
        if (!correct) {
            message = mContext.getString(R.string.you_lose);
            state = State.LOSE;
            if (debug) {
                Log.i(TAG, ", wrong. ");
                Log.i(TAG, "[DEBUG] state is now " + getStateAsString());
            }

            // they got it right
        } else {
            if (debug) { Log.i(TAG, ", correct."); }

            // if last button, then the win the round
            if (index == sequence.size()) {
                message = mContext.getString(R.string.you_won);
                state = State.WIN;
                // update the score and increase the difficulty
                score++;
                length++;

                if (debug) {
                    Log.i(TAG, "[DEBUG] state is now " + getStateAsString());
                    Log.i(TAG, "[DEBUG] new score " + score
                            + ", length increased to " + length);
                }
            }
        }
        initObservers();
        return correct;
    }

    /**
     * Helper method to make it easier to initialize all observers
     */
    public void initObservers()
    {
        setChanged();
        notifyObservers();
    }

    /**
     * Deletes an observer from the set of observers of this object.
     * Passing <CODE>null</CODE> to this method will have no effect.
     *
     * @param o the observer to be deleted.
     */
    @Override
    public synchronized void deleteObserver(Observer o)
    {
        super.deleteObserver(o);
    }

    /**
     * Adds an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param o an observer to be added.
     * @throws NullPointerException if the parameter o is null.
     */
    @Override
    public synchronized void addObserver(Observer o)
    {
        super.addObserver(o);
    }

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    @Override
    public synchronized void deleteObservers()
    {
        super.deleteObservers();
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to
     * indicate that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. In other
     * words, this method is equivalent to:
     * <blockquote><tt>
     * notifyObservers(null)</tt></blockquote>
     *
     * @see Observable#clearChanged()
     * @see Observable#hasChanged()
     * @see Observer#update(Observable, Object)
     */
    @Override
    public void notifyObservers()
    {
        super.notifyObservers();
    }
}
