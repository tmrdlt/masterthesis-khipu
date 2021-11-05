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
    op.execute("""insert into workflow.workflow_list (id, api_id, title, description, parent_id, position, list_type, state, data_source, use_case, is_temporal_constraint_board, created_at, updated_at, owner_api_id)
values  (1, 'b903d414-26fe-4944-92a6-2e0e19e78f56', 'Funding application restaurant', null, null, 2, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 14:48:09.205954', '2021-11-04 13:03:23.623142', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (3, 'bad9d9c4-71f4-4079-b5f1-4e4bdd042260', 'Doing', null, 1, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 14:48:09.236468', '2021-11-01 14:48:09.236468', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (4, 'ba4fde8f-6f68-4a48-8131-6c7860f633d0', 'Done', null, 1, 2, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 14:48:09.236468', '2021-11-01 14:48:09.236468', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (5, '0a6dc9c9-fe86-4575-abac-ea37537f983c', 'Gather details about the application', null, 4, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 14:48:29.236084', '2021-11-04 11:07:27.957626', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (6, 'e5d08d9e-38df-4958-ae26-38bf779fc229', 'Write down tasks', null, 4, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 14:48:37.455277', '2021-11-01 14:48:44.782625', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (8, '5e85ddee-40e4-4e28-92c5-1b0746ea3deb', 'Your task', 'You found out about an organisation that founds creative restaurant concepts. Because you were thinking about opening up a restaurant for a long time, you decided to give it a try and apply for this funding.
The application consists of a presentation and a submission that takes place beforehand.

Expected for the submission are:
• A business plan
• A concept including design

Expected to be presented are:
• The submitted concept
• A food example

You already wrote down tasks that came to you when you were thinking about the project. They are in the list "ToDo" and are currently without any order.', null, 1, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 14:49:03.476484', '2021-11-04 19:53:34.660206', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (9, '5cdcdbc2-5428-4e62-ba04-36ebf08ba169', 'Do', 'Reorganize the tasks in the "ToDo" list. Try to arrange them in a way that makes the most sense for you and helps you get an overview of the project.

If you want, you can
• reorder tasks
• convert tasks to other types
• move tasks around
• create new lists and boards and move tasks inside them

Time limit: 10 minutes', 8, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 14:49:29.987408', '2021-11-04 13:06:13.146183', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (10, 'f7df17aa-f798-46a2-8479-9f1119c6bb7e', 'Board A', null, null, 3, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:10.808910', '2021-11-04 11:09:08.468262', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (12, '4f3b9568-2714-4e50-8643-23d897a0641a', 'Doing', null, 10, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:10.825208', '2021-11-01 15:09:10.825208', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (13, '22dfc135-9f72-4cca-b53d-424f8eaf9654', 'Done', null, 10, 2, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:10.825208', '2021-11-01 15:09:10.825208', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (14, '0da185b7-f526-4449-931e-2e15b3d63bd0', 'Board B', null, null, 4, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:52.128407', '2021-11-04 11:09:13.871772', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (16, 'bfd12fe4-db4b-494f-9d5d-21eb29b46230', 'Doing', null, 14, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:52.151895', '2021-11-01 15:09:52.151895', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (17, 'b62fb487-e76f-4c15-82e3-4f79d7855226', 'Done', null, 14, 2, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:09:52.151895', '2021-11-01 15:09:52.151895', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (19, 'a45ac8fa-014c-4c2a-9e17-ac81bcccf12c', 'Board C', null, null, 5, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 15:10:12.423099', '2021-11-04 11:09:19.727770', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (21, '8810fc43-cfde-4807-a6e6-f0c9e396fe11', 'Doing', null, 19, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:10:12.451861', '2021-11-01 15:10:12.451861', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (22, '314c3711-d73a-4114-b221-c8e49b86822e', 'Done', null, 19, 2, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 15:10:12.451861', '2021-11-01 15:10:12.451861', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (23, 'ca02e171-dceb-4c09-8104-bc5a8dcc8906', 'Gather details about the application', null, 13, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:10:37.476842', '2021-11-04 11:07:51.784028', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (24, '0f533735-2c46-4750-9e8c-b090332ac7d9', 'Gather details about the application', null, 17, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:10:46.962915', '2021-11-04 11:07:59.187062', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (25, '0e88bb3c-f568-43ca-9232-f057259e677f', 'Gather details about the application', null, 22, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:11:00.635709', '2021-11-04 11:08:16.298740', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (26, '811595f8-8b4c-43f8-9e78-7b718b216f84', 'Write down tasks ', null, 13, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:11:10.201574', '2021-11-01 15:11:10.201574', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (27, 'd938cc11-21e1-4610-9e65-f163bbed3c42', 'Write down tasks', null, 17, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:11:13.522619', '2021-11-01 15:11:13.522619', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (28, 'fd63ad24-3c6b-4240-9d85-65e6a2be330c', 'Write down tasks', null, 22, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 15:11:17.425793', '2021-11-01 15:11:17.425793', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (89, 'bf865efa-7548-4a34-8eba-ec2d07c2f3a1', 'Do', '1. Sort all tasks in the "ToDo" list to what you think is the best order.
2. Write your estimate of the finish date into the "Estimation" item
3. Write down how many due dates you think you missed into the "Estimation" item

Time limit: 3 minutes', 90, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:15:21.073165', '2021-11-04 13:11:19.455571', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (90, 'de3a8251-1fdf-400a-875a-e5318daab7cf', 'Your task', 'It is Monday, 01.11., 10 am. 
You are working from home while also having to organize your houshold.
Below you see a board representing your open ToDos for today.

You try to keep as many due dates as possible while finishing the last task as early as possible.
In which order should you process the tasks? 

Assume the following: 
• You only work on tasks Monday to Friday between 10 am and 6 pm.
• You can process only ONE task at a time.
• Once you start a task you have to finish it first before you can start the next task.
• If tasks have a start date assigned, they can not be started before that time.', null, 6, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 16:26:33.499253', '2021-11-04 10:53:03.718905', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (91, 'a9d8b166-12b7-4b39-9bc0-2ae32b12de3d', 'Estimation', 'Estimated finish date:
Due dates missed:', 90, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:26:44.914026', '2021-11-04 13:11:09.085447', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (92, '6ff93009-2cc4-49de-a08b-9ebc85cfdd2a', 'Tasks for the week', null, 120, 0, 'BOARD', 'OPEN', 'Khipu', null, true, '2021-11-01 16:27:31.576032', '2021-11-04 09:42:01.705318', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (93, 'e1c87b75-1dce-4495-9fc4-a9fc1536bd80', 'ToDo', null, 92, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 16:27:31.607763', '2021-11-01 16:27:31.607763', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (94, 'af259435-c832-45bd-b7b6-26de2e3bbcd6', 'Done', null, 92, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 16:27:31.607763', '2021-11-01 16:27:31.607763', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (95, '05da36a7-a676-4fc3-9bf0-727194dc35ae', 'Clean the kitchen', null, 93, 3, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:28:07.715322', '2021-11-03 18:07:10.874921', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (97, '8c054cc3-8a77-41d7-89c9-fe92572393ee', 'Pick up son from the kindergarden', null, 93, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:30:43.673394', '2021-11-04 09:41:03.900918', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (100, '7922d5b4-72d6-4f40-adf0-69376f784ca1', 'Vacuum the living room', null, 93, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:32:38.128075', '2021-11-01 16:32:38.128075', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (101, '20c1038f-6c0f-4774-9d79-3db65c576948', 'Monday work', null, 93, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 16:40:19.524718', '2021-11-01 16:42:52.015551', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (102, 'f3f7a0f0-b47a-4440-95a7-0f3919c70b7a', 'Your task', 'It is Monday, 01.11., 10 am. 
You are working in a laboratory.
Below you see a board representing your open ToDos for this week.

You try to keep as many due dates as possible while finishing the last task as early as possible.
In which order should you process the tasks? 

Assume the following: 
• You only work on tasks Monday to Friday between 10 am and 6 pm.
• You can process only ONE task at a time.
• Once you start a task you have to finish it first before you can start the next task.
• If tasks have a start date assigned, they can not be started before that time.', null, 7, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-01 17:01:02.606189', '2021-11-04 10:52:49.005393', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (103, 'c86eb34b-6254-4030-9350-7acd5a99ae79', 'Do', '1. Sort all tasks in the "ToDo" list to what you think is the best order.
2. Write your estimate of the finish date into the "Estimation" item
3. Write down how many due dates you think you missed into the "Estimation" item

Time limit: 6 minutes', 102, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:01:03.411270', '2021-11-04 13:11:49.027020', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (104, 'c8239e20-20f2-4e50-849b-69b55b1d8175', 'Estimation', 'Estimated finish date:
Due dates missed:', 102, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:01:03.411270', '2021-11-04 13:11:54.152347', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (105, '73266b7f-8b59-4ac4-bd29-fb5d7ada51fb', 'Tasks for this week', null, 122, 0, 'BOARD', 'OPEN', 'Khipu', null, true, '2021-11-01 17:01:45.138201', '2021-11-04 10:41:58.313682', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (106, '8fcecdff-5ad4-490a-b332-bbb5139afe18', 'ToDo', null, 105, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 17:01:45.165649', '2021-11-01 17:01:45.165649', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (107, '5f5919c8-b807-4f97-b830-2cf1d34301d2', 'Done', null, 105, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-01 17:01:45.165649', '2021-11-01 17:01:45.165649', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (109, 'd484be85-2c6c-46d1-a77b-91be4270025b', 'Experiment 1', '• Perform tests
• Deliver results', 106, 4, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:05:01.829538', '2021-11-04 10:50:43.010674', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (111, 'e59d256c-e18b-4166-9f6f-23732e320ad7', 'Check results of colleagues ', 'Results will come in on friday', 106, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:05:17.398434', '2021-11-04 10:33:37.719765', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (112, 'e67eb392-b6d9-4652-81f3-87c2e0548a56', 'Order probes for next week', null, 106, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:05:25.220662', '2021-11-04 10:09:28.677668', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (113, '90f073d1-7bb2-425b-b15b-d8bba8e4ccaa', 'Read newest edition of Bio-Magazine', null, 106, 3, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:05:32.419987', '2021-11-04 10:15:54.885487', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (115, 'b5a28df1-1065-4468-9878-46cf21595a9a', 'Experiment 3', '• Perform tests
• Deliver results', 106, 6, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:05:46.318919', '2021-11-04 10:51:58.213629', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (118, 'ffe04b87-d464-4591-94b2-32dbb6b41fdf', 'Finish writing paper', null, 106, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-01 17:06:12.500171', '2021-11-04 10:16:33.966661', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (119, '25497d8a-5cfc-4f89-8599-d36097723f78', 'Bake birthday cake', 'Finish before partner gets home!', 93, 4, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-03 18:05:48.773093', '2021-11-03 18:12:25.004225', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (120, '4d6e4261-40bd-47fc-a714-9aa2280af105', 'Household organisation', null, null, 8, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-03 18:14:43.134123', '2021-11-03 18:15:17.231690', 'f9815865-6a34-4ec7-89ec-7fa2311cc00d'),
        (122, '84cf9df6-efc1-4a9a-a2ed-5aefc77964c7', 'Laboratory work', null, null, 9, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-04 10:04:52.168011', '2021-11-04 13:12:05.228683', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (124, '74fa7d38-765f-4374-8903-0f2c66b516f0', 'Experiment 2', null, 106, 5, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 10:05:33.820773', '2021-11-04 10:50:56.819730', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (126, '6e6c9738-32e1-404a-8805-e8b9efb36663', 'ToDo', null, 1, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 10:55:51.643519', '2021-11-04 11:33:06.847272', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (127, '10c111c0-a0ac-4b60-9eb4-4369135d590d', 'ToDo', null, 10, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 11:08:42.251288', '2021-11-04 11:25:11.909358', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (128, 'f59af0f7-7bb6-45ff-a3ab-07510247d0f7', 'ToDo', null, 14, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 11:09:51.570206', '2021-11-04 11:23:00.394015', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (129, '8c397ba0-04f2-4d5d-90ea-35b749e97e72', 'ToDo', null, 19, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 11:09:59.762880', '2021-11-04 11:25:19.163360', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (202, '26f8cbc7-371a-429c-a16c-b026dafcd9e1', 'Gather inspiration for concept', null, 289, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:23:00.399540', '2021-11-04 19:41:58.513440', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (203, '1f27d258-d6e8-490b-997f-3f0142693ed2', 'Decide which food the restaurant should offer', null, 289, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:23:00.399540', '2021-11-04 19:52:38.118764', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (204, '533760bd-483d-46fc-8566-cfcc91e19a2a', 'Create design concept', null, 128, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 11:23:00.399540', '2021-11-04 11:23:41.567717', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (206, 'a3351da9-5253-4f0c-8d8f-1aac7ff67b36', 'Create logo', null, 204, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:23:00.399540', '2021-11-04 11:23:48.237506', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (207, '7f3bf91a-3d9c-4790-97f6-e2b81afa2f9b', 'Read how to make a business plan', null, 209, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:23:00.399540', '2021-11-04 11:23:37.779568', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (208, 'cbc4e164-5bb1-4a75-a165-b08efd7befe3', 'Calculate estimated income and expanses', null, 209, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:23:00.399540', '2021-11-04 11:23:32.980546', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (209, 'd3620509-9a0b-4a6a-8e26-9fe2a171b6a5', 'Create business plan', null, 128, 2, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 11:23:00.399540', '2021-11-04 11:23:29.620101', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (210, 'fd15f097-726f-4797-ab8e-b8efcd101e59', 'Prepare food example', null, 128, 4, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 11:23:00.399540', '2021-11-04 11:24:21.136100', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (211, '14f2478d-73f3-4488-a61a-7455c9a0a220', 'Find recipe and buy ingredients for cake', null, 210, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:23:00.399540', '2021-11-04 19:55:25.754081', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (212, '421bd5f9-f743-449d-b615-e245d130e5b6', 'Bake cake', null, 210, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:23:00.399540', '2021-11-04 19:55:34.774511', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (213, '570c2339-0003-41ef-97f5-aaeb25d41f1d', 'Prepare presentation', null, 214, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:23:00.399540', '2021-11-04 19:37:30.493056', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (214, 'b6e2e0cb-3081-43e8-960f-b11bfe30a6a9', 'Hold presentation', null, 128, 5, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 11:23:00.399540', '2021-11-04 19:37:25.071360', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (215, '196893ac-b495-4895-a97b-c8cff83c6968', 'Gather inspiration for concept', null, 127, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:11.916595', '2021-11-04 19:40:48.941809', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (216, 'e52f45fe-6e5b-458e-9f08-831c88c4b971', 'Decide which food the restaurant should offer', null, 127, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:11.916595', '2021-11-04 19:52:46.322850', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (217, 'ff957d01-f0f4-4273-b6f3-d90c58b3753d', 'Create design concept', null, 127, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:11.916595', '2021-11-04 11:25:11.916595', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (219, '4d5f7384-cff1-4be9-be43-5410bd1a4ba0', 'Create logo', null, 127, 3, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:11.916595', '2021-11-04 11:25:11.916595', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (220, 'dd7cb864-eefe-433d-93d7-b81f8e5d3616', 'Read how to make a business plan', null, 127, 5, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:11.916595', '2021-11-04 11:25:11.916595', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (221, '24ed80aa-054e-409c-ae11-85aa42bc7d69', 'Calculate estimated income and expanses', null, 127, 6, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:11.916595', '2021-11-04 11:25:11.916595', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (222, 'a8a2d926-eb0a-46e7-8f4f-915cda941589', 'Create business plan', null, 127, 7, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:11.916595', '2021-11-04 11:25:11.916595', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (223, '2f3abfa1-834f-4b00-856f-4f24cd0185e8', 'Prepare food example', null, 127, 9, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:11.916595', '2021-11-04 11:25:11.916595', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (224, '9133159d-8aab-4129-89e2-bbd801966787', 'Find recipe and buy ingredients for cake', null, 127, 10, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:11.916595', '2021-11-04 19:54:47.054101', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (225, '259bbee0-382e-4c42-b27b-44ef45f41d82', 'Bake cake', null, 127, 11, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:11.916595', '2021-11-04 19:55:11.600023', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (226, '5bbce91f-c7d3-4d55-9a47-ccfd0a231eb7', 'Prepare presentation', null, 127, 12, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:11.916595', '2021-11-04 11:25:11.916595', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (227, '591d0da1-6f34-4f93-b475-7454816960c8', 'Hold presentation', null, 127, 13, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:11.916595', '2021-11-04 19:34:11.067368', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (228, '349ffcc6-f29d-4e32-82c9-18e86f95e1f5', 'Gather inspiration for general concept', null, 244, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:19.168077', '2021-11-04 11:30:08.096571', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (229, '3858e6ef-4160-4825-bdad-2a8c92acf2a3', 'Decide which food the restaurant should offer', null, 244, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:19.168077', '2021-11-04 19:53:12.474068', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (230, 'd780e46a-54dd-4a84-8cb8-2013fff2e936', 'Create design concept', null, 245, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:19.168077', '2021-11-04 11:30:25.868442', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (232, '62fc727b-7093-45a9-87ff-93c0c7a714e8', 'Create logo', null, 245, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:19.168077', '2021-11-04 11:30:15.585539', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (233, '187deacf-4dd9-4569-aabb-adae13ce7b98', 'Read how to make a business plan', null, 247, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:19.168077', '2021-11-04 11:31:39.963542', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (234, '701be219-3fb4-4be6-9f9c-6b7738ce0b63', 'Calculate estimated income and expanses', null, 247, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:19.168077', '2021-11-04 11:31:43.190662', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (235, '52c4d3a6-7f2d-467a-87c9-79645259f192', 'Create business plan', null, 243, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:19.168077', '2021-11-04 11:31:10.330958', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (236, '143a10c4-60f3-444f-affd-5ce99de2e75d', 'Prepare food example', null, 246, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:19.168077', '2021-11-04 11:29:31.387776', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (237, '474934ac-c859-4868-bc71-7c5676ec5f4d', 'Find recipe and buy ingredients for cake', null, 236, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:19.168077', '2021-11-04 19:56:10.752239', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (238, '5d0ae860-910d-4dd2-847d-fa45ce7fc80e', 'Bake cake', null, 236, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:19.168077', '2021-11-04 19:56:18.742332', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (239, 'c296eacf-04d3-45e7-869b-8f716acd7f09', 'Prepare presentation', null, 246, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:19.168077', '2021-11-04 11:29:52.841113', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (240, '05a5c92a-d91e-4f25-a1ac-b084c5206426', 'Hold presentation', null, 242, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:19.168077', '2021-11-04 19:36:11.288044', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (241, '43362086-ceff-4ceb-a14e-5982daaa02bb', 'Concept', '', 286, 0, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:36.884270', '2021-11-04 19:37:47.544564', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (242, '5ae8a1eb-17fc-4dbe-8524-9b33e22fc283', 'Presentation', null, 129, 1, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-04 11:25:50.833481', '2021-11-04 11:27:48.460508', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (243, '257781e0-69da-4b0c-a6cc-4747da75ff0e', 'Business plan', null, 286, 1, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-04 11:26:01.111100', '2021-11-04 19:37:55.911274', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (244, '492f84a3-4f03-476e-aa17-bd15ff4d68c6', 'Research and Ideation', '', 241, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 11:26:28.562604', '2021-11-04 11:26:28.562604', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (245, 'a3b4d9b1-e891-4d46-b505-2341b5573d3a', 'Design', null, 241, 2, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 11:26:36.223655', '2021-11-04 19:43:12.555296', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (246, '651be268-9a0d-48b4-838f-b62bd010dbb7', 'Preparation', null, 242, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 11:28:49.101375', '2021-11-04 11:29:43.728426', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (247, '93577383-4458-49f9-987f-211ddfae6692', 'Preperation', '', 243, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 11:31:35.720930', '2021-11-04 11:31:46.269682', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (248, 'db07eb48-684c-4cfa-a715-79722ecf2380', 'Gather inspiration for concept', null, 126, 6, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:33:06.854304', '2021-11-04 19:40:21.946836', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (249, 'ba034ac1-438a-41b6-892f-f557b43617cc', 'Decide which food the restaurant should offer', null, 126, 2, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:33:06.854304', '2021-11-04 19:53:24.061882', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (250, '37674450-769e-4678-b6c5-c69145cedaff', 'Create design concept', null, 126, 4, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:33:06.854304', '2021-11-04 11:33:30.852386', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (252, '303b8f59-ca68-4717-a3d9-09093ec63ed6', 'Create logo', null, 126, 10, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:33:06.854304', '2021-11-04 11:33:22.932967', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (253, 'c799098c-b56e-4ff3-abca-93f2b75401b6', 'Read how to make a business plan', null, 126, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:33:06.854304', '2021-11-04 11:33:13.638996', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (254, 'e39f2731-6412-48e2-ac61-b8582b355ca3', 'Calculate estimated income and expanses', null, 126, 11, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:33:06.854304', '2021-11-04 19:49:46.348534', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (255, '8bfbd525-f2a0-4e24-b6d4-bf57d418f5b3', 'Create business plan', null, 126, 8, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:33:06.854304', '2021-11-04 19:49:44.007532', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (256, '5a9f289d-09f4-43e5-a1db-cd6bb522f506', 'Prepare food example', null, 126, 12, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:33:06.854304', '2021-11-04 11:33:06.854304', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (257, '06c5270d-64a9-4dd7-8fba-dbbd61e8cc1d', 'Find recipe and buy ingredients for the cake', null, 126, 9, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:33:06.854304', '2021-11-04 19:30:03.984944', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (258, 'fd43455e-fbae-4fad-bb37-52de42f4f09c', 'Bake a cake', null, 126, 3, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:33:06.854304', '2021-11-04 11:33:24.730810', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (259, '6a937b68-df4e-4a90-b948-7a5147d785ba', 'Prepare presentation', null, 126, 13, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:33:06.854304', '2021-11-04 11:33:06.854304', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (260, 'a79a2cab-deb6-451b-ac84-2776cd343368', 'Hold presentation', null, 126, 1, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 11:33:06.854304', '2021-11-04 19:33:30.367413', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (261, 'bcfc9571-04f0-4973-8a6d-b10bc22213ec', 'Experiment 4', '', 106, 7, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 12:40:01.309505', '2021-11-04 12:40:01.309505', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (262, '18f03bf7-1ee5-4c04-bf08-93a4b5ce6496', 'Experiment 5', '', 106, 8, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 12:40:07.110720', '2021-11-04 12:40:07.110720', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (263, 'e106100e-7fd0-4ad0-8ea0-ba7d9c7f957b', 'Experiment 6', '', 106, 9, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 12:40:15.388257', '2021-11-04 12:40:15.388257', 'f81e8919-6a23-4813-810d-55b9fd6c7057'),
        (264, '7783ceb5-8658-48e2-9175-c434aecbab1a', 'Your task', 'Below you see three boards (A, B, C) that represent different possible arrangements of the tasks from the previous task.', null, 0, 'BOARD', 'OPEN', 'Khipu', null, false, '2021-11-04 13:06:44.313137', '2021-11-04 19:58:33.051253', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (265, 'c32c0a0d-32cf-4fae-8c87-0bc491127b75', 'Do', 'Look at the boards for a moment. Which representation of the project do you like the most?
 
Time limit: 5 minutes', 264, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 13:08:58.024274', '2021-11-04 13:13:35.210154', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (284, 'ddeb3fdc-a3fb-4bca-8048-1ef9b3172261', 'Submit things for submission', '', 126, 5, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 19:33:49.668457', '2021-11-04 19:33:54.642524', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (285, '6bfe5eef-dc1d-4125-97eb-f54adc11e575', 'Submit things for submission', '', 127, 8, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 19:34:29.715019', '2021-11-04 19:34:37.263323', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (286, '75fcf875-06cb-45bd-91b0-78d8c1c7e2a8', 'Submit things for submission', '', 129, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 19:36:40.949609', '2021-11-04 19:38:09.408591', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (287, '4609d127-dd08-4cb3-9329-461c09bd45bc', 'Submit things for submission', '', 128, 3, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 19:36:55.844933', '2021-11-04 19:37:08.070456', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (288, 'ab8206e2-c71c-4458-a12f-9ed56bbad3de', 'Create concept', '', 126, 7, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 19:41:31.917822', '2021-11-04 19:41:35.490109', '8594e3a1-06e6-453a-97a6-2d8d239faf58'),
        (289, 'f691fb43-dbaa-4839-8674-2c0d07ec5039', 'Create concept', '', 128, 0, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 19:41:44.044928', '2021-11-04 19:41:53.958927', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (290, '2804a2d6-f634-477b-969e-c9ba8bcea21a', 'Create concept', '', 291, 0, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 19:42:39.761231', '2021-11-04 19:43:26.580993', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (291, 'a4f64156-181e-40ec-b47f-81b5f4f29c28', 'General', '', 241, 1, 'LIST', 'OPEN', 'Khipu', null, false, '2021-11-04 19:43:18.472238', '2021-11-04 19:43:35.999964', '5b0d62ba-a40a-4ca7-975d-63197de32642'),
        (292, '7e8f8669-45e3-490b-8235-a3ec68bb982e', 'Create concept', '', 127, 4, 'ITEM', 'OPEN', 'Khipu', null, false, '2021-11-04 19:51:13.211573', '2021-11-04 19:51:23.678992', '5b0d62ba-a40a-4ca7-975d-63197de32642');
        """)

    # TEMPORAL CONSTRAINTS
    op.execute("""insert into workflow.temporal_resource (id, workflow_list_id, start_date, end_date, duration_in_minutes, created_at, updated_at) values
    (1, 95, null, null, 60, '2021-11-01 16:28:07.800669', '2021-11-03 18:07:06.601149'),
    (3, 97, '2021-11-01 16:00:00.000000', '2021-11-01 16:30:00.000000', 30, '2021-11-01 16:30:43.732831', '2021-11-03 18:11:40.900338'),
    (4, 100, null, null, 30, '2021-11-01 16:34:41.794015', '2021-11-01 16:34:45.406253'),
    (5, 101, null, '2021-11-03 16:00:00.000000', 300, '2021-11-01 16:40:19.725630', '2021-11-03 18:13:09.356523'),
    (6, 109, null, '2021-11-02 10:00:00.000000', 540, '2021-11-01 17:05:01.960260', '2021-11-04 10:50:01.510529'),
    (8, 111, '2021-11-03 12:00:00.000000', '2021-11-03 18:00:00.000000', 180, '2021-11-01 17:06:20.707901', '2021-11-04 10:39:28.541427'),
    (9, 112, null, null, 60, '2021-11-01 17:06:25.293111', '2021-11-04 10:35:35.126277'),
    (10, 113, '2021-11-04 10:00:00.000000', null, 120, '2021-11-01 17:06:30.296987', '2021-11-04 10:19:44.420022'),
    (12, 115, '2021-11-02 17:00:00.000000', '2021-11-04 10:30:00.000000', 540, '2021-11-01 17:06:41.344439', '2021-11-04 10:51:39.834396'),
    (15, 118, null, '2021-11-04 18:00:00.000000', 360, '2021-11-01 17:06:54.674182', '2021-11-04 10:37:08.223670'),
    (16, 92, null, null, null, '2021-11-03 17:43:48.964041', '2021-11-04 09:41:30.835989'),
    (17, 119, null, '2021-11-01 17:00:00.000000', 60, '2021-11-03 18:05:48.828449', '2021-11-03 18:12:24.993725'),
    (19, 105, null, null, null, '2021-11-04 10:17:09.797030', '2021-11-04 10:41:48.113863'),
    (20, 124, null, null, 420, '2021-11-04 10:51:02.572088', '2021-11-04 12:42:06.828627'),
    (21, 261, null, null, 60, '2021-11-04 12:40:27.822407', '2021-11-04 12:40:27.822407'),
    (22, 262, null, null, 60, '2021-11-04 12:40:31.696405', '2021-11-04 12:40:40.267081'),
    (23, 263, null, null, 60, '2021-11-04 12:40:36.678665', '2021-11-04 12:40:36.678665');
    """)

    op.execute("""
    SELECT pg_catalog.setval(pg_get_serial_sequence('workflow.temporal_resource', 'id'), MAX(id)) FROM workflow.temporal_resource;
    SELECT pg_catalog.setval(pg_get_serial_sequence('workflow.user', 'id'), MAX(id)) FROM workflow.user;
    SELECT pg_catalog.setval(pg_get_serial_sequence('workflow.workflow_list', 'id'), MAX(id)) FROM workflow.workflow_list;
    """)


def downgrade():
    pass
