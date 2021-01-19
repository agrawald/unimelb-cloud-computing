# Author:SD SHARUKH

import sys
import tweepy
import pandas as pd
import json
import logging
import time
import couchdb
import re
from afinn import Afinn
import textblob as textBlob





# intialize logger
logger = logging.getLogger('SociealmediaAnalysis')
logging.basicConfig(level=logging.DEBUG)
logger.setLevel(logging.WARNING)

# Intialize DB settings

db_name = 'db_tweeter'
server_location = "http://localhost:5984/"

# API Authentication details
USER = "Sharukh"

# twitter's app credentials
consumer_key = "nQq0n6emrIgJp1HL58ygIgJjA"
consumer_secret = "nDCvPlPRdyisbqO5Qf4s3DhB4ldMJpeBIDowx01c2MbmDKFG8k"
access_key = "804136640293650432-bJXOJL0HhCsbZhlOTcyCWrMcXxVJCdd"
access_secret = "JpWAywXJnoHF2D7qqKXcH97b1nG4JH5oXfSt3u7f8EIW6"

# Intialize twitter API


try:
    auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_key, access_secret)
    api = tweepy.API(auth)
except Exception as ex:
    logger.error("Twiiter API authentication failure")

# DB connection function


try:
    couch = couchdb.Server(server_location)
    db = couch[db_name]

except Exception as ex:
    logger.error("DB Connection failed")


# preprocessor tweet cleaning options
def super_clean_tweet(self, tweet):
    '''
    Utility function to clean tweet text by removing links, special characters
    using simple regex statements.
    '''
    pattern1 =r"(@[A-Za-z0-9]+)|([^0-9A-Za-z \t])|(\w+:\/\/\S+)"
    try:
        result = re.sub(pattern1, "", tweet)
        return result
    except Exception as ex:
        logger.warning("cleaning failure")


def clean_tweet(tweet):
    '''
    Utility function to clean the text in a tweet by removing
    links using regex.
    '''
    pattern2 = r"http\S+"
    try:
        result = re.sub(pattern2, "", tweet)
        return result
    except Exception as ex:
        logger.warning("cleaning failure")


def get_sentiment_score(tweet, lang):
    try:
        clean_text = clean_tweet(tweet)
        afinn = Afinn(language=lang)
        sentiment = afinn.score(clean_text)
        return sentiment

    except:
        try:
            logger.debug("Using TextBlob for analysis")
            sc_tweet =super_clean_tweet(tweet)
            analysis = textBlob(sc_tweet)

            # set sentiment
            if analysis.sentiment.polarity > 0:
                return 1
            elif analysis.sentiment.polarity == 0:
                return 0
            else:
                return -1
        except:
            logger.error("Error: Failed to get sentiment"+tweet)


# Get Twitter data stream to flow in this    for data analysis and data saving into CouchDB
# Custom stream listener fetches data according to the filters set and saves them to database
class CustomStreamListener(tweepy.StreamListener):
    # A listener handles tweets received from the stream.
    # This is a custom listener that store received tweets to FILE.
    def on_data(self, tweet_data):
        try:
            # converts to json format then saves in couchdb
            tweets_json = json.loads(tweet_data)
            doc_id = tweets_json["id_str"]
            tweet_lang = tweets_json["lang"]
            sentiment_score = get_sentiment_score(tweets_json["text"], tweet_lang)
            if sentiment_score is None:
                # if invalid language, topic and sentiment is not added to document
                doc = {"_id": doc_id, "tweet_data": tweets_json, "sentiment": 'No sentiment found'}
            else:
                doc = {"_id": doc_id, "tweet_data": tweets_json, "sentiment": sentiment_score}

                # saves the document to database
            db.save(doc)
            logger.info('added: ' + doc_id)
            return True
        except BaseException as e:
            logger.warning(e)
            time.sleep(5)
        except couchdb.http.ResourceConflict:
            # handle duplicates
            time.sleep(5)

        def on_status(self, status):
            logger.info(status.author.screen_name, status.created_at, status.text)


# Initiate Twitter streaming and save data to Database
def call_stream():
    try:
        streamingAPI = tweepy.streaming.Stream(auth, CustomStreamListener())
        # Add filter of melbourne location co-ordinates here
        streamingAPI.filter(track=['russia'])

    except:
        logger.error("Streaming failed")


# Read data from DB view to keep track of live data in pandas dataframe
def fetch_db_data():
    try:
        rows = db.view('_all_docs', include_docs=True)
        data = [row['doc'] for row in rows]
        # use pandas dataframes for better data analytics maybe or use webservices
        df = pd.DataFrame(data)
        print(df)
    except:
        logger.error("DB data fetch failure")

#use call_stream() to stream tweets get sentiment and save to CouchDB -> remove # in below line to stream twitter data and save it into your database and # to this if u want to use fetch_db_data()
call_stream()

#use fetch_db_data() to view all tweets with sentiments saved in database -> remove # in below line to view db data and # to this if u want to use call_stream()
#fetch_db_data()
