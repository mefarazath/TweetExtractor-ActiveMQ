package org.wso2.cep.uima.demo.Util;

import org.apache.log4j.Logger;
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

import static java.lang.System.exit;

/**
 * Created by Vidura on 1/23/15.
 */

public class TwitterConfigurationBuiler {
    private static String CONFIGURATION_FILE = "twitterConfig.xml";
    private static Logger logger = Logger.getLogger(TwitterConfigurationBuiler.class);


    private TwitterConfigurationBuiler(){

    }

    /***
     * Method to parse and return the twitter keys configuration file
     * @return TwitterConfiguration twitter configuration object with relevant parameters set
     */
    public static TwitterConfiguration getTwitterConfiguration(){

        TwitterConfiguration twitterConfiguration = new TwitterConfiguration();
        File xmlFile = new File(CONFIGURATION_FILE);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("Error When Configuring the parser for "+TwitterConfigurationBuiler.class.getName(),e);
            exit(1);
        }
        Document doc = null;

        try {
            doc = dBuilder.parse(xmlFile);
        } catch (IOException e) {
            logger.error("Error Reading the Twitter Configuration File : twitterConfig.xml ",e);
            exit(1);
        } catch (SAXException e) {
            logger.error("Error Parsing Twitter Configuration File : twitterConfig.xml ",e);
            exit(1);
        }

        doc.getDocumentElement().normalize();

        //get API keys
        NodeList nodeList = doc.getElementsByTagName("TwitterAPIKeys");
        Node nNode = nodeList.item(0);

        Element eElement = (Element) nNode;
        twitterConfiguration.setConsumerKey(eElement.getElementsByTagName("consumerKey").item(0).getTextContent());
        twitterConfiguration.setConsumerSecret(eElement.getElementsByTagName("consumerSecret").item(0).getTextContent());
        twitterConfiguration.setAccessToken(eElement.getElementsByTagName("accessToken").item(0).getTextContent());
        twitterConfiguration.setAccessTokenSecret(eElement.getElementsByTagName("accessTokenSecret").item(0).getTextContent());

        //get user to search for
        NodeList nodeList2 = doc.getElementsByTagName("TwitterConfiguration");
        Node userToSearchNode = nodeList2.item(0);
        Element userToSearchElement = (Element) userToSearchNode;
        twitterConfiguration.setUserToSearch(userToSearchElement.getElementsByTagName("userToSearch").item(0).getTextContent());

        NodeList nodeList3 = doc.getElementsByTagName("StreamingConfiguration");
        Node followersNode = nodeList3.item(0);
        Element followersElement = (Element) followersNode;
        String strFollowers = followersElement.getElementsByTagName("followers").item(0).getTextContent();
        twitterConfiguration.setFollowers(getFollowers(strFollowers));

        return twitterConfiguration;
    }


    /***
     * Utility Method to get followers IDs as an Array from a comma separated String
     * @param followers String - comma separated String of IDs to follow in the stream
     * @return long[] Long array of IDs of users to follow
     */
    private static long[] getFollowers(String followers){

        String[] split = followers.split(",");
        long[] followerId = new long[split.length];

        for (int i = 0; i < split.length; i++){
            followerId[i] = Long.parseLong(split[i].trim());
        }
        return followerId;
    }
    

}


