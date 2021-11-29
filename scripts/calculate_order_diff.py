#!/usr/bin/env python
import difflib
import os

import pandas as pd

df = pd.read_json(os.path.join(os.path.dirname(__file__), 'data/ordering-results.json'))

df_sols1 = pd.read_json(os.path.join(os.path.dirname(__file__), 'data/solutions_task_2_1.json'))
df_sols2 = pd.read_json(os.path.join(os.path.dirname(__file__), 'data/solutions_task_2_2.json'))

data = []

for index, row in df.iterrows():
    manual_1 = row['task2_1_manual']
    ratios_1 = []
    for sol in df_sols1['sols']:
        ratio = difflib.SequenceMatcher(None, manual_1, sol).ratio()
        ratios_1.append(ratio)
    row_1 = [
        row['participantId'],
        'Task 2.1',
        difflib.SequenceMatcher(None, manual_1, row['task2_1_proposal']).ratio(),
        max(ratios_1)
    ]

    manual_2 = row['task2_2_manual']
    ratios_2 = []
    for sol in df_sols2['sols']:
        ratio = difflib.SequenceMatcher(None, manual_2, sol).ratio()
        ratios_2.append(ratio)
    row_2 = [
        row['participantId'],
        'Task 2.2',
        difflib.SequenceMatcher(None, manual_2, row['task2_2_proposal']).ratio(),
        max(ratios_2)
    ]
    data.append(row_1)
    data.append(row_2)

data_df = pd.DataFrame(data, columns=("Participant ID", "Task", "to proposal", "to optimal"))
data_df.to_excel(os.path.join(os.path.dirname(__file__), 'data/cals.xlsx'), encoding='utf-8', index=False)
