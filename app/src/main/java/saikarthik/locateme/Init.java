package saikarthik.locateme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Init  extends AppCompatActivity {


    EditText name;
    Button sub;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        name = (EditText)findViewById(R.id.name);
        sub = (Button)findViewById(R.id.sub);
        tv = (TextView)findViewById(R.id.tv);
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AksHelper ah = new AksHelper(Init.this);
                String uname = name.getText().toString();
                ah.insertData(uname);
                ah.close();
                Toast.makeText(Init.this, "Account created successfully\n" + "Welcome "+uname+"\nEnjoy the app", Toast.LENGTH_LONG).show();
                Intent i = new Intent(Init.this,GPSActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}
