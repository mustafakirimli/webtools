package com.mkirimli.webtools;

import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 *
 *  Web tabının içeriği
 * 
 * @author mustafa
 */
public class ShowWeb {

    // PWT nesnesine erişir
    private PWT pwt;
    
    // Web tabinde bulunan browser alani
    WebView engine;
   
    // Tabin daha önce görünüp görünmediğini tutar
    private boolean isShow = false;
    
    // Tab 'in hazır olup olmadığı (data bekliyor olabilir)
    private boolean isReady = false;
    
    // Console fix
    private boolean isFixed = false;

    /**
     * Consructor
     * @param pwt PWT nesnesi 
     */
    public ShowWeb(PWT pwt) {
        this.pwt = pwt;
    }

    public WebView getEngine() {
        return engine;
    }

    public void setEngine(WebView engine) {
        this.engine = engine;
    }
    
    /**
     * Task lerden gelen veriyi set eder
     */
    public void ready(){
        engine.getSettings().setJavaScriptEnabled(true);
        engine.getSettings().setBuiltInZoomControls(true);
        engine.getSettings().setSupportZoom(true);
        engine.getSettings().setUseWideViewPort(true);
        engine.getSettings().setDefaultZoom(ZoomDensity.CLOSE);

        engine.setWebViewClient(new ShowWebViewClient());
        engine.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                try {
                    if (cm.sourceId().equals("")
                            || cm.sourceId() == null
                            || cm.sourceId().equalsIgnoreCase("undefined")) {
                        pwt.getShowConsole().showConsole(cm.message());
                    } else {
                        pwt.getShowConsole().showConsole(
                                cm.message()
                                + " -- From line "
                                + cm.lineNumber()
                                + " of " + cm.sourceId());
                    }
                } catch (NullPointerException e) {}
                return true;
            }
        });

        /*engine.setPictureListener(new PictureListener() {
        
        public void onNewPicture(WebView view, Picture picture) {
        Log.d("A", "onNewPicture- Height" + picture.getHeight());
        Log.d("B", "onNewPicture- Width" + picture.getWidth());
        }            
        });*/
        
        isReady = true;
        if(isShow){ // tab e daha önce girildiyse task bitince veriyi güncelle
            display();
        }
    }
    
    public void consoleCommand(String commandText) {
       commandText = commandText.replace("\"", "\\\"");
       commandText = commandText.replace("\n", "\\n");
       engine.loadUrl("javascript:window.console.log(eval(\"" + commandText + "\"))");
    }

    private class ShowWebViewClient extends WebViewClient {

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            // TODO : network tab, yüklenen tum içerikler burada gorunuyor
            pwt.getShowNetwork().parseAndDisplay(url);
            // engine.loadUrl("javascript:window.console.log(\"" + pwt.tr(R.string.resource) + ":\\n" + url + "\")");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            engine.loadUrl("javascript:window.console.log(\"url:" + url + "\")");
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(false == isFixed){
                 String jsFix = "iframe = document.createElement('iframe');"
                                + "iframe.style.display = 'none';"
                                + "document.getElementsByTagName('body')[0].appendChild(iframe);"
                                + "window.console = iframe.contentWindow.console;";
                consoleCommand(jsFix);
                isFixed = true;
            }
            /* This call inject JavaScript into the page which just finished loading. */
            engine.loadUrl("javascript:window.console.log(\"" + pwt.tr(R.string.console_loaded) + "\")");
        }
    }
    
    /**
     * Tab 'e içeriğin (datanın) yüklenmesini sağlar
     */
    public void display() {
        // TODO: encoding change
        engine.loadDataWithBaseURL(pwt.getUrl(),
                pwt.getSource(),
                pwt.getRealContentType(),
                pwt.getEncodings().get("encoding"),
                null);
    }
    
    /**
     * Tab 'de bulunan dataları temizler
     */
    public void clear(){
        engine.clearView();
    }
    
    /**
     * Tab değiştiğinde çalışır
     * @param tab id
     */
    public void tabChanged(String tabId){
        // içerik yüklenmemişse yükle
        if(tabId.equals("web") && isShow == false && isReady){
            display();
            isShow = true;
        }
    }
}
