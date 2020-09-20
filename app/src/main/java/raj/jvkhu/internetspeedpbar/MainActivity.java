package raj.jvkhu.internetspeedpbar;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import raj.jvkhu.internetspeedprogressbar.dialog.InternetSpeedProgressBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_show_progress_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InternetSpeedProgressBar internetSpeedProgressBar = new InternetSpeedProgressBar(MainActivity.this);
                internetSpeedProgressBar.show();
            }
        });
    }
}
