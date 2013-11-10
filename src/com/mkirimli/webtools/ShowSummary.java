package com.mkirimli.webtools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mustafa
 */
final class ShowSummary {
    
    // PWT nesnesine erişir
    private PWT pwt;
    private ExpandableListView summaryList;
    private ArrayList<String> groups;
    private ArrayList<ArrayList<String>> children;
    BaseExpandableListAdapter expListAdapter;
    public HashMap<String, HashMap<String, String>> seoData;
    public Parser parser;
    String groupNames[];
    String childNames[][];
    
    // Tabin daha önce görünüp görünmediğini tutar
    private boolean isShow = false;
    
    // Tab 'in hazır olup olmadığı (data bekliyor olabilir)
    private boolean isReady = false;
    
     /**
     * Consructor
     * @param pwt PWT nesnesi 
     */
    public ShowSummary(PWT pwt) {
        this.pwt = pwt;
        this.groupNames = new String[]{  pwt.tr(R.string.Ranks),
                                         pwt.tr(R.string.Indexes),
                                         //pwt.tr(R.string.VisitorsMonthly),
                                         pwt.tr(R.string.Backlinks),
                                         pwt.tr(R.string.Other),
                                         pwt.tr(R.string.Social)
                                      };
        this.childNames = new String[][]{
                             {pwt.tr(R.string.GooglePR), pwt.tr(R.string.Alexa) + " *", pwt.tr(R.string.AlexaCountry) + " *"},
                             {pwt.tr(R.string.Google), pwt.tr(R.string.GoogleImage), pwt.tr(R.string.Yahoo), pwt.tr(R.string.Bing)},
                             //{pwt.tr(R.string.Visits) + " *", pwt.tr(R.string.Visitors) + " *", pwt.tr(R.string.PageViews) + " *"},
                             {pwt.tr(R.string.Google), pwt.tr(R.string.Alexa) + " *"},
                             {pwt.tr(R.string.AdCost) + " *", pwt.tr(R.string.DMOZ) + " *", pwt.tr(R.string.LastAccess)},
                             {pwt.tr(R.string.FacebookLike) + " *", pwt.tr(R.string.FacebookTalking) + " *", pwt.tr(R.string.Twitter) + " *"}
                          };
        this.parser = new Parser();
        setSeoDataDefaults();
    }
    
    public void setSeoDataDefaults(){
        this.seoData = new HashMap<String, HashMap<String, String>>();
        for (int i = 0; i < groupNames.length; i++) {
            HashMap<String, String> hMap = new HashMap<String, String>();
            for (int j = 0; j < childNames[i].length; j++) {
                hMap.put((String)childNames[i][j], null);
                
            }
            this.seoData.put((String)groupNames[i], hMap);
            
        }
    }

    public ExpandableListView getSummaryList() {
        return summaryList;
    }

    public void setSummaryDetails(ExpandableListView summaryList) {
        this.summaryList = summaryList;
                    
        // Gruplari ayarla
        this.groups = new ArrayList<String>(5);
        for (int i = 0; i < groupNames.length; i++) {
            this.groups.add((String) groupNames[i]);
        }
        
        this.children = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < childNames.length; i++) {
            ArrayList<String> arrList = new ArrayList<String>();
            String[] strings = childNames[i];
            for (int j = 0; j < strings.length; j++) {
                arrList.add((String)strings[j]);
                
            }
            this.children.add(arrList);
            
        }

        this.expListAdapter = new BaseExpandableListAdapter() {
            
            public int getGroupCount() {
                return groups.size();
            }

            public int getChildrenCount(int groupPosition) {
                return children.get(groupPosition).size();
            }

            public Object getGroup(int groupPosition) {
                return groups.get(groupPosition);
            }

            public Object getChild(int groupPosition, int childPosition) {
                return children.get(groupPosition).get(childPosition);
            }

            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            public boolean hasStableIds() {
                return true;
            }

            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                String group = (String) getGroup(groupPosition);
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) pwt.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.show_summary_group, null);
                }
                
                boolean isEmpty = false;
                for (int i = 0; i < childNames[groupPosition].length; i++) {
                    String cname = childNames[groupPosition][i];
                    String childValue = pwt.getShowSummary().seoData.get(group).get(cname);
                    if(childValue == null){
                        isEmpty = true;
                        break;
                    }
                }
                ProgressBar pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
                if(isEmpty == false){
                    pb.setVisibility(View.INVISIBLE);
                }
                
                TextView tv = (TextView) convertView.findViewById(R.id.tvGroup);
                tv.setText(group);
                return convertView;
            }

            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                String child = (String) getChild(groupPosition, childPosition);
                String group = (String) getGroup(groupPosition);
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) pwt.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.show_summary_child, null);
                }
                TextView tv = (TextView) convertView.findViewById(R.id.tvGroup2);
                tv.setText("   " + child);

                TextView tv2 = (TextView) convertView.findViewById(R.id.childValue);
                tv2.setText("");
                
                // Depending upon the child type, set the imageTextView01
                tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                tv2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                
                String childValue = (String)pwt.getShowSummary().seoData.get(group).get(child);
                if(childValue == null){
                    tv2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.loader, 0);
                }else{
                    tv2.setText(childValue);
                }
                 
                if (child.startsWith("Google")) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.google, 0, 0, 0);
                }else if (child.startsWith("Yahoo")) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.yahoo, 0, 0, 0);
                }else if (child.startsWith("Alexa")) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.alexa, 0, 0, 0);
                }else if (child.startsWith("Bing")) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bing, 0, 0, 0);
                }else if(child.startsWith("Ad Cost *")){
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dollar, 0, 0, 0);
                }else{
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.other, 0, 0, 0);
                }
                return convertView;
            }

            public boolean isChildSelectable(int arg0, int arg1) {
                return true;
            }
        };
        this.summaryList.setAdapter( expListAdapter );
        this.summaryList.expandGroup(0);
        this.summaryList.expandGroup(1);
        this.summaryList.expandGroup(2);
        
    }
    
    /**
     * Task lerden gelen veriyi set eder
     * @param result 
     */
    public void ready(String result){
        isReady = true;
        if(isShow){ // tab e daha önce girildiyse task bitince veriyi güncelle
            display();
        }
    }
    
    public void display() {
    }
    
    public void clear(){
        this.seoData.clear();
        this.setSeoDataDefaults();
    }
    
    /**
     * Tab değiştiğinde çalışır
     * @param tab id
     */
    public void tabChanged(String tabId){
        // içerik yüklenmemişse yükle
        if(tabId.equals("summary") && isShow == false && isReady){
            display();
            isShow = true;
        }
    }
}
