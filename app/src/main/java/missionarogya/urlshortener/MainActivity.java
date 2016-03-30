package missionarogya.urlshortener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnmABCDEFGHIJKLMNOPQRSTUVWXYZ";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences("URLs", Context.MODE_PRIVATE);

        final EditText editTextURL = (EditText)findViewById(R.id.editURL);
        final Button btnCounter = (Button)findViewById(R.id.btnGetCounter);
        final Button btnLongURL = (Button)findViewById(R.id.btnGetLongURL);
        final Button btnShortURL = (Button)findViewById(R.id.btnGetShortURL);
        final TextView result = (TextView)findViewById(R.id.result);
        final TextView btnClear = (TextView)findViewById(R.id.btnGetClear);
        final int hits = 0;

        final SharedPreferences.Editor editor = sharedpreferences.edit();
        final SharedPreferences shf = getSharedPreferences("URLs", MODE_PRIVATE);


        btnShortURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url = editTextURL.getText().toString().trim();
                    if (URLValidator(url) && url.length() > 17) {
                        String value = shf.getString(url, null);
                        if( value == null) {
                            String urlString = generateKey(4);
                            String domainString = "";
                            String[] urlparts = url.split("//");
                            String newURL = urlparts[0];
                            String[] domainKey = urlparts[1].split("/");
                            String[] domain = domainKey[0].split("\\.");
                            domainString = domain[1].substring(0, 1) + "." + domain[2].substring(0, 2);
                            newURL = newURL + "//" + domainString + "/" + urlString;
                            result.setText(newURL);
                            editor.putString(url, newURL);
                            editor.commit();
                        }else{
                            result.setText(value);
                        }
                        result.setClickable(true);
                        result.setPaintFlags(result.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid URL / URL required / Already a tiny URL.", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLongURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String longURL = "";
                String url = editTextURL.getText().toString().trim();
                if(URLValidator(url) && url.length() == 17){
                    longURL = getLongURL(url,shf);
                    result.setText(longURL);
                    result.setClickable(true);
                    result.setPaintFlags(result.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }else{
                    Toast.makeText(getApplicationContext(),"Invalid URL / URL required / Already a long URL.",Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editTextURL.getText().toString().trim();
                if(URLValidator(url) && url.length()== 17){
                    int count = shf.getInt(url, 0);
                    result.setText(Integer.toString(count));
                    result.setClickable(false);
                }else{
                    Toast.makeText(getApplicationContext(),"Invalid URL / URL required / Only counts hits for tiny urls.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = MainActivity.this.getSharedPreferences("URLs", Context.MODE_PRIVATE);
                settings.edit().clear().commit();
                Toast.makeText(getApplicationContext(), "Data cleared!", Toast.LENGTH_SHORT).show();
                result.setText("");
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = result.getText().toString();
                if(text.length() == 17){
                    int count = shf.getInt(text, 0);
                    editor.putInt(text, count+1);
                    editor.commit();
                    text = getLongURL(text, shf);
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
                startActivity(browserIntent);
            }
        });

        final Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "You are exiting from this app!", Toast.LENGTH_SHORT).show();
                MainActivity.this.finish();
            }
        });
    }

    private boolean URLValidator(String url){
        if(url != null && url.length()>0)
            return Patterns.WEB_URL.matcher(url).matches();
        else
            return false;
    }

    private String getLongURL(String url, SharedPreferences shf){
        String longURL="";
        for (Map.Entry<String, ?> entry : shf.getAll().entrySet()) {
            if (url.equals(entry.getValue())) {
                longURL = entry.getKey();
            }
        }
        return longURL;
    }

    private static String generateKey(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
