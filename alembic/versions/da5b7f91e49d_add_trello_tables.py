"""add trello tables

Revision ID: da5b7f91e49d
Revises: afb2d9be7aca
Create Date: 2021-02-12 15:34:38.252253

"""
from alembic import op

from sqlalchemy import Column
from sqlalchemy.dialects.postgresql import (BIGINT, TIMESTAMP, VARCHAR, BOOLEAN)

# revision identifiers, used by Alembic.
revision = 'da5b7f91e49d'
down_revision = 'afb2d9be7aca'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('trello_board',
                    Column('id', VARCHAR, primary_key=True),
                    Column('name', VARCHAR, nullable=False),
                    Column('desc', VARCHAR, nullable=False),
                    Column('closed', BOOLEAN, nullable=False),
                    schema='workflow')
    op.create_table('trello_list',
                    Column('id', VARCHAR, primary_key=True),
                    Column('name', VARCHAR, nullable=False),
                    Column('closed', BOOLEAN, nullable=False),
                    Column('pos', BIGINT, nullable=False),
                    Column('id_board', VARCHAR, nullable=False),
                    schema='workflow')
    op.create_table('trello_card',
                    Column('id', VARCHAR, primary_key=True),
                    Column('name', VARCHAR, nullable=False),
                    Column('desc', VARCHAR, nullable=False),
                    Column('pos', BIGINT, nullable=False),
                    Column('closed', BOOLEAN, nullable=False),
                    Column('id_board', VARCHAR, nullable=False),
                    Column('id_list', VARCHAR, nullable=False),
                    Column('date_last_activity', TIMESTAMP, nullable=False),
                    schema='workflow')
    op.create_table('trello_action',
                    Column('id', VARCHAR, primary_key=True),
                    Column('type', VARCHAR, nullable=False),
                    Column('id_board', VARCHAR, nullable=False),
                    Column('id_list', VARCHAR, nullable=True),
                    Column('id_card', VARCHAR, nullable=True),
                    Column('date', TIMESTAMP, nullable=False),
                    schema='workflow')

    op.create_foreign_key('board_fk', 'trello_list', 'trello_board', ['id_board'], ['id'],
                          None, 'CASCADE', None, None, None, 'workflow', 'workflow')

    op.create_foreign_key('board_fk', 'trello_card', 'trello_board', ['id_board'], ['id'],
                          None, 'CASCADE', None, None, None, 'workflow', 'workflow')
    op.create_foreign_key('list_fk', 'trello_card', 'trello_list', ['id_list'], ['id'],
                          None, 'CASCADE', None, None, None, 'workflow', 'workflow')

    op.create_foreign_key('board_fk', 'trello_action', 'trello_board', ['id_board'], ['id'],
                          None, 'CASCADE', None, None, None, 'workflow', 'workflow')


def downgrade():
    op.drop_table('trello_action', schema='workflow')
    op.drop_table('trello_card', schema='workflow')
    op.drop_table('trello_list', schema='workflow')
    op.drop_table('trello_board', schema='workflow')
