package com.mou.smslocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Information extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Button contact = (Button) findViewById(R.id.contact);
        if (contact == null) throw new AssertionError("Object cannot be null");
        contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View p) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, "arnaudalies.py@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback Sms Location");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
    }
}
