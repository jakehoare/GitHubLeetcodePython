package com.machinelearningforsmallbusiness.leetcodepython;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DisplayHelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView mDisplayTitle = findViewById(R.id.tv_help_title);
        mDisplayTitle.setText(getString(R.string.help_title));

        TextView mDisplayText = findViewById(R.id.tv_help_text);

        StringBuilder helpFile = new StringBuilder();
        // Build a string with the text of the file
        try {
            InputStream is = getAssets().open(getString(R.string.help_file));
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                helpFile.append(line);
                helpFile.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        mDisplayText.setText(helpFile);
        mDisplayText.setMovementMethod(new ScrollingMovementMethod());
    }

    // Handle menu item click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == R.id.action_feedback_help) {
            sendFeedback();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Start a new activity for sending a feedback email
    private void sendFeedback() {
        Uri uri = Uri.parse(getString(R.string.mail_feedback_email));
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.general_feedback));
        try {
            startActivity(mailIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), R.string.no_email,
                    Toast.LENGTH_LONG).show();
        }
    }

    // Add the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }
}
