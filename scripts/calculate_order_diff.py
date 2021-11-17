#!/usr/bin/env python
import difflib
import os

import pandas as pd

df = pd.read_json(os.path.join(os.path.dirname(__file__), 'data/ordering-results.json'))

for index, row in df.iterrows():
    print(
        'User' + str(row['participantId']) +
        ': Task 2.1: ' + str(difflib.SequenceMatcher(None, row['task2_1_manual'], row['task2_1_proposal']).ratio()) +
        ", Task 2.2: " + str(difflib.SequenceMatcher(None, row['task2_2_manual'], row['task2_2_proposal']).ratio())
    )
