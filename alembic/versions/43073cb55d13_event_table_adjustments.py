"""event table adjustments

Revision ID: 43073cb55d13
Revises: 7b684192c510
Create Date: 2021-10-21 13:56:40.618105

"""
from alembic import op

from sqlalchemy import Column
from sqlalchemy.dialects.postgresql import (BIGINT, JSON, VARCHAR)

# revision identifiers, used by Alembic.
revision = '43073cb55d13'
down_revision = '7b684192c510'
branch_labels = None
depends_on = None


def upgrade():
    op.alter_column(table_name='event',
                    column_name='date',
                    new_column_name='created_at', schema='workflow')

    op.add_column(table_name='event',
                  column=Column('old_position', BIGINT, nullable=True),
                  schema='workflow')

    op.add_column(table_name='event',
                  column=Column('new_position', BIGINT, nullable=True),
                  schema='workflow')

    op.add_column(table_name='event',
                  column=Column('new_type', VARCHAR, nullable=True),
                  schema='workflow')

    op.add_column(table_name='event',
                  column=Column('resources_updated', BIGINT, nullable=True),
                  schema='workflow')

    op.add_column(table_name='event',
                  column=Column('temporal_query_result', JSON, nullable=True),
                  schema='workflow')


def downgrade():
    op.drop_column(column_name='old_position', table_name='event', schema='workflow')
    op.drop_column(column_name='new_position', table_name='event', schema='workflow')
    op.drop_column(column_name='new_type', table_name='event', schema='workflow')
    op.drop_column(column_name='resources_updated', table_name='event', schema='workflow')
    op.drop_column(column_name='temporal_query_result', table_name='event', schema='workflow')
    op.drop_table(table_name='temporal_query_result', schema='workflow')
    op.alter_column(table_name='event',
                    column_name='created_at',
                    new_column_name='date', schema='workflow')
