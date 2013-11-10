package com.mkirimli.webtools;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author mkirimli
 */
public class Parser {

    public HashMap<String, String> alexaInfo(String url) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse("http://data.alexa.com/data?cli=10&dat=snbamz&url=" + url);
        doc.getDocumentElement().normalize();

        HashMap<String, String> data = new HashMap<String, String>();
        if( doc.getElementsByTagName("SD").getLength() > 0 ){
            NodeList rankChilds = doc.getElementsByTagName("SD").item(1).getChildNodes();
            String alexa_domain = rankChilds.item(1).getAttributes().getNamedItem("URL").getNodeValue();
            String alexa_rank = rankChilds.item(1).getAttributes().getNamedItem("TEXT").getNodeValue();
            String alexa_reach = rankChilds.item(3).getAttributes().getNamedItem("RANK").getNodeValue();
            String alexa_rankd = rankChilds.item(5).getAttributes().getNamedItem("DELTA").getNodeValue();
            String alexa_backlink = doc.getElementsByTagName("LINKSIN").item(0).getAttributes().getNamedItem("NUM").getNodeValue();
            String dmoz = doc.getElementsByTagName("DMOZ").getLength() > 0 ? "YES" : "NO";
            data.put("domain", alexa_domain);
            data.put("rank", formatNumber(alexa_rank));
            data.put("reach", alexa_reach);
            data.put("rank_delta", alexa_rankd);
            data.put("backlink", formatNumber(alexa_backlink));
            data.put("dmoz", dmoz);
        }else{
            data.put("domain", "0");
            data.put("rank", "0");
            data.put("reach", "0");
            data.put("rank_delta", "0");
            data.put("backlink", "0");
            data.put("dmoz", "N/A");
        }
        return data;
    }

    public HashMap<String, String> alexaCountry(String url) throws Exception{
        String alexa_link = "http://www.alexa.com/search?q=" + url + "&r=home_home&p=bigtop";
        String result = this.getSourceFromUrl(alexa_link);
        String country = "";
        String rank = "";
        if(result.contains("Traffic Rank in </span>")){
            String alexa = result.split("Traffic Rank in </span> <a href=\"/topsites/countries/")[1].split("</span>")[0].trim();
            country = alexa.split("\" title=\"")[0].trim();
            rank = alexa.split(":")[1].trim();
        }else if(result.contains("No regional data")){
            country = "N/A";
            rank = "0";
        }else{
            country = "-";
            rank = "-";
        }
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("country", country);
        data.put("rank", rank);
        
        return data;
    }
    
    public String googlePageRank(String url)throws Exception{
        String hash = PageRankHash.checksum(url);
        String parse_url = "http://toolbarqueries.google.com/tbr?client=navclient-auto&ch=" + hash 
                      +"&features=Rank&q=info:" + url + "&num=100&filter=0";
        String result = this.getSourceFromUrl(parse_url);
        if(result.contains("Rank_")){
           return result.split(":")[2] + "/10";   
        }else{
           return "N/A"; 
        }
    }
    
    public String googleIndex(String url) throws Exception{
        String backlink_url = "http://www.google.com/search?hl=en&lr=&ie=UTF-8&q=site:" + url + "&filter=0";
        String result = this.getSourceFromUrl(backlink_url);
        String google_index = "";
        if(result.contains("<div>About ")){
            google_index = result.split("<div>About ")[1].split(" result")[0].trim();
        }else if(result.contains("<div id=\"resultStats\">")){
            google_index = result.split("<div id=\"resultStats\">")[1].split(" result")[0].trim();
        }else if(result.contains("Advanced search</a></div><div>")){
            google_index = result.split("Advanced search</a></div><div>")[1].split(" result")[0].trim();
        }else if(result.contains("did not match any documents")){
            google_index = "0";
        }else{
            google_index = "-";
        }
        return google_index;
    }
    
    public String googleImageIndex(String url) throws Exception{
        String backlink_url = "http://www.google.com/images?hl=en&source=imghp&biw=1024&bih=383&q=site:"+url+"&gbv=2&aq=f&aqi=&aql=&oq=&gs_rfai=";
        String result = this.getSourceFromUrl(backlink_url);
        String google_index = "";
        if(result.contains("<div id=\"resultStats\">")){
            google_index = result.split("<div id=\"resultStats\">")[1].split(" result")[0].trim();
        }else if(result.contains("<div>About ")){
            google_index = result.split("<div>About ")[1].split(" result")[0].trim();
        }else if(result.contains("Advanced search</a></div><div>")){
            google_index = result.split("Advanced search</a></div><div>")[1].split(" result")[0].trim();
        }else if(result.contains("did not match any documents")){
            google_index = "0";
        }else{
            google_index = "-";
        }
        return google_index;
    }
    
    public String googleLastAccess(String url)throws Exception{
        String link = "http://webcache.googleusercontent.com/search?hl=en&q=cache:" + url + "&btnG=Google+Search&meta=";
        String result = this.getSourceFromUrl(link);
        String google_access;
        if(result.contains("That’s an error.")){
            google_access = "-";
        }else if(result.contains("GMT. The ") ){
            google_access = result.split(" GMT. The ")[0].split("it appeared on ")[1].trim();
        }else{
            google_access = "-";
        }
        return google_access;
    }
    
    public String googleBackLink(String url) throws Exception{
        String backlink_url = "http://www.google.com/search?hl=en&lr=&ie=UTF-8&q=link:" + url + "&filter=0";
        String result = this.getSourceFromUrl(backlink_url);
        String google_backlink = "";
        if(result.contains("did not match any documents")){
            google_backlink = "0";
        }else if(result.contains("Advanced search</a></div><div>About ")){
            google_backlink = result.split("Advanced search\\<\\/a\\>\\<\\/div\\>\\<div\\>About ", 0)[1].split(" results", 0)[0].trim();
        }else if(result.contains("\\<div id\\=\\\"resultStats\\\"\\>")){
            google_backlink = result.split("\\<div id\\=\\\"resultStats\\\"\\>About ")[1].split(" results\\<nobr\\>")[0].trim();
        }else if(result.contains("result")){
            google_backlink = result.split("result")[1].split("\\<div\\>")[1].replace("About ", "").trim();
        }else{
            google_backlink = "-";
        }
        return google_backlink;
    }
    
    /*public HashMap<String, String> adPlannerInfo(String url) throws Exception{
        URL uri = new URL(url);
        String host = uri.getHost(); 
        host = host.replaceFirst("www.", "");
        String parse_url = "https://www.google.com/adplanner/rpc/"
                          +"SiteDetailsService/getPlacementProfile?"
                          +"&request_pb=[null,[null,1,\"" + host
                          +"\",null,null,\"http://" + host
                          +"/\",[\"" + host
                          +"\"],0,null,null,[null,[null,2,null,"
                          +"[null,\"" + host
                          +"\"]],[null,1]]],\"001\",10]";
        String result = this.getSourceFromUrl(parse_url);

        HashMap<String, String> data = new HashMap<String, String>();
        if(result.equals("[[[,23]]]")){
            data.put("visits", "-");
            data.put("visitors", "-");
            data.put("pageview", "-");
            data.put("visits2", "-");
        }else{
            String adplanner_text = result.split(",\\[\\[,3,")[1].split("\\]")[0].trim();
            String adplanner_data[] = adplanner_text.split(",");
            
            data.put("visits", formatNumber(adplanner_data[0]));
            data.put("visitors", formatNumber(adplanner_data[1]));
            data.put("pageview", formatNumber(adplanner_data[2]));
            data.put("visits2", formatNumber(adplanner_data[6]));
        }
        
        return data;
    }*/
    
    public String formatNumber(String number){
        double drank = Double.parseDouble(number);
        DecimalFormat priceFormatter = new DecimalFormat("###,###.###");
        return priceFormatter.format(drank);
    }
    
    public String yahooIndex(String url) throws Exception{
        String backlink_url = "http://search.yahoo.com/search;_ylt=AtY44csZ9Xt1gHjt0jvGKD6bvZx4?p=site:"+url+"&toggle=1&cop=mss&ei=UTF-8&fr=yfp-t-701";
        String result = this.getSourceFromUrl(backlink_url);
        String yahoo_index;
        if(result.contains("resultCount")){
            yahoo_index = result.split("<span id=\"resultCount\">")[1].split("</span>")[0].trim();
        }else if(result.contains("We did not find results for") ){
            yahoo_index = "0";
        }else{
            yahoo_index = "-";
        }
        return yahoo_index;
    }
    
    public String bingIndex(String url) throws Exception{
        String backlink_url = "http://www.bing.com/search?q=site:" + url;
        String result = this.getSourceFromUrl(backlink_url);
        String bing_index;
        if(result.contains("<span class=\"sb_count\" id=\"count\">")){
            bing_index = result.split("<span class=\"sb_count\" id=\"count\">")[1].split(" result")[0].split(" of ")[1].trim();
        }else if(result.contains("No results found for")){
            bing_index = "0";
        }else{
            bing_index = "-";
        }
        return bing_index;
    }
    
    public String adCost(String url) throws Exception{
        URL uri = new URL(url);
        String host = uri.getHost();
        String adcost_url = "http://www.keywordspy.com/research/search.aspx?q=" + host + "&tab=domain-overview";
        String result = this.getSourceFromUrl(adcost_url);
        String adcost;
        if(result.contains("<td width=\"17%\"><b>$")){
            adcost = result.split("<td width=\"17%\"><b>\\$")[1].split("</b></td>")[0].trim();
            adcost = "$ " + formatNumber(Integer.toString(Integer.parseInt(adcost.replace(",", "")) * 30));
        }else if(result.contains("No Results Found")){
            adcost = "0";
        }else{
            adcost = "-";
        }
        return adcost;
    }
    
    public String facebookGetLikes(String id)throws Exception{
        String url = "http://graph.facebook.com/" + id;
        return this.getSourceFromUrl(url);
    }
    
    public boolean facebookValidate(String result, String host)throws Exception{
        String websites = result.split("\"website\": \"")[1].split("\",")[0];
        return websites.toLowerCase().contains(host.toLowerCase());
    }
    
    public boolean twitterValidate(String result, String host)throws Exception{
        String websites = result.split("\\<url\\>")[1].split("\\<\\/url\\>")[0];
        return websites.toLowerCase().contains(host.toLowerCase());
    }
    
    public HashMap<String, String> getFacebook(String url)throws Exception{
        URL uri = new URL(url);
        String host = uri.getAuthority();
        try{host = host.split("\\.", 0)[1];}catch(Exception e){}
        String gurl = "https://graph.facebook.com/search?q=" + host + "&type=page";
        String result = this.getSourceFromUrl(gurl);
        HashMap<String, String> data = new HashMap<String, String>();
        if( false == result.contains("id") ){
            data.put("FacebookLike", "0");
            data.put("FacebookTalking", "0");
        }else if( result.contains("id") && result.contains("\"category\": \"Website\",") ){
            String r[] = result.split("\\}")[0].split("\\{")[2].replace(" ", "").split("\\,");
            String fbid = r[2].replace("\"", "").split("id\\:")[1];
            String likes = facebookGetLikes(fbid);
            if(facebookValidate(likes, host)){
                String like = likes.split("\\\"likes\\\": ")[1].split(",")[0];
                String talking = likes.split("\\\"talking_about_count\\\"\\: ")[1].split("\\}")[0];
                data.put("FacebookLike", formatNumber(like));
                data.put("FacebookTalking", formatNumber(talking));
            }else{
                data.put("FacebookLike", "-");
                data.put("FacebookTalking", "-");
            }
        }else{
            data.put("FacebookLike", "-");
            data.put("FacebookTalking", "-");
        }
        return data;
    }
    
    public String getTwitter(String url)throws Exception{
        URL uri = new URL(url);
        String host = uri.getAuthority();
        try{host = host.split("\\.", 0)[1];}catch(Exception e){}
        String gurl = "https://api.twitter.com/1/users/lookup.xml?screen_name=" + host;
        String followers = "";
        String result = this.getSourceFromUrl(gurl);
        if( false == result.contains("followers_count") ){
            followers = "0";
        }else if( result.contains("followers_count") ){
            followers = result.split("\\<followers_count\\>")[1].split("\\<\\/followers_count\\>")[0].replace(" ", "");
            if(twitterValidate(result, host)){
                followers = formatNumber(followers);
            }else{
                followers = "-";
            }
        }else{
            followers = "-";
        }
        return followers;
    }
    
    public String getSourceFromUrl(String address) throws Exception{
        URL url = new URL(address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(30000);
        conn.addRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");

        InputStream is = conn.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;

        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            if (!in.ready()) {
                break;
            }
        }
        in.close();
        return response.toString();
    }
}
