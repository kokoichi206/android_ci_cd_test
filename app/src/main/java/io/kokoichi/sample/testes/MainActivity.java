package io.kokoichi.sample.testes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.jarjarred.org.antlr.v4.tool.Rule;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}