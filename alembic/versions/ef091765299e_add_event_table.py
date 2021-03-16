"""add event table

Revision ID: ef091765299e
Revises: da5b7f91e49d
Create Date: 2021-02-22 15:02:52.541431

"""
from alembic import op

from sqlalchemy import Column, func, Enum
from sqlalchemy.dialects.postgresql import (BIGINT, TIMESTAMP, VARCHAR)

# revision identifiers, used by Alembic.
revision = 'ef091765299e'
down_revision = 'afb2d9be7aca'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('event',
                    Column('id', BIGINT, primary_key=True),
                    Column('api_id', VARCHAR, nullable=False),
                    Column('event_type', VARCHAR, nullable=False),
                    Column('workflow_list_api_id', VARCHAR, nullable=False),
                    Column('board_api_id', VARCHAR, nullable=True),
                    Column('parent_api_id', VARCHAR, nullable=True),
                    Column('old_parent_api_id', VARCHAR, nullable=True),
                    Column('new_parent_api_id', VARCHAR, nullable=True),
                    Column('user_api_id', VARCHAR, nullable=True),
                    Column('date', TIMESTAMP, nullable=False, server_default=func.now()),
                    Column('data_source', Enum('Khipu', 'GitHub', 'Trello', name='data_source', schema='workflow'),
                           nullable=False),
                    schema='workflow')


def downgrade():
    op.drop_table('event', schema='workflow')
