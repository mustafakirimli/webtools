package com.mkirimli.webtools;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.Gravity;
import android.widget.Toast;
//import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Sınıflar (Activiy) arasında data iletişimini sağlar
 * 
 * @author mustafa
 */
public class PWT extends Application {

    public static final String APP_NAME = "Web Tools";
    /**
     * Kullanıcının girdiği URL adresi
     */
    private String url;
    /**
     * Girilen URL adresine ait kaynak kodu
     */
    private String source;
    /**
     * Kaynak kodunun değiştirilip değiştirilmediğini tutar
     */
    public boolean sourceChanged;
    /**
     * Aktif tab in adı
     */
    public String currentTab;
    /**
     * Sunucudan dönen (JS disable) kaynak kodu
     */
    private String nativeSource;
    /**
     * Girilen adrese ait cookieler
     */
    private ArrayList<String> cookies;
    /**
     * Kullanıcının girdiği URL adresine gidilirken alınan yönlendirmeler.
     */
    private LinkedHashMap<String, Integer> urlRedirections;
    /**
     * Kullanıcının girdiği URL 'e yapılan headerlar
     */
    private LinkedHashMap<String, String> requestHeaders;
    /**
     * Kullanıcının girdiği URL 'in döndürdüğü headerlar
     */
    private LinkedHashMap<String, String> responseHeaders;
    /**
     * Girilen URL adresine ait kaynak koddaki meta bilgileri
     */
    private LinkedHashMap<String, String> metas;
    /**
     * Varsayılan, headerla gelen, metada tanımlanan encoding bilgileri
     */
    private LinkedHashMap<String, String> encodings;
    /**
     * Varsayılan, headerla gelen, metada tanımlanan content type bilgileri
     */
    private LinkedHashMap<String, String> contentType;
    /**
     * Sayfada yüklenen dosyalar (js, css, img, xhr)
     */
    private LinkedHashMap<String, String> networkFiles;
    /**
     * Sayfa hakkında bazı bilgileri (pagerank, alexa, seo indexes)
     */
    private LinkedHashMap<String, String> summaryInfos;
    /**
     * Hata mesajlarini gostermek icin
     */
    private Toast toast;
    /**
     * Analytics tracking
     *
    private GoogleAnalyticsTracker tracker;*/
    
    /************ TAB CLASSES : START *************/                    
    private ShowSource showSource;
    private ShowHeader showHeader;
    private ShowNetwork showNetwork;
    private ShowSummary showSummary;
    private ShowMeta showMeta;
    private ShowWeb showWeb;
    private ShowUrl showUrl;
    private ShowConsole showConsole;
    /************ TAB CLASSES : END *************/
    
    @Override
    public void onCreate() {
        super.onCreate();
        url = "";
        source = "";
        sourceChanged = false;
        currentTab = "";
        nativeSource = "";
        
        cookies = new ArrayList<String>();
        urlRedirections = new LinkedHashMap<String, Integer>();
        requestHeaders = new LinkedHashMap<String, String>();
        responseHeaders = new LinkedHashMap<String, String>();
        metas = new LinkedHashMap<String, String>();
        encodings = new LinkedHashMap<String, String>(3);
        contentType = new LinkedHashMap<String, String>(3);
        networkFiles = new LinkedHashMap<String, String>();
        summaryInfos = new LinkedHashMap<String, String>();
        
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        
        initializeTabClass();
        
        /*tracker = GoogleAnalyticsTracker.getInstance();
        // Start the tracker in manual dispatch mode...
        tracker.startNewSession("UA-28317263-1", this);*/
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public String getUrl() {
        return url;
    }

    public String getSource() {
        return source;
    }

    public String getNativeSource() {
        return nativeSource;
    }

    public ArrayList<String> getCookies() {
        return cookies;
    }

    public HashMap<String, Integer> getUrlRedirections() {
        return urlRedirections;
    }

    public HashMap<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public HashMap<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public HashMap<String, String> getMetas() {
        return metas;
    }

    public LinkedHashMap<String, String> getEncodings() {
        return encodings;
    }

    public LinkedHashMap<String, String> getContentType() {
        return contentType;
    }

    public LinkedHashMap<String, String> getNetworkFiles() {
        return networkFiles;
    }

    public LinkedHashMap<String, String> getSummaryInfos() {
        return summaryInfos;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setNativeSource(String nativeSource) {
        this.nativeSource = nativeSource;
    }

    public void setCookies(ArrayList<String> cookies) {
        this.cookies = cookies;
    }

    public void setUrlRedirections(LinkedHashMap<String, Integer> urlRedirections) {
        this.urlRedirections = urlRedirections;
    }

    public void setRequestHeaders(LinkedHashMap<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public void setResponseHeaders(LinkedHashMap<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public void setMetas(LinkedHashMap<String, String> metas) {
        this.metas = metas;
    }

    public void setEncodings(LinkedHashMap<String, String> encodings) {
        this.encodings = encodings;
    }

    public void setContentType(LinkedHashMap<String, String> contentType) {
        this.contentType = contentType;
    }

    public void setNetworkFiles(LinkedHashMap<String, String> networkFiles) {
        this.networkFiles = networkFiles;
    }

    public void setSummaryInfos(LinkedHashMap<String, String> summaryInfos) {
        this.summaryInfos = summaryInfos;
    }

    public String addCookie(String cookie) {
        this.cookies.add(source);
        return source;
    }

    public Integer addUrlRedirection(String key, Integer value) {
        return this.urlRedirections.put(key, value);
    }

    public String addResponseHeader(String key, String value) {
        return this.responseHeaders.put(key, value);
    }

    public String addRequestHeader(String key, String value) {
        return this.requestHeaders.put(key, value);
    }

    public String addMeta(String key, String value) {
        return this.metas.put(key, value);
    }
    
    public String addEncoding(String key, String value){
        return this.encodings.put(key, value);
    }
    
    public String addContentType(String key, String value){
        return this.contentType.put(key, value);
    }

    public String addNetworkFile(String key, String value){
        return this.networkFiles.put(key, value);
    }
    
    public String addSummaryInfo(String key, String value){
        return this.summaryInfos.put(key, value);
    }
    
    /**
     * Internet baglantisi olup olmadigina bakar.
     * 
     * Wifi 3G vb. baglantilardan herhangi birinin olup olmadigina bakar.
     * 
     * @return boolean 
     */
    public boolean checkInternetConnection() {
        ConnectivityManager conMgr;
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected());
    }

    /**
     * Kullanıcının girdiği URL adresini gecerli olup olmadigina bakar.
     * 
     * @return boolean
     */
    public boolean checkUrl(){
        try {
            URL enteredUrl = new URL(url);
        } catch (MalformedURLException me) {
            if (me.getMessage().startsWith("Protocol not found:")) {
                url = "http://" + url;
                return checkUrl();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
     /**
     * Verilen hata mesajini gosterir.
     * 
     * @param msg Gosterilecek mesaj
     */
    public void showMessage(String msg) {
        toast.setText((CharSequence) msg);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
    
    /**
     * Analytics tracking
     * @param pageName pagename
     *
    public void trackPage(String pageName){
        tracker.trackPageView(pageName);
        tracker.dispatch();
    }*/
    
    /**
     * Çeviri
     * 
     * @param _id 
     * @return String
     */
    public String tr(int _id){
        return getApplicationContext().getResources().getString(_id);
    }
    
    /**
     * Tab işlemlerini yapan sınıfları yükler
     */
    private void initializeTabClass() {
        /* CREATE TAB CLASSESS OBJECTS : START */
        showSource = new ShowSource(this);
        showHeader = new ShowHeader(this);
        showNetwork = new ShowNetwork(this);
        showSummary = new ShowSummary(this);
        showMeta = new ShowMeta(this);
        showWeb = new ShowWeb(this);
        showUrl = new ShowUrl(this);
        showConsole = new ShowConsole(this);
        /* CREATE TAB CLASSESS OBJECTS : END */
    }

    public ShowSource getShowSource() {
        return showSource;
    }

    public ShowHeader getShowHeader() {
        return showHeader;
    }

    public ShowNetwork getShowNetwork() {
        return showNetwork;
    }

    public ShowSummary getShowSummary() {
        return showSummary;
    }

    public ShowMeta getShowMeta() {
        return showMeta;
    }

    public ShowWeb getShowWeb() {
        return showWeb;
    }

    public ShowUrl getShowUrl() {
        return showUrl;
    }

    public ShowConsole getShowConsole() {
        return showConsole;
    }
    
    public void clearData(){
        source = "";
        sourceChanged = false;
        nativeSource = "";
        cookies = null;
        urlRedirections.clear();
        requestHeaders.clear();
        responseHeaders.clear();
        metas.clear();
        encodings.clear();
        encodings.put("encoding", "utf-8");
        contentType.clear();
        contentType.put("contentType", "text/html");
        networkFiles.clear();
        summaryInfos.clear();
    }

    public String getRealEncoding() {
        if (encodings.get("header") != null
                && false == encodings.get("header").equals("")) {
            return encodings.get("header");
        } else if (encodings.get("meta") != null
                && false == encodings.get("meta").equals("")) {
            return encodings.get("meta");
        }
        return encodings.get("encoding");
    }
    
    public String getRealContentType(){
        if (contentType.get("header") != null
                && false == contentType.get("header").equals("")) {
            return contentType.get("header");
        } else if (contentType.get("meta") != null
                && false == contentType.get("meta").equals("")) {
            return contentType.get("meta");
        }
        return contentType.get("encoding");
    }
}
