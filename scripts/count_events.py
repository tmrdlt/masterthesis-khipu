import pandas as pd
import psycopg2 as pg

engine = pg.connect("dbname='workflowdb' user='dev' host='localhost' port='5432' password='OurLocalPassword'")

task_1_1_id = "8594e3a1-06e6-453a-97a6-2d8d239faf58"

df = pd.read_sql("select * from workflow.event where user_api_id = '%s'" % task_1_1_id, con=engine)

print("Total events for Task 1.1: %s" % len(df.index))

print(df.groupby('event_type').size())
