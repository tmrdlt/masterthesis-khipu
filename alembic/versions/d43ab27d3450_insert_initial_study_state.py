"""insert initial study state

Revision ID: d43ab27d3450
Revises: 43073cb55d13
Create Date: 2021-11-01 14:43:20.156695

"""
from alembic import op

# revision identifiers, used by Alembic.
revision = 'd43ab27d3450'
down_revision = '43073cb55d13'
branch_labels = None
depends_on = None


def upgrade():
    # TASKS
    op.execute("""insert into workflow.user (id, api_id, username, is_active, created_at, updated_at) values  
    (1, '718aee81-8a65-4b1d-84f8-52e9bd75603c', 'Exploration', true, '2021-11-01 14:41:58.179600', '2021-11-01 14:41:58.179600'),
    (2, '8594e3a1-06e6-453a-97a6-2d8d239faf58', 'Task 1.1', true, '2021-11-01 14:42:03.311680', '2021-11-01 14:42:03.311680'),
    (3, '5b0d62ba-a40a-4ca7-975d-63197de32642', 'Task 1.2', true, '2021-11-01 14:42:08.794144', '2021-11-01 14:42:08.794144'),
    (4, 'f9815865-6a34-4ec7-89ec-7fa2311cc00d', 'Task 2.1', true, '2021-11-01 14:42:13.943388', '2021-11-01 14:42:13.943388'),
    (5, 'f81e8919-6a23-4813-810d-55b9fd6c7057', 'Task 2.2', true, '2021-11-01 14:42:17.320028', '2021-11-01 14:42:17.320028');""")

    # WORKFLOW_LISTS
    op.execute("""insert into workflow.workflow_list (id, api_id, title, description, parent_id, position, list_type, state, data_source, use_case, is_temporal_constraint_board, created_at, updated_at, owner_api_id) values
      (1, 'b903d414-26fe-4944-92a6-2e0e19e78f56', 'Seminar', '', null, 1, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 14:48:09.205954', '2021-11-01 14:48:09.205954', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (2, '85ebf625-4cea-4dac-859a-c49a61180d43', 'ToDo', null, 1, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 14:48:09.236468', '2021-11-01 14:48:09.236468', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (3, 'bad9d9c4-71f4-4079-b5f1-4e4bdd042260', 'Doing', null, 1, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 14:48:09.236468', '2021-11-01 14:48:09.236468', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (4, 'ba4fde8f-6f68-4a48-8131-6c7860f633d0', 'Done', null, 1, 2, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 14:48:09.236468', '2021-11-01 14:48:09.236468', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (5, '0a6dc9c9-fe86-4575-abac-ea37537f983c', 'Select topic for presentation', 'Select which topic I want to present in the seminar.', 4, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 14:48:29.236084', '2021-11-01 14:48:29.236084', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (6, 'e5d08d9e-38df-4958-ae26-38bf779fc229', 'Write down tasks', '', 4, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 14:48:37.455277', '2021-11-01 14:48:44.782625', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (7, '7ad1b95d-9a5a-42a8-b15f-16b3b6607510', 'Organize tasks in board', '', 3, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 14:48:56.169897', '2021-11-01 14:48:56.169897', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (8, '5e85ddee-40e4-4e28-92c5-1b0746ea3deb', 'Your task', null, null, 0, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 14:49:03.476484', '2021-11-01 16:25:20.361589', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (9, '5cdcdbc2-5428-4e62-ba04-36ebf08ba169', 'Your task', 'Imagine you have to hold a presentation in a seminar you are attending. The date you have to hold the presentation the 1st of December, 11am. Furthermore, you also have to hand in a report one week later.
You have already selected a topic. Your presentation will be about the history of Middle Eastern cuisine.

There are a lot of different things to think about and to keep an overview about the project you will use this tool. A board with three lists representing open todos, tasks you are currently working on and already done tasks has already been created. You already wrote down all the ideas, tasks and steps you have in mind in the form of a quick, unordered list. Your Goal is to now organize them in a way that makes most sense for you. You are free to create new elements such as boards, lists and items using the features of the tool.

List of tasks:
- Hold the presentation
- Hand in the report
- Prepare a typical cake for audience to try
- Create powerpoint slides for the presentation
- Look for a recipe for the cake
- Buy the ingredients for the cake
- Make research about the history of Middle Eastern cuisine
- Look for information online
- Gather information about the topic
- Write the report
- Create the structure of the presentation
- Go to the library to lend some books
- Think about a good, interesting introduction to the presentation', 8, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 14:49:29.987408', '2021-11-01 16:25:13.108967', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (10, 'f7df17aa-f798-46a2-8479-9f1119c6bb7e', 'Planning A', '', null, 2, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:10.808910', '2021-11-01 15:09:10.808910', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (11, '0bd5a149-9f85-454c-af11-38964d1f5de2', 'ToDo', null, 10, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:10.825208', '2021-11-01 15:14:42.459961', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (12, '4f3b9568-2714-4e50-8643-23d897a0641a', 'Doing', null, 10, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:10.825208', '2021-11-01 15:09:10.825208', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (13, '22dfc135-9f72-4cca-b53d-424f8eaf9654', 'Done', null, 10, 2, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:10.825208', '2021-11-01 15:09:10.825208', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (14, '0da185b7-f526-4449-931e-2e15b3d63bd0', 'Planning B', '', null, 3, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:52.128407', '2021-11-01 15:09:52.128407', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (15, 'ef6292f3-c72a-4d6b-ac13-c9682978597a', 'ToDo', '', 14, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:52.151895', '2021-11-01 15:15:33.352125', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (16, 'bfd12fe4-db4b-494f-9d5d-21eb29b46230', 'Doing', null, 14, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:52.151895', '2021-11-01 15:09:52.151895', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (17, 'b62fb487-e76f-4c15-82e3-4f79d7855226', 'Done', null, 14, 2, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:52.151895', '2021-11-01 15:09:52.151895', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (19, 'a45ac8fa-014c-4c2a-9e17-ac81bcccf12c', 'Planning C', '', null, 4, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 15:10:12.423099', '2021-11-01 15:10:12.423099', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (20, 'deccc759-e1fb-426a-ba0b-2265d7cc304b', 'ToDo', null, 19, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:10:12.451861', '2021-11-01 15:23:24.983541', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (21, '8810fc43-cfde-4807-a6e6-f0c9e396fe11', 'Doing', null, 19, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:10:12.451861', '2021-11-01 15:10:12.451861', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (22, '314c3711-d73a-4114-b221-c8e49b86822e', 'Done', null, 19, 2, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:10:12.451861', '2021-11-01 15:10:12.451861', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (23, 'ca02e171-dceb-4c09-8104-bc5a8dcc8906', 'Select topic for presentation', 'Select which topic I want to present in the seminar.', 13, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:10:37.476842', '2021-11-01 15:10:37.476842', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (24, '0f533735-2c46-4750-9e8c-b090332ac7d9', 'Select topic for presentation', 'Select which topic I want to present in the seminar.', 17, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:10:46.962915', '2021-11-01 15:10:46.962915', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (25, '0e88bb3c-f568-43ca-9232-f057259e677f', 'Select topic for presentation', 'Select which topic I want to present in the seminar.', 22, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:11:00.635709', '2021-11-01 15:11:00.635709', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (26, '811595f8-8b4c-43f8-9e78-7b718b216f84', 'Write down tasks ', '', 13, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:11:10.201574', '2021-11-01 15:11:10.201574', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (27, 'd938cc11-21e1-4610-9e65-f163bbed3c42', 'Write down tasks', '', 17, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:11:13.522619', '2021-11-01 15:11:13.522619', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (28, 'fd63ad24-3c6b-4240-9d85-65e6a2be330c', 'Write down tasks', '', 22, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:11:17.425793', '2021-11-01 15:11:17.425793', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (29, '4d323293-ef5d-4fb2-a249-2e54cb2ffe3a', 'Organize tasks in board', '', 13, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:11:26.716780', '2021-11-01 15:11:26.716780', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (30, '1517fb3c-4a1f-451f-a007-cababd510fa4', 'Organize tasks in board', '', 17, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:11:30.405098', '2021-11-01 15:11:30.405098', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (31, 'aae4519d-300f-4a23-9fb9-cb95356ec438', 'Organize tasks in board', '', 22, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:11:34.545045', '2021-11-01 15:11:34.545045', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (32, 'a023807b-a32b-4c35-ae79-d6689c60d256', 'Hold the presentation', null, 11, 6, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:21:39.740484', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (33, 'ae6336b4-aaa0-467b-9f7d-9a157e3eaf3f', 'Hand in the report', null, 11, 5, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:12:43.734171', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (34, '9208b4be-8875-4a93-b54d-13242114edc9', 'Prepare a typical cake for audience to try', null, 11, 4, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:21:42.321231', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (35, 'af394f28-5c62-4786-9cc9-e731c698f11d', 'Create powerpoint slides for the presentation', null, 11, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:21:44.814393', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (36, '68c59c4d-a61c-49e1-9800-c365bf639d35', 'Look for a recipe for the cake', null, 11, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:21:52.122916', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (37, 'bb25a90b-f33c-4ff1-9792-9aaae98735c8', 'Buy the ingredients for the cake', null, 11, 3, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:21:48.129987', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (38, '3a8c1b31-eb2f-41e9-989b-b3399e86d964', 'Make research about the history of Middle Eastern cuisine', null, 11, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:21:59.125510', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (41, '12114590-b71e-43ae-afc4-1c44ba8a7e34', 'Look for information online', null, 11, 7, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:12:08.845530', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (43, '6756761d-6152-4da2-bf9e-aea12c346f32', 'Gather information about the topic', null, 11, 8, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:12:08.845530', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (44, '40097179-b943-4e23-ab86-5da80fe9ed18', 'Write the report', null, 11, 9, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:12:08.845530', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (45, '646d4748-73ae-4f31-9024-68e5e2267723', 'Create the structure of the presentation', null, 11, 10, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:12:08.845530', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (46, '753587e3-38a3-4977-90cc-a23ded7f6b63', 'Go to the library to lend some books', null, 11, 11, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:12:08.845530', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (47, '645c20f8-ca51-4433-9e06-6aa59fccf83e', 'Think about a good, interesting introduction to the presentation', null, 11, 12, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:12:08.845530', '2021-11-01 15:12:08.845530', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (48, '3a7f26db-d1d5-4635-bb1a-0ca5201499fb', 'Hold the presentation', null, 64, 3, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:15:33.359339', '2021-11-01 15:17:23.722090', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (49, 'f70f7adf-835f-473e-801a-d6c4d0202ea6', 'Hand in the report', null, 67, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:15:33.359339', '2021-11-01 15:20:48.946459', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (51, '6e2570f2-070d-4d31-91ed-1de14b72b59a', 'Create powerpoint slides for the presentation', null, 64, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:15:33.359339', '2021-11-01 15:18:47.838241', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (52, '9965024a-11a2-496d-a88b-0d2697617043', 'Look for a recipe for the cake', null, 66, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:15:33.359339', '2021-11-01 15:22:10.417197', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (53, '6bb4ffef-ea1b-46e6-b67e-085daaa564a4', 'Buy the ingredients for the cake', null, 66, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:15:33.359339', '2021-11-01 15:21:19.249800', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (54, 'c5a2e612-296e-4df9-84ab-ea4cb746d3ba', 'Make research about the history of Middle Eastern cuisine', null, 65, 3, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:15:33.359339', '2021-11-01 15:22:05.670981', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (57, '28aaef30-f830-45f3-b17f-96b2623517d1', 'Look for information online', null, 65, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:15:33.359339', '2021-11-01 15:20:36.769369', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (59, '19873f74-ecfa-4ccd-8bec-c598a0fe25d2', 'Gather information about the topic', null, 65, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:15:33.359339', '2021-11-01 15:19:31.704006', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (60, 'f4564ebb-c21b-442e-98ea-4e7c6ee8b29c', 'Write the report', null, 67, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:15:33.359339', '2021-11-01 15:20:43.721243', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (61, 'c128a84d-f3db-47df-8976-e183aaf39f6b', 'Create the structure of the presentation', null, 64, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:15:33.359339', '2021-11-01 15:18:58.790837', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (62, 'a0c1637a-39d3-45a0-8d1d-80b1d02ef7e5', 'Go to the library to lend some books', null, 65, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:15:33.359339', '2021-11-01 15:19:38.042916', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (63, 'f8275fd2-b5fa-4309-a4ea-75598f09a535', 'Think about a good, interesting introduction to the presentation', null, 64, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:15:33.359339', '2021-11-01 15:18:50.620856', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (64, '86860169-37fa-4afa-981e-ef713027a7ac', 'Presentation', '', 15, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:17:11.814251', '2021-11-01 15:17:11.814251', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (65, 'b8e4325d-f87f-4825-8e73-e7d6efb3be19', 'Research', '', 15, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:19:11.136309', '2021-11-01 15:19:11.136309', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (66, 'd857c9d4-5f23-4118-88a5-45e738fac182', 'Prepare a typical cake for audience to try', null, 15, 2, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:19:17.683263', '2021-11-01 15:21:09.726581', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (67, '093d1d30-a2ee-4e5a-beb5-2825dcfb405f', 'Report', '', 15, 3, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:19:25.493310', '2021-11-01 15:19:25.493310', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (68, '2cca89dc-1735-470b-82c3-3cc2ea11d364', 'Hold the presentation', null, 81, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:23:17.930136', '2021-11-01 15:24:17.880073', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (69, '7b6c98ba-04be-4c00-8fd7-6ac4f1930f58', 'Hand in the report', null, 84, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:23:17.930136', '2021-11-01 15:28:40.004796', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (71, '3bb25dc4-222c-484f-b18d-ea3d9298ecf1', 'Create powerpoint slides for the presentation', null, 81, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:23:17.930136', '2021-11-01 15:30:46.416177', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (72, '6be11713-fda5-4a67-8c08-23e7c903deee', 'Look for a recipe for the cake', null, 83, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:23:17.930136', '2021-11-01 15:31:45.096388', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (73, '5069c32f-ebd0-4e35-bb33-e02f65d830c0', 'Buy the ingredients for the cake', null, 83, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:23:17.930136', '2021-11-01 15:28:26.386067', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (75, '6f739b87-2d3a-4a1c-8f70-8417ed304169', 'Look for information online', null, 76, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:23:17.930136', '2021-11-01 15:28:33.382654', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (76, '425b8bb8-586e-47b5-9663-8fee5a7b2605', 'Gather information about the topic', null, 85, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:23:17.930136', '2021-11-01 15:28:16.343390', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (77, '623a76d2-936b-4ea4-a496-0954dbd93353', 'Write the report', null, 84, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:23:17.930136', '2021-11-01 15:27:24.875508', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (78, 'c4160b2a-b317-44d8-9470-0d3f0d800de1', 'Create the structure', null, 82, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:23:17.930136', '2021-11-01 15:32:37.804539', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (79, 'cabd47fb-7655-43a6-9538-1ed18963cddc', 'Go to the library to lend some books', null, 76, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:23:17.930136', '2021-11-01 15:27:55.930859', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (80, '8fe3cac5-73f3-48f6-b6d0-14f5912aded3', 'Think about a good, interesting introduction to the presentation', null, 81, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:23:17.930136', '2021-11-01 15:30:39.655262', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (81, '1f86e4fe-0c06-4091-bacc-2a3bb5f0ea56', 'Presentation', '', 86, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:24:06.183224', '2021-11-01 15:29:54.538496', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (82, '1d74504e-6b05-4338-b3b3-8606e5241627', 'Preperation', '', 86, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:24:26.668974', '2021-11-01 15:30:17.301891', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (83, '1597f1f6-76d3-4918-ab85-9e4d956990d5', 'Prepare a typical cake for audience to try', null, 20, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:26:47.856299', '2021-11-01 15:32:22.220134', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (84, '1f69ed71-46ec-448c-acb1-c863be4f1bd0', 'Report', '', 86, 2, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:26:55.687006', '2021-11-01 15:29:44.791302', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (85, '376c3bf5-7214-4c06-9d7a-e1ccf70f3d35', 'Research about Middle Eastern cuisine', null, 82, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:27:02.180567', '2021-11-01 15:31:26.801974', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (86, '6e1e5f2a-7cfe-4545-a91f-b6ee79e4edb9', 'Report & presentation', '', 20, 1, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 15:29:00.624911', '2021-11-01 15:29:51.048209', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (87, '8f010185-b6da-418c-a2f8-f7290270c87a', 'Bake the cake', '', 66, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:33:05.962329', '2021-11-01 15:33:05.962329', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (88, 'ad425dfb-8fd4-4b54-a0e6-739cc219da4b', 'Bake the cake', '', 11, 13, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:33:15.888272', '2021-11-01 15:33:15.888272', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (89, 'bf865efa-7548-4a34-8eba-ec2d07c2f3a1', 'Your task', 'Imagine it is Monday morning the 1st of November, 10am, the working week has just started.
You see a board that represents a project with some temporal constraints configured. You goal is to think about a optimal processing order for the tasks so that

1. The project will finish as early as possible
2. As many of the due dates are kept

Furthermore assume the following:
- Tasks can only be processed Monday-Friday between 10am and 6pm 
- Only one task can be processed at a time

Please write down into the solution field:
- Your proposal for the optimal order of the tasks
- Your estimation of the final due date
- How many due dates you think you missed with that order', 90, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:15:21.073165', '2021-11-01 17:19:42.193755', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (90, 'de3a8251-1fdf-400a-875a-e5318daab7cf', 'Your task', '', null, 5, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 16:26:33.499253', '2021-11-01 16:26:33.499253', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (91, 'a9d8b166-12b7-4b39-9bc0-2ae32b12de3d', 'Your solution', '', 90, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:26:44.914026', '2021-11-01 16:26:44.914026', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (92, '6ff93009-2cc4-49de-a08b-9ebc85cfdd2a', 'Home office and household', null, null, 6, 'BOARD', 'OPEN', 'Khipu', null, true, '2021-11-01 16:27:31.576032', '2021-11-01 16:43:15.446558', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (93, 'e1c87b75-1dce-4495-9fc4-a9fc1536bd80', 'ToDo', null, 92, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 16:27:31.607763', '2021-11-01 16:27:31.607763', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (94, 'af259435-c832-45bd-b7b6-26de2e3bbcd6', 'Done', null, 92, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 16:27:31.607763', '2021-11-01 16:27:31.607763', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (95, '05da36a7-a676-4fc3-9bf0-727194dc35ae', 'Clean the kitchen', 'Finish before roommate comes home!', 93, 4, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:28:07.715322', '2021-11-01 16:38:49.594106', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (96, '92e3f429-4206-4c69-91a5-ee9c647e6b7c', 'Put the clothes into the washing machine', null, 93, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:29:47.480313', '2021-11-01 16:39:14.012785', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (97, '8c054cc3-8a77-41d7-89c9-fe92572393ee', 'Hang up the washing', '', 93, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:30:43.673394', '2021-11-01 16:30:43.673394', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (100, '7922d5b4-72d6-4f40-adf0-69376f784ca1', 'Vacuum the living room', '', 93, 3, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:32:38.128075', '2021-11-01 16:32:38.128075', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (101, '20c1038f-6c0f-4774-9d79-3db65c576948', 'Monday work', null, 93, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:40:19.524718', '2021-11-01 16:42:52.015551', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (102, 'f3f7a0f0-b47a-4440-95a7-0f3919c70b7a', 'Your task', '', null, 7, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 17:01:02.606189', '2021-11-01 17:01:02.606189', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (103, 'c86eb34b-6254-4030-9350-7acd5a99ae79', 'Your task', 'Imagine it is Monday morning the 1st of November, 10am, the working week has just started.
You see a board that represents a project with some temporal constraints configured. You goal is to think about a optimal processing order for the tasks so that

1. The project will finish as early as possible
2. As many of the due dates are kept

Furthermore assume the following:
- Tasks can only be processed Monday-Friday between 10am and 6pm 
- Only one task can be processed at a time

Please write down into the solution field:
- Your proposal for the optimal order of the tasks
- Your estimation of the final due date
- How many due dates you think you missed with that order', 102, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:01:03.411270', '2021-11-01 17:19:33.348980', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (104, 'c8239e20-20f2-4e50-849b-69b55b1d8175', 'Your solution', null, 102, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:01:03.411270', '2021-11-01 17:01:03.411270', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (105, '73266b7f-8b59-4ac4-bd29-fb5d7ada51fb', 'Laboratory work', null, null, 8, 'BOARD', 'OPEN', 'Khipu', null, true, '2021-11-01 17:01:45.138201', '2021-11-01 17:07:58.516185', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (106, '8fcecdff-5ad4-490a-b332-bbb5139afe18', 'ToDo', null, 105, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 17:01:45.165649', '2021-11-01 17:01:45.165649', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (107, '5f5919c8-b807-4f97-b830-2cf1d34301d2', 'Done', null, 105, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 17:01:45.165649', '2021-11-01 17:01:45.165649', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (109, 'd484be85-2c6c-46d1-a77b-91be4270025b', 'Experiment 1', '', 106, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:05:01.829538', '2021-11-01 17:05:01.829538', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (111, 'e59d256c-e18b-4166-9f6f-23732e320ad7', 'Experiment 3', '', 106, 3, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:05:17.398434', '2021-11-01 17:12:06.485448', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (112, 'e67eb392-b6d9-4652-81f3-87c2e0548a56', 'Experiment 4', '', 106, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:05:25.220662', '2021-11-01 17:05:25.220662', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (113, '90f073d1-7bb2-425b-b15b-d8bba8e4ccaa', 'Experiment 5', '', 106, 4, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:05:32.419987', '2021-11-01 17:05:32.419987', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (114, 'ed24d060-0376-40b8-9d6a-f39a0ed6fc25', 'Experiment 6', '', 106, 5, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:05:39.520062', '2021-11-01 17:05:39.520062', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (115, 'b5a28df1-1065-4468-9878-46cf21595a9a', 'Experiment 7', '', 106, 6, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:05:46.318919', '2021-11-01 17:05:46.318919', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (116, '5a11b3e4-b7a1-4651-9a1a-6781984eefbd', 'Experiment 8', '', 106, 7, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:05:53.732614', '2021-11-01 17:05:53.732614', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (117, 'a14780ea-ff51-4fa8-9fab-cb41edac9618', 'Experiment 9', '', 106, 8, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:06:03.113613', '2021-11-01 17:06:03.113613', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (118, 'ffe04b87-d464-4591-94b2-32dbb6b41fdf', 'Experiment 2', null, 106, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:06:12.500171', '2021-11-01 17:11:08.447781', 'f81e8919-6a23-4813-810d-55b9fd6c7057');
    """)

    # TEMPORAL CONSTRAINTS
    op.execute("""
    insert into workflow.temporal_resource (id, workflow_list_id, start_date, end_date, duration_in_minutes, created_at, updated_at) values
    (1, 95, null, '2021-11-01 17:00:00.000000', 60, '2021-11-01 16:28:07.800669', '2021-11-01 16:28:07.800669'),
    (2, 96, '2021-11-01 16:00:00.000000', null, 15, '2021-11-01 16:29:47.532619', '2021-11-01 16:29:47.532619'),
    (3, 97, '2021-11-01 17:30:00.000000', null, 30, '2021-11-01 16:30:43.732831', '2021-11-01 16:30:43.732831'),
    (4, 100, null, null, 30, '2021-11-01 16:34:41.794015', '2021-11-01 16:34:45.406253'),
    (5, 101, '2021-11-01 12:00:00.000000', '2021-11-01 16:00:00.000000', 240, '2021-11-01 16:40:19.725630', '2021-11-01 16:40:19.725630'),
    (6, 109, null, '2021-11-01 12:00:00.000000', 240, '2021-11-01 17:05:01.960260', '2021-11-01 17:10:09.231395'),
    (8, 111, null, '2021-11-02 10:00:00.000000', 240, '2021-11-01 17:06:20.707901', '2021-11-01 17:11:58.147903'),
    (9, 112, null, '2021-11-01 18:00:00.000000', 60, '2021-11-01 17:06:25.293111', '2021-11-01 17:09:02.109163'),
    (10, 113, null, '2021-11-02 12:00:00.000000', 360, '2021-11-01 17:06:30.296987', '2021-11-01 17:12:28.131393'),
    (11, 114, '2021-11-04 16:30:00.000000', '2021-11-05 16:30:00.000000', 60, '2021-11-01 17:06:36.143644', '2021-11-01 17:09:15.515819'),
    (12, 115, '2021-11-03 10:00:00.000000', null, 360, '2021-11-01 17:06:41.344439', '2021-11-01 17:12:55.577201'),
    (13, 116, null, null, 60, '2021-11-01 17:06:45.739720', '2021-11-01 17:06:45.739720'),
    (14, 117, '2021-11-03 10:00:00.000000', '2021-11-03 18:00:00.000000', 480, '2021-11-01 17:06:50.276532', '2021-11-01 17:07:35.068432'),
    (15, 118, null, '2021-11-05 18:00:00.000000', 480, '2021-11-01 17:06:54.674182', '2021-11-01 17:07:46.523141');
    """)

    op.execute("""
    SELECT pg_catalog.setval(pg_get_serial_sequence('workflow.temporal_resource', 'id'), MAX(id)) FROM workflow.temporal_resource;
    SELECT pg_catalog.setval(pg_get_serial_sequence('workflow.user', 'id'), MAX(id)) FROM workflow.user;
    SELECT pg_catalog.setval(pg_get_serial_sequence('workflow.workflow_list', 'id'), MAX(id)) FROM workflow.workflow_list;
    """)


def downgrade():
    pass
