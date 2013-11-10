package com.mkirimli.webtools;

//import android.app.ProgressDialog;
import android.app.TabActivity;
//import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.ToggleButton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 *
 * @author mkirimli
 */
public class AnalysisTabs extends TabActivity {

    // PWT nesnesine erişir
    private PWT pwt;
    // Yükleniyor ekranı için
    /*private ProgressDialog pd;
    // Websayfasından kaynak kodları alan task*/
    private ShowSourceTask showSourceTask;
    // Tamamlanan işlerin sayısı
    int completedTask = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_tabs);

        // PWT nesnesini oluştur
        pwt = (PWT) getApplication();
        
        // Tablari yükle
        loadTabs();
        
        // Tablardaki elementleri yükle
        loadTabsContent();
        
        // Clear old data
        pwt.clearData();
        clearUI();
        
        /*this.pd = ProgressDialog.show(
                this,
                pwt.tr(R.string.working) + "...", pwt.tr(R.string.getting_message),
                true,
                true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        String result = pwt.tr(R.string.user_cancelled);
                        pwt.clearData();
                        pwt.setSource(result);
                        showSourceTask.cancel(true);
                    }
                });*/
            
        // Show the ProgressDialog on this thread
        showSourceTask = new ShowSourceTask();
        showSourceTask.execute(pwt.getUrl());
        
        ParseAlexaAdp parseAlexaAdp = new ParseAlexaAdp();
        parseAlexaAdp.execute(pwt.getUrl());
        
        ParseYahooBing parseYahooBing = new ParseYahooBing();
        parseYahooBing.execute(pwt.getUrl());
        
        ParseAdCostAlexaCnt parseAdCostAlexaCnt = new ParseAdCostAlexaCnt();
        parseAdCostAlexaCnt.execute(pwt.getUrl());
        
        ParseGoogleBacklinkAccess parseGoogleBacklinkAccess 
                                              = new ParseGoogleBacklinkAccess();
        parseGoogleBacklinkAccess.execute(pwt.getUrl());
        
        ParseGoogle parseGoogle = new ParseGoogle();
        parseGoogle.execute(pwt.getUrl());
        
        /* Analytics tracking */
        //pwt.trackPage("/AnalysisTabs");
    }
    
    /**
     * Gösterilmesi gereken tabları yükler.
     */
    private void loadTabs(){
        Resources res = getResources(); // Resource object to get Drawables
        final TabHost tabHost = getTabHost();  // The activity TabHost

        tabHost.addTab(tabHost.newTabSpec("summary")
               .setIndicator(pwt.tr(R.string.summary), 
                                res.getDrawable(R.drawable.ic_tab_summary))
               .setContent(R.id.ShowSummaryLayout));

        tabHost.addTab(tabHost.newTabSpec("header")
               .setIndicator(pwt.tr(R.string.header), 
                                res.getDrawable(R.drawable.ic_tab_header))
               .setContent(R.id.ShowHeaderLayout));

        tabHost.addTab(tabHost.newTabSpec("meta")
               .setIndicator(pwt.tr(R.string.meta), 
                                res.getDrawable(R.drawable.ic_tab_meta))
               .setContent(R.id.ShowMetaLayout));

        tabHost.addTab(tabHost.newTabSpec("url")
               .setIndicator(pwt.tr(R.string.url), 
                                res.getDrawable(R.drawable.ic_tab_url))
               .setContent(R.id.ShowUrlLayout));
        
        tabHost.addTab(tabHost.newTabSpec("network")
               .setIndicator(pwt.tr(R.string.network), 
                                res.getDrawable(R.drawable.ic_tab_network))
               .setContent(R.id.ShowNetworkLayout));

        // Tüm tabları oluştur ve tabHost a ekle.
        tabHost.addTab(tabHost.newTabSpec("source")
               .setIndicator(pwt.tr(R.string.source), 
                                res.getDrawable(R.drawable.ic_tab_source))
                .setContent(R.id.ShowSourceLayout));
        
        tabHost.addTab(tabHost.newTabSpec("web")
               .setIndicator(pwt.tr(R.string.web), 
                                res.getDrawable(R.drawable.ic_tab_web))
               .setContent(R.id.ShowWebLayout));
        
        tabHost.addTab(tabHost.newTabSpec("console")
               .setIndicator(pwt.tr(R.string.console), 
                                res.getDrawable(R.drawable.ic_tab_console))
               .setContent(R.id.ShowConsoleLayout));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                pwt.getShowSource().tabChanged(tabId);
                pwt.getShowHeader().tabChanged(tabId);
                pwt.getShowMeta().tabChanged(tabId);
                pwt.getShowWeb().tabChanged(tabId);
                pwt.getShowUrl().tabChanged(tabId);
                pwt.getShowNetwork().tabChanged(tabId);
                pwt.getShowConsole().tabChanged(tabId);
                pwt.getShowSummary().tabChanged(tabId);
                /* Analytics tracking */
                //pwt.trackPage(tabId + "Tab");
            }
        });
        tabHost.setCurrentTab(0);
        
        // Standart genislik ver
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().width = (int) (75 * density);
        }
    }
    
    /**
     * Tablardaki elementleri yükle
     */
    private void loadTabsContent(){
       pwt.getShowSummary().setSummaryDetails((ExpandableListView) findViewById(R.id.summaryList));
       pwt.getShowSource().setHtmlsource((EditText) findViewById(R.id.htmlsource)); 
       pwt.getShowHeader().setHeaderDetails((ListView) findViewById(R.id.headerList));
       pwt.getShowNetwork().setNetworkDetails((ListView) findViewById(R.id.networkList),
                                              (ToggleButton) findViewById(R.id.CssButton),
                                              (ToggleButton) findViewById(R.id.ImgButton),
                                              (ToggleButton) findViewById(R.id.JsButton),
                                              (ToggleButton) findViewById(R.id.OtherButton),
                                              (ToggleButton) findViewById(R.id.AllButton));
       pwt.getShowMeta().setMetaDetails((ListView) findViewById(R.id.metaList));
       pwt.getShowUrl().setUrlDetails((ListView) findViewById(R.id.urlList));
       pwt.getShowWeb().setEngine((WebView) findViewById(R.id.web_engine));
       pwt.getShowConsole().setObjects((ListView) findViewById(R.id.commandList),
                                       (Button) findViewById(R.id.ClearLine),
                                       (Button) findViewById(R.id.runCommand),
                                       (Button) findViewById(R.id.ClearConsone),
                                       (AutoCompleteTextView) findViewById(R.id.command),
                                       (Button) findViewById(R.id.defvar),
                                       (Button) findViewById(R.id.newobj),
                                       (Button) findViewById(R.id.ifstate),
                                       (Button) findViewById(R.id.fnc),
                                       (Button) findViewById(R.id.forloop),
                                       (Button) findViewById(R.id.whileloop),
                                       (Button) findViewById(R.id.trycatch),
                                       (Button) findViewById(R.id.parantez),
                                       (Button) findViewById(R.id.suslu),
                                       (Button) findViewById(R.id.koseli),
                                       (Button) findViewById(R.id.jshort),
                                       (Button) findViewById(R.id.cifttirnak),
                                       (Button) findViewById(R.id.tektirnak),
                                       (Button) findViewById(R.id.esittir),
                                       (Button) findViewById(R.id.newline));
    }

    private void clearUI(){
        pwt.getShowSummary().clear();
        pwt.getShowHeader().clear();
        pwt.getShowMeta().clear();
        pwt.getShowNetwork().clear();
        pwt.getShowConsole().clear();
        pwt.getShowSource().clear();
        pwt.getShowUrl().clear();
        pwt.getShowWeb().clear();
    }
    
    private void taskCompleted(String result) {
        pwt.getShowSource().ready(result);
        pwt.getShowMeta().ready(result);
        pwt.getShowUrl().ready(result);
        pwt.getShowNetwork().ready();
        pwt.getShowWeb().ready();
        pwt.getShowSummary().ready(result);
        pwt.getShowConsole().ready();
    }

   private class ParseAlexaAdp extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... args) {
            /************************** ALEXA *********************************/
            HashMap<String, String> alexa_info = new HashMap<String, String>();
            try {
                // Returns domain, rank, reach, rank_delta, backlink, dmoz
                alexa_info = pwt.getShowSummary().parser.alexaInfo(pwt.getUrl());
                String dmoz_tr = alexa_info.get("dmoz").equals("YES") ? 
                                 pwt.tr(R.string.Yes) : 
                                 (alexa_info.get("dmoz").equals("NO") ? 
                                 pwt.tr(R.string.No) :
                                 alexa_info.get("dmoz"));
                                 
                alexa_info.put("dmoz", dmoz_tr);
            } catch (Exception e) {
                alexa_info.put("rank", "?");
                alexa_info.put("dmoz", "?");
                alexa_info.put("backlink", "?");
            }
            
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Ranks))
                                        .put(pwt.tr(R.string.Alexa) + " *", 
                                                    alexa_info.get("rank"));
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Other))
                                        .put(pwt.tr(R.string.DMOZ) + " *", 
                                             alexa_info.get("dmoz"));
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Backlinks))
                                        .put(pwt.tr(R.string.Alexa) + " *", 
                                             alexa_info.get("backlink"));
            
            /**************************** ADPLANNER ***************************
            HashMap<String, String> adplanner_info = new HashMap<String, String>();
            try {
                // Returns visits, visitors, pageview, visits2
                adplanner_info = pwt.getShowSummary().parser.adPlannerInfo(pwt.getUrl());
            } catch (Exception e) {
                adplanner_info.put("visits", "?");
                adplanner_info.put("visitors", "?");
                adplanner_info.put("pageview", "?");
            }
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.VisitorsMonthly))
                                        .put(pwt.tr(R.string.Visits) + " *", 
                                             adplanner_info.get("visits"));
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.VisitorsMonthly))
                                        .put(pwt.tr(R.string.Visitors) + " *", 
                                                    adplanner_info.get("visitors"));
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.VisitorsMonthly))
                                        .put(pwt.tr(R.string.PageViews) + " *", 
                                                    adplanner_info.get("pageview"));*/
            
            //pwt.getShowSummary().expListAdapter.notifyDataSetChanged();
            return "";
        }
        
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pwt.getShowSummary().expListAdapter.notifyDataSetChanged();
        }
    }
    
   private class ParseYahooBing extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... args) {
            /************************** YAHO **********************************/
            String yahoo_index = "";
            try {
                yahoo_index 
                         = pwt.getShowSummary().parser.yahooIndex(pwt.getUrl());
            } catch (Exception e) {
                yahoo_index = "?";
            }
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Indexes))
                                        .put(pwt.tr(R.string.Yahoo), 
                                             yahoo_index);
            
            /************************** BING **********************************/
            String bing_index = "";
            try {
                bing_index = 
                          pwt.getShowSummary().parser.bingIndex(pwt.getUrl());
            } catch (Exception e) {
                bing_index = "?";
            }
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Indexes))
                                        .put(pwt.tr(R.string.Bing), 
                                             bing_index);
            //pwt.getShowSummary().expListAdapter.notifyDataSetChanged();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pwt.getShowSummary().expListAdapter.notifyDataSetChanged();
        }
    }
     
   private class ParseAdCostAlexaCnt extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... args) {
             /************************ ADCOST *********************************/
            String adcost = "";
            try {
                adcost = 
                          pwt.getShowSummary().parser.adCost(pwt.getUrl());
            } catch (Exception e) {
                adcost = "?";
            }
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Other))
                                        .put(pwt.tr(R.string.AdCost) + " *", 
                                             adcost);
            
            /*************************** ALEXA COUNTRY ************************/
            HashMap<String, String> alexa_country = new HashMap<String, String>();
            try {
                // Returns country, rank
                alexa_country = pwt.getShowSummary().parser.alexaCountry(pwt.getUrl());
            } catch (Exception e) {
                alexa_country.put("rank", "?");
            }
            String acs = "" + alexa_country.get("country") + " - " + alexa_country.get("rank");
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Ranks))
                                        .put(pwt.tr(R.string.AlexaCountry) + " *", 
                                             acs);
            
            /*FACEBOOK*/
            HashMap<String, String> facebook = new HashMap<String, String>();
            try {
              facebook = pwt.getShowSummary().parser.getFacebook(pwt.getUrl());   
            } catch (Exception e) {
                facebook.put("FacebookLike", "?");
                facebook.put("FacebookTalking", "?");
            }
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Social))
                                        .put(pwt.tr(R.string.FacebookLike) + " *", facebook.get("FacebookLike"));
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Social))
                                        .put(pwt.tr(R.string.FacebookTalking) + " *", facebook.get("FacebookTalking"));
            
            /* Twitter */
            String twitter = new String();
            try {
              twitter = pwt.getShowSummary().parser.getTwitter(pwt.getUrl());   
            } catch (Exception e) {
                twitter = "?";
            }
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Social))
                                        .put(pwt.tr(R.string.Twitter) + " *", twitter);
            
            //pwt.getShowSummary().expListAdapter.notifyDataSetChanged();
            return "";
        }
        
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pwt.getShowSummary().expListAdapter.notifyDataSetChanged();
        }
    }
    
   private class ParseGoogleBacklinkAccess extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... args) {
             String google_backlink = "";
            try {
                google_backlink = 
                       pwt.getShowSummary().parser.googleBackLink(pwt.getUrl());
            } catch (Exception e) {
                google_backlink = "?";
            }
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Backlinks))
                                        .put(pwt.tr(R.string.Google), 
                                             google_backlink);

            String google_last_access = "";
            try {
                google_last_access = 
                     pwt.getShowSummary().parser.googleLastAccess(pwt.getUrl());
                String strings[] = google_last_access.split(" ");
                google_last_access = strings[0] + " " + strings[1] + " " + strings[2];
            } catch (Exception e) {
                google_last_access = "?";
            }
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Other))
                                        .put(pwt.tr(R.string.LastAccess), 
                                             google_last_access);
            //pwt.getShowSummary().expListAdapter.notifyDataSetChanged();
            return "";
        }
        
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pwt.getShowSummary().expListAdapter.notifyDataSetChanged();
        }
    }
   
   private class ParseGoogle extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... args) {
            /************************* GOOGLE *********************************/
            String google_rank = "";
            try {
                google_rank = 
                          pwt.getShowSummary().parser.googlePageRank(pwt.getUrl());
            } catch (Exception e) {
                google_rank = "?";
            }
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Ranks))
                                        .put(pwt.tr(R.string.GooglePR), google_rank);

            String google_index = "";
            try {
                google_index = 
                          pwt.getShowSummary().parser.googleIndex(pwt.getUrl());
            } catch (Exception e) {
                google_index = "?";
            }
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Indexes))
                                        .put(pwt.tr(R.string.Google), google_index);
                
            String google_image_index = "";
            try {
                google_image_index = 
                     pwt.getShowSummary().parser.googleImageIndex(pwt.getUrl());
            } catch (Exception e) {
                google_image_index = "?";
            }
            pwt.getShowSummary().seoData.get(pwt.tr(R.string.Indexes))
                                        .put(pwt.tr(R.string.GoogleImage), 
                                             google_image_index);
            return "";
        }
        
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pwt.getShowSummary().expListAdapter.notifyDataSetChanged();
        }
    }
   
   private class ShowSourceTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... args) {
            String response = "";
            try {
                URL url = new URL(args[0]);
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(30000);

                String lastUrl = args[0];
                pwt.addUrlRedirection(lastUrl, conn.getResponseCode());
                while (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
                        || conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP
                        || conn.getResponseCode() == HttpURLConnection.HTTP_SEE_OTHER) {
                    lastUrl = conn.getHeaderField("Location");
                    url = new URL(lastUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    pwt.addUrlRedirection(lastUrl, conn.getResponseCode());
                }

                // List all the response headers from the server.
                // Note: The first call to getHeaderFieldKey() will implicit send
                // the HTTP request to the server.
                for (int i = 0;; i++) {
                    String headerName = conn.getHeaderFieldKey(i);
                    String headerValue = conn.getHeaderField(i);

                    if (headerName == null && headerValue == null) {
                        break; // No more headers
                    }
                    if (headerName == null) {
                        // The header value contains the server's HTTP version
                    } else {
                        pwt.getShowHeader().ready(headerName, headerValue);
                    }
                }
                
                InputStream is = conn.getInputStream();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(is, Charset.forName(pwt.getRealEncoding())));
                String inputLine;

                StringBuilder reply = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    //publishProgress((int) ((response.length() / (float) size) * 100));
                    reply.append(inputLine);
                    if (!in.ready()) {
                        break;
                    }
                }
                in.close();
                response = reply.toString();
            } catch (MalformedURLException exURL) {
                response = pwt.tr(R.string.invalid_url);
            } catch (IOException ex) {
                response = pwt.tr(R.string.url_open_error);
                response += "\n" + ex.getMessage() + "\n";
            } catch (Exception exc) {
                response = pwt.tr(R.string.url_open_error);
                response += "\n" + exc.getMessage() + "\n";
            }
            return response.toString();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //String title = pwt.tr(R.string.working);
            //AnalysisTabs.this.pd.setTitle(title + "... (%" + progress[0] + ")");
        }

        @Override
        protected void onPostExecute(String result) {
            // Pass the result data back to the main activity
            pwt.getShowSummary().expListAdapter.notifyDataSetChanged();
            AnalysisTabs.this.taskCompleted(result);
        }
    }
}
