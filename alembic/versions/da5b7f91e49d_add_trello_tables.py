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
down_revision = 'ef091765299e'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('trello_action',
                    Column('id', VARCHAR, primary_key=True),
                    Column('type', VARCHAR, nullable=False),
                    Column('id_board', VARCHAR, nullable=False),
                    Column('id_list', VARCHAR, nullable=True),
                    Column('id_card', VARCHAR, nullable=True),
                    Column('date', TIMESTAMP, nullable=False),
                    schema='workflow')
    op.create_table('github_event',
                    Column('id', BIGINT, primary_key=True),
                    Column('api_id', VARCHAR, nullable=False),
                    Column('type', VARCHAR, nullable=False),
                    Column('issue_id', VARCHAR, nullable=False),
                    Column('user_id', VARCHAR, nullable=False),
                    Column('date', TIMESTAMP, nullable=False),
                    schema='workflow')

def downgrade():
    op.drop_table('trello_action', schema='workflow')
    op.drop_table('github_event', schema='workflow')

