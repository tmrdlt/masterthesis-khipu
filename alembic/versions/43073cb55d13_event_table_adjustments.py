"""event table adjustments

Revision ID: 43073cb55d13
Revises: 7b684192c510
Create Date: 2021-10-21 13:56:40.618105

"""
from alembic import op

from sqlalchemy import Column, func
from sqlalchemy.dialects.postgresql import (BIGINT, JSON, TIMESTAMP, VARCHAR)

# revision identifiers, used by Alembic.
revision = '43073cb55d13'
down_revision = '7b684192c510'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('temporal_query_result',
                    Column('id', BIGINT, primary_key=True),
                    Column('board_result', JSON, nullable=False),
                    Column('tasks_result', JSON, nullable=False),
                    Column('work_schedule', JSON, nullable=False),
                    Column('created_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    schema='workflow')

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
                  column=Column('temporal_query_result_id', BIGINT, nullable=True),
                  schema='workflow')

    op.create_foreign_key(constraint_name='temporal_query_result_fk',
                          source_table='event',
                          referent_table='temporal_query_result',
                          local_cols=['temporal_query_result_id'],
                          remote_cols=['id'],
                          ondelete='CASCADE',
                          source_schema='workflow',
                          referent_schema='workflow')


def downgrade():
    op.drop_column(column_name='old_position', table_name='event', schema='workflow')
    op.drop_column(column_name='new_position', table_name='event', schema='workflow')
    op.drop_column(column_name='new_type', table_name='event', schema='workflow')
    op.drop_column(column_name='resources_updated', table_name='event', schema='workflow')
    op.drop_column(column_name='temporal_query_result_id', table_name='event', schema='workflow')
    op.drop_table(table_name='temporal_query_result', schema='workflow')
    op.alter_column(table_name='event',
                    column_name='created_at',
                    new_column_name='date', schema='workflow')
