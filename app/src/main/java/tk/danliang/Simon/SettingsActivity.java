package tk.danliang.Simon;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getName();
    private SettingsViewModel settingsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsViewModel =  ViewModelProviders.of(this).get(SettingsViewModel.class);
        settingsViewModel.init();

        Spinner buttonSpinner = findViewById(R.id.buttons_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.buttons_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buttonSpinner.setAdapter(adapter);
        buttonSpinner.setSelection(settingsViewModel.getButtons().getValue() - 1);

        Spinner diffSpinner = findViewById(R.id.diff_spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.diff_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diffSpinner.setAdapter(adapter1);
        Simon.Diff initDiff = settingsViewModel.getDifficulty().getValue();
        int diffChoice;
        switch (initDiff) {
            case EASY:
                diffChoice = 0;
                break;
            case NORMAL:
                diffChoice = 1;
                break;
            case HARD:
                diffChoice = 2;
                break;
            default:
                diffChoice = 1;
        }
        diffSpinner.setSelection(diffChoice);

        buttonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                settingsViewModel.changeButtons(Integer.parseInt((String)adapterView.getItemAtPosition(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        diffSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String diff = (String) adapterView.getItemAtPosition(i);
                settingsViewModel.changeDifficulty(diff);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }
}
