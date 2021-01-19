# Author: Dheeraj Agrawal <agrawald@student.unimelb.edu.au>

import json
import logging
import couchdb

# intialize logger
logger = logging.getLogger('aurin-harvester')
logging.basicConfig(level=logging.DEBUG)
logger.setLevel(logging.WARNING)

# Intialize DB settings
db_file_map = {'db_visit_to_green_places': 'visit_to_green_places.json',
               'db_work_life_balance': 'work_life_balance.json',
               'db_house_rent_weekly': 'house_rent_weekly.json',
               'db_median_and_average': 'median_and_average.json',
               'db_total_household_income_by_family_composition': 'total_household_income_by_family_composition.json',
               'db_weekly_income_by_sex': 'weekly_income_by_sex.json'}
server_location = "http://localhost:5984/"

# API Authentication details
USER = "Dheeraj Agrawal <agrawald@student.unimelb.edu.au>"


# load json file
def load_json_file(file_name):
    with open(file_name) as data_file:
        data = json.load(data_file)
    return data["features"]


# normalize feature
def normalize_feature(a_feature):
    return {'coordinates': a_feature['geometry']['coordinates'][0][0], 'properties': a_feature['properties']}


# DB connection function
try:
    couch = couchdb.Server(server_location)
    # store a record in couchdb
    for db_name, data_file in db_file_map.items():
        if db_name not in couch:
            logger.info("Create DB: " + db_name)
            couch.create(db_name)
        features = load_json_file(data_file)
        for feature in features:
            couch[db_name].save(normalize_feature(feature))
        logger.info(db_name + " loaded with " + data_file)
except Exception as ex:
    logger.error("DB Connection failed", ex)
