package org.wso2.cep.uima.demo;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.PropertyConfigurator;
import org.wso2.cep.uima.demo.Util.Tweet;
import org.wso2.cep.uima.demo.Util.TwitterConfiguration;
import org.wso2.cep.uima.demo.Util.TwitterConfigurationBuiler;
import org.xml.sax.SAXException;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.jms.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by farazath on 12/22/14.
 */
public class TweetExtractor {

    private ConfigurationBuilder cb;
    private Twitter twitterApp;

    private ArrayList<Tweet> tweetList = new ArrayList<>();
    private static Logger logger = Logger.getLogger(TweetExtractor.class);
    private String userToSearch;
    private String JMSUrl;
    private String queueName;


    public TweetExtractor(String JMSUrl, String queueName) throws ParserConfigurationException, SAXException, IOException {

        this.JMSUrl = JMSUrl;
        this.queueName = queueName;

        buildConfiguration();   // set the API keys to the Config Builder
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitterApp = tf.getInstance();

        PropertyConfigurator.configure("src/main/resources/log4j.properties");
    }

    public static void main(String[] args) throws JMSException, IOException, SAXException, ParserConfigurationException {

        if(args.length != 2 || args[0].equals("") || args[1].equals("")){
            System.out.println("Usage ant -DjmsUrl=<JMS_URL> -DqueueName=<QUEUE_NAME>");
            System.exit(0);
        }


        TweetExtractor extractor = new TweetExtractor(args[0],args[1]);
        extractor.retrieveTweets();
        logger.info("Total Tweets Extracted: " + extractor.tweetList.size());

        // create the factory for ActiveMQ connection
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(extractor.JMSUrl);
        Connection connection = factory.createConnection();
        connection.start();
        logger.info("ActiveMQ connection started for TweetExtractor successfully");

        // Create a non-transactional session with automatic acknowledgement
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create a reference to the queue test_queue in this session.
        Queue queue = session.createQueue(extractor.queueName);

        // Create a producer for queue
        MessageProducer producer = session.createProducer(queue);
        logger.info("ActiveMQ producer successfully created for TwitterExtractor");

        logger.info("ActiveMQ producer enqueuing extracted Tweets");

        for(Tweet t: extractor.tweetList){
            TextMessage tweetMessage = session.createTextMessage(t.getText());
            producer.send(tweetMessage);
            logger.debug(tweetMessage.getText()+" successfully sent");
        }

        logger.info("ActiveMQ Tweet Extractor enqueued "+extractor.tweetList.size()+" messages");
        // Stop the connection — good practice but redundant here
        producer.close();
        logger.info("Producer Closed");
        connection.stop();
        logger.info("Connection Stopped");

        System.exit(0);
    }


    /***
     * Method to retrieve tweets from the userToSearch user's timeline
     * Only a maximum of ~3200 tweets can be extracted due to API limitations
     */
    private void retrieveTweets(){

        Paging paging;

        // set the lowest value of the tweet ID initially to one less than Long.MAX_VALUE
        long min_id = Long.MAX_VALUE - 1;
        int count;
        int index = 0;

        logger.info("Started Extracting Tweets of "+ userToSearch);
        // iterate through the timeline untill the iteration returns no tweets
        while (true) {
            try {

                count = tweetList.size();

                // paging tweets at a rate of 100 per page
                paging = new Paging(1, 100);

                // if this is not the first iteration set the new min_id value for the page
                if (count != 0)
                    logger.info("Extracted Tweet Count : "+count);
                    paging.setMaxId(min_id - 1);

                // get a page of the tweet timeline with tweets with ids less than the min_id value
                List<Status> results = twitterApp.getUserTimeline(userToSearch, paging);

                // iterate the results and add to tweetList
                for (Status s : results) {
                    Tweet tweet = new Tweet(s.getId(),s.getCreatedAt(),s.getText());
                    tweetList.add(tweet);
                    Logger.getLogger(TweetExtractor.class).debug(" " + (index++) + " " + tweet.toString());

                    // set the value for the min value for the next iteration
                    if (s.getId() < min_id) {
                        min_id = s.getId();
                    }
                }

                // if the results for this iteration is zero, means we have reached the API limit or we have extracted the maximum
                // possible, so break
                if (results.size() == 0) {
                    break;
                }

            } catch (TwitterException e) {
                e.printStackTrace();
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

    }


    /***
     *  Method to set up the API keys for the configuration builder
     */
    private void buildConfiguration() throws IOException, SAXException, ParserConfigurationException {
        cb = new ConfigurationBuilder();
        Logger.getLogger(TweetExtractor.class).debug("Building Configuration");

        TwitterConfiguration config = TwitterConfigurationBuiler.getTwitterConfiguration();

        String consumerKey = config.getConsumerKey();
        String consumerSecret = config.getConsumerSecret();
        String accessToken = config.getAccessToken();
        String accessTokenSecret = config.getAccessTokenSecret();
        userToSearch = config.getUserToSearch();

        if(consumerKey == null || consumerSecret == null || accessToken == null || accessTokenSecret == null) {
            logger.error("Twitter API Keys have not been set properly in twitterConfig.xml");
            throw new NullPointerException("TWitter API Keys not set");
        }

        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
    }



}
