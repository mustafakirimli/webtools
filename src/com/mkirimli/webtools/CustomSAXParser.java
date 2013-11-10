package com.mkirimli.webtools;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class CustomSAXParser extends DefaultHandler {
    // PWT nesnesine erişir
    private PWT pwt;
    String tempVal;
    String tempTag;
    boolean titleOpened = false;

    public CustomSAXParser(PWT pwt) {
        this.pwt = pwt;
    }

    public void parseDocument(String source) {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(new ByteArrayInputStream(source.getBytes(pwt.getRealEncoding())), this);

        } catch (SAXException se) {
            //se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            //pce.printStackTrace();
        } catch (IOException ie) {
            //ie.printStackTrace();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    //Event Handlers
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("meta")) {
            tempTag = attributes.getValue("http-equiv") == null ? 
                      attributes.getValue("name") :
                      attributes.getValue("http-equiv");
            tempTag = tempTag == null ? 
                      attributes.getValue("property") : tempTag;
            pwt.addMeta(tempTag, attributes.getValue("content"));
            if( tempTag.equalsIgnoreCase("Content-Type") ){
                pwt.addEncoding("meta", attributes.getValue("content").split(";")[1].split("=")[1]);
                pwt.addContentType("meta", attributes.getValue("content").split(";")[0]);
            }
        }else if(qName.equalsIgnoreCase("title")){
            titleOpened = true;
        }else if(qName.equalsIgnoreCase("body")){
            endDocument();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(titleOpened){
            tempVal = new String(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if(qName.equalsIgnoreCase("title")){
            pwt.addMeta("title", tempVal);
            titleOpened = false;
        }
    }
}
