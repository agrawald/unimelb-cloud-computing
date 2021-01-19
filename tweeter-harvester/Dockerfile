FROM python:3

WORKDIR /usr/src/app

RUN pip install tweepy
RUN pip install pandas
RUN pip install couchdb
RUN pip install afinn
RUN pip install textblob

COPY . .

CMD [ "python", "./tweeterHarvester.py" ]
