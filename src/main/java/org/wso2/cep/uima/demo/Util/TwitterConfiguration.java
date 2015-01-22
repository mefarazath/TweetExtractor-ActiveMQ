package org.wso2.cep.uima.demo.Util;

/**
 * Created by farazath on 1/23/15.
 */
public class TwitterConfiguration {

    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;
    private String userToSearch;
    private long[] followers;

    TwitterConfiguration(){

    }

    public String getConsumerKey() {
        return consumerKey;
    }
    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }
    public String getConsumerSecret() {
        return consumerSecret;
    }
    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }
    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    public String getUserToSearch() {
        return userToSearch;
    }
    public void setUserToSearch(String userToSearch) {
        this.userToSearch = userToSearch;
    }

    public long[] getFollowers() {
        return followers;
    }

    public void setFollowers(long[] followers) {
        this.followers = followers;
    }
}