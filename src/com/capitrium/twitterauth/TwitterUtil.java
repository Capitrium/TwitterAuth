package com.capitrium.twitterauth;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * A singleton class used to connect to and get request/access tokens from
 * Twitter via their API.
 * 
 * <p>
 * Since it's a singleton, it will only generate one RequestToken until the
 * instance is garbage collected. It's possible for the instance to stay 
 * in-memory in certain instances, so just call  {@link #reset()} to force
 * the memory to be refreshed.
 * </p>
 *   
 * @author Zack Brenton
 */
public final class TwitterUtil {

    private RequestToken requestToken = null;
    private TwitterFactory twitterFactory = null;
    private Twitter twitter;

    public TwitterUtil() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true);
        configurationBuilder.setOAuthConsumerKey(TwitterValues.TWITTER_CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(TwitterValues.TWITTER_CONSUMER_SECRET);
        Configuration configuration = configurationBuilder.build();
        twitterFactory = new TwitterFactory(configuration);
        twitter = twitterFactory.getInstance();
    }

    public TwitterFactory getTwitterFactory() {
        return twitterFactory;
    }

    public void setTwitterFactory(AccessToken accessToken) {
        twitter = twitterFactory.getInstance(accessToken);
    }

    public Twitter getTwitter() {
        return twitter;
    }
    
    public RequestToken getRequestToken() {
        if (requestToken == null) {
            try {
                requestToken = twitterFactory.getInstance().getOAuthRequestToken(TwitterValues.TWITTER_CALLBACK_URL);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return requestToken;
    }

    static TwitterUtil instance = new TwitterUtil();

    public static TwitterUtil getInstance() {
        return instance;
    }

    public void reset() {
        instance = new TwitterUtil();
    }
}
