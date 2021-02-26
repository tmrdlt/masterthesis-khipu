"""add move action table


Revision ID: 0fcd20832c18
Revises: da5b7f91e49d
Create Date: 2021-02-26 11:09:50.440094

"""
from alembic import op

from sqlalchemy import Column
from sqlalchemy.dialects.postgresql import (BIGINT, VARCHAR)

# revision identifiers, used by Alembic.
revision = '0fcd20832c18'
down_revision = 'da5b7f91e49d'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('move_action',
                    Column('id', BIGINT, primary_key=True),
                    Column('action_id', BIGINT, nullable=False),
                    Column('old_list_api_id', VARCHAR, nullable=False),
                    Column('new_list_api_id', VARCHAR, nullable=False),
                    schema='workflow')

    op.create_foreign_key('action_fk', 'move_action', 'action', ['action_id'], ['id'],
                          None, 'CASCADE', None, None, None, 'workflow', 'workflow')


def downgrade():
    op.drop_table('move_action', schema='workflow')
