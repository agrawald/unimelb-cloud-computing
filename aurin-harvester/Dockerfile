FROM python:3

WORKDIR /usr/src/app

RUN pip install couchdb

COPY . .

CMD [ "python", "./aurinHarvester.py" ]
