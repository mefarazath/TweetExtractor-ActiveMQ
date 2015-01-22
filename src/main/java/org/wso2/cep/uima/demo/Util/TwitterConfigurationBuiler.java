package org.wso2.cep.uima.demo.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Vidura on 1/23/15.
 */
public class TwitterConfigurationBuiler {
    private String filePath;


    public TwitterConfigurationBuiler(String filePath){
        this.filePath = filePath;
    }


    public TwitterConfiguration getSearchConfiguration() throws ParserConfigurationException, SAXException, IOException {

        TwitterConfiguration testConfig = new TwitterConfiguration();
        File fXmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        doc.getDocumentElement().normalize();

        //get keys
        NodeList nList = doc.getElementsByTagName("TwitterAPIKeys");
        Node nNode = nList.item(0);
        Element eElement = (Element) nNode;
        testConfig.setConsumerKey(eElement.getElementsByTagName("consumerKey").item(0).getTextContent());
        testConfig.setConsumerSecret(eElement.getElementsByTagName("consumerSecret").item(0).getTextContent());
        testConfig.setAccessToken(eElement.getElementsByTagName("accessToken").item(0).getTextContent());
        testConfig.setAccessTokenSecret(eElement.getElementsByTagName("accessTokenSecret").item(0).getTextContent());

        //get user to search
        NodeList n2List = doc.getElementsByTagName("TwitterConfiguration");
        Node userToSearchNode = n2List.item(0);
        Element userToSearchElement = (Element) userToSearchNode;
        testConfig.setUserToSearch(userToSearchElement.getElementsByTagName("userToSearch").item(0).getTextContent());

        return testConfig;
    }


    public TwitterConfiguration getStreamingConfiguration() throws ParserConfigurationException, IOException, SAXException {
        TwitterConfiguration testConfig = new TwitterConfiguration();
        File fXmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        doc.getDocumentElement().normalize();

        //get keys
        NodeList nList = doc.getElementsByTagName("TwitterAPIKeys");
        Node nNode = nList.item(0);
        Element eElement = (Element) nNode;
        testConfig.setConsumerKey(eElement.getElementsByTagName("consumerKey").item(0).getTextContent());
        testConfig.setConsumerSecret(eElement.getElementsByTagName("consumerSecret").item(0).getTextContent());
        testConfig.setAccessToken(eElement.getElementsByTagName("accessToken").item(0).getTextContent());
        testConfig.setAccessTokenSecret(eElement.getElementsByTagName("accessTokenSecret").item(0).getTextContent());

        //get followers
        NodeList n1List = doc.getElementsByTagName("StreamingConfiguration");
        Node followersNode = n1List.item(0);
        Element followersElement = (Element) followersNode;
        String strFollowers = followersElement.getElementsByTagName("followers").item(0).getTextContent();
        testConfig.setFollowers(getFollowers(strFollowers));

        return testConfig;
    }

    private static long[] getFollowers(String followers){

        String[] theFollowers = followers.split(",");
        long[] answer = new long[theFollowers.length];

        for (int i = 0; i < theFollowers.length; i++){
            answer[i] = Long.parseLong(theFollowers[i].trim());
        }
        return answer;
    }
    
    




}


