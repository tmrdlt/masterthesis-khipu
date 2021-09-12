"""add work_schedule table

Revision ID: 7b684192c510
Revises: b4bd95ae3284
Create Date: 2021-09-09 18:32:33.536414

"""
from alembic import op

from sqlalchemy import Column, VARCHAR
from sqlalchemy.dialects.postgresql import (INTEGER, BIGINT, ARRAY)

# revision identifiers, used by Alembic.
revision = '7b684192c510'
down_revision = 'b4bd95ae3284'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('work_schedule',
                    Column('id', BIGINT, primary_key=True),
                    Column('start_work_at_hour', INTEGER, nullable=False),
                    Column('stop_work_at_hour', INTEGER, nullable=False),
                    Column('working_days_of_week', ARRAY(VARCHAR), nullable=False),
                    schema='workflow')

    op.execute("""INSERT INTO workflow.work_schedule (start_work_at_hour, stop_work_at_hour, working_days_of_week) VALUES
                (10, 18, ARRAY['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'])
                """)


def downgrade():
    op.drop_table(table_name='work_schedule', schema='workflow')
