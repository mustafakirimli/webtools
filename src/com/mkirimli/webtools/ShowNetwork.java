package com.mkirimli.webtools;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;
import java.net.URL;
import java.util.Map;

/**
 *
 * @author mkirimli
 */
public class ShowNetwork{

    // PWT nesnesine erişir
    private PWT pwt;
    
    // Network list
    private ListView networkList;
    
    // Buttons
    private ToggleButton cssButton;
    private ToggleButton imgButton;
    private ToggleButton jsButton;
    private ToggleButton otherButton;
    private ToggleButton allButton;
    
    // Show only 
    private String listOnly = "any";
    
    // ListView Adapter
    ArrayAdapter<String> networkAdapter;

    
    // Tabin daha önce görünüp görünmediğini tutar
    private boolean isShow = false;
    
    // Tab 'in hazır olup olmadığı (data bekliyor olabilir)
    private boolean isReady = false;

    /**
     * Consructor
     * @param pwt PWT nesnesi 
     */
    public ShowNetwork(PWT pwt) {
        this.pwt = pwt;
    }

    /**
     * 
     * @return 
     */
    public ListView getNetworkList() {
        return networkList;
    }

    public void uncheckAll(ToggleButton exclude){
        this.cssButton.setChecked(false);
        this.imgButton.setChecked(false);
        this.jsButton.setChecked(false);
        this.otherButton.setChecked(false);
        this.allButton.setChecked(false);
        exclude.setChecked(true);
    }
    
    /**
     * 
     * @param networkList
     * @param cssButton
     * @param imgButton
     * @param jsButton
     * @param otherButton
     * @param allButton 
     */
    public void setNetworkDetails(ListView networkList, ToggleButton cssButton, ToggleButton imgButton, ToggleButton jsButton, ToggleButton otherButton, ToggleButton allButton) {
        this.networkList = networkList;
        networkAdapter = new ArrayAdapter<String>(pwt.getApplicationContext(), R.layout.show_network_row);
        this.networkList.setAdapter(networkAdapter);
        this.cssButton = cssButton;
        this.imgButton = imgButton;
        this.jsButton = jsButton;
        this.otherButton = otherButton;
        this.allButton = allButton;
        
        this.cssButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                uncheckAll(ShowNetwork.this.cssButton);
                if(ShowNetwork.this.cssButton.isChecked()){
                    listOnly = "css";
                } else{
                    listOnly = "any";
                }
                display();
            }
        });
        this.imgButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                uncheckAll(ShowNetwork.this.imgButton);
                if(ShowNetwork.this.imgButton.isChecked()){
                    listOnly = "img";
                } else{
                    listOnly = "any";
                } 
                display();
            }
        });
        this.jsButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                uncheckAll(ShowNetwork.this.jsButton);
                if(ShowNetwork.this.jsButton.isChecked()){
                    listOnly = "js";
                } else{
                    listOnly = "any";
                }
                display();
            }
        });
        this.otherButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                uncheckAll(ShowNetwork.this.otherButton);
                if(ShowNetwork.this.otherButton.isChecked()){
                    listOnly = "other";
                }else{
                    listOnly = "any";
                }
                display();
            }
        });
        this.allButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                uncheckAll(ShowNetwork.this.allButton);
                if(ShowNetwork.this.allButton.isChecked()){
                    listOnly = "any";
                }else{
                    listOnly = "";
                }
                display();
            }
        });
    }
    
    /**
     * Task lerden gelen veriyi set eder
     */
    public void ready(){
        isReady = true;
        if(isShow){ // tab e daha önce girildiyse task bitince veriyi güncelle
            display();
        }
    }
    
    public void parseAndDisplay(String url){
        String ext = "";
        try {
            URL aURL = new URL(url);
            String filename = aURL.getFile();
            int mid = filename.lastIndexOf(".");
            ext = filename.substring(mid + 1, filename.length());
        } catch (Exception e) {
        }
        
        if ( ext.equals("png") || ext.equals("gif") || ext.equals("jpeg")
                || ext.equals("jpg") ) {
            ext = "img";
        }else if( ext.equals("js") ){
            ext = "js";
        }else if( ext.equals("css") ){
            ext = "css";
        }else{
            ext = "other";
        }
        pwt.addNetworkFile(url, ext);
        if( listOnly.equals("any") || listOnly.equals(ext) ){
            networkAdapter.add(ext + " : " + url);
        }
    }
    
    public void display() {
        networkAdapter.clear();
        for (Map.Entry<String, String> item : pwt.getNetworkFiles().entrySet()) {
            String key = item.getKey();
            String value = item.getValue();
            //networkAdapter.add(key + " : " + value);
            if ( false == listOnly.equals("") &&
                (listOnly.equals("any") || listOnly.equals(value)) ) {
                networkAdapter.add(value + " : " + key);
            }
        }
    }
    
    public void clear(){
        networkAdapter.clear();
    }
    
    /**
     * Tab değiştiğinde çalışır
     * @param tab id
     */
    public void tabChanged(String tabId){
        // içerik yüklenmemişse yükle
        if(tabId.equals("network") && isShow == false && isReady){
            display();
            isShow = true;
            // Bağlantılı tableri yükle
            pwt.getShowWeb().display();
        }
    }
}