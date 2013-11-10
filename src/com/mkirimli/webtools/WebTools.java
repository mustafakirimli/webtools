package com.mkirimli.webtools;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.net.URL;
import java.util.ArrayList;

public class WebTools extends Activity implements OnClickListener {
    /**
     * PWT nesnesine erişir
     */
    private PWT pwt;
    /**
     * Kullanıcının URL adresi gireceği alan.
     */
    private EditText entry;
    private Intent analysisTabs;
    private ListView recentList;
    ArrayAdapter<String> recentAdapter;

    public void onClick(View v) {
        
        // Kullanıcın girdiği adresi set et
        pwt.setUrl(entry.getText().toString());

        if (pwt.getUrl().equals("")) {
            pwt.showMessage(pwt.tr(R.string.enter_url));
            return;
        } else if (false == pwt.checkInternetConnection()) {
            pwt.showMessage(pwt.tr(R.string.check_your_internet));
            return;
        } else if (false == pwt.checkUrl()) {
            pwt.showMessage(pwt.tr(R.string.invalid_url));
            return;
        } else {
            startActivity(analysisTabs);
        }
    }

    public String getSharedUrl(){
        String sharedUrl = "";
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();

        // if this is from the share menu
        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_TEXT)) {
                try {
                    // Get resource path from intent callee
                    sharedUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
                } catch (Exception e) {
                    //Log.v("share error:", e.toString());
                }
            }
        }
        return (String) sharedUrl;
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        pwt = (PWT) getApplication();
        entry = (EditText) findViewById(R.id.entry);
        analysisTabs = new Intent(getApplicationContext(), AnalysisTabs.class);
        
        /* Share Via */
        String sharedUrl = this.getSharedUrl();
        if( sharedUrl != null ){
            entry.setText(sharedUrl);
        }
        
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.entry);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.protocol_list);

        String[] protocols = getResources().getStringArray(R.array.protocols_array);
        for (String string : protocols) {
            adapter.add(string);
        }

        /*ClipboardManager clipboard =
                (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        Cursor mCur = this.managedQuery(Browser.BOOKMARKS_URI,
                Browser.HISTORY_PROJECTION, null, null, null);
        String clipText = "";
        if( false != clipboard.getText().equals("") ){
           clipText = (String) clipboard.getText().toString();
        }
        if (false == clipText.equals("")) {
            adapter.add(clipText);
        }

        mCur.moveToFirst();
        int iCur = mCur.getCount();
        int k = 0;
        if (iCur > 0) {
            while (mCur.isAfterLast() == false) {
                if( iCur - k > 10 ){
                    adapter.add(mCur.getString(Browser.HISTORY_PROJECTION_URL_INDEX));
                }
                k++;
                mCur.moveToNext();
            }
        }*/
        textView.setAdapter(adapter);
        
        textView.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    onClick(v);
                    return true;
                }
                return false;
            }
        });

        // Capture our button from layout
        Button button = (Button) findViewById(R.id.ok);
        // Register the onClick listener with the implementation above
        button.setOnClickListener(this);
        
        /* Recent List */
        this.recentList = (ListView) findViewById(R.id.recentList);
        this.recentAdapter = new ArrayAdapter<String>(pwt.getApplicationContext(), R.layout.main_row);
        this.recentList.setAdapter(this.recentAdapter);
        recentList.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Object o = recentList.getItemAtPosition(arg2);
                entry.setText((String) o);
                }
        });
        
        try {
            ArrayList<String> recents = new ArrayList<String>();
            Cursor mCur = this.managedQuery(Browser.BOOKMARKS_URI,
                    Browser.HISTORY_PROJECTION,
                    null,
                    null,
                    Browser.HISTORY_PROJECTION_DATE_INDEX + " DESC");

            if (mCur.getCount() > 0) {
                mCur.moveToPosition(mCur.getCount() - 1);
                while (mCur.isBeforeFirst() == false) {
                    if (recents.size() > 19) {
                        break;
                    }
                    String url = mCur.getString(Browser.HISTORY_PROJECTION_URL_INDEX);
                    try {
                        URL uri = new URL(url);
                        url = (uri.getProtocol() + "://" + uri.getHost()).toLowerCase();
                        if (false == recents.contains(url)) {
                            recents.add(url);
                        }
                    } catch (Exception e) {
                    }
                    mCur.moveToPrevious();
                }
            }
            for (int i = 0; i < recents.size(); i++) {
                String rurl = recents.get(i);
                recentAdapter.add(rurl);
            }
        } catch (Exception e) {}
        /* Recent List */
        
        /* Analytics tracking */
        //pwt.trackPage("/HomeScreen");
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* Analytics tracking */
        //pwt.trackPage("/Home");
    }
}