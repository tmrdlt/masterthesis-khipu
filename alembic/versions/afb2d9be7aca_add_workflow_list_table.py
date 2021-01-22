"""Add workflow list table


Revision ID: afb2d9be7aca
Revises: 
Create Date: 2020-12-04 16:21:56.293515

"""
from alembic import op

from sqlalchemy import Column, func
from sqlalchemy.dialects.postgresql import (BIGINT, TIMESTAMP, UUID, VARCHAR)



# revision identifiers, used by Alembic.
revision = 'afb2d9be7aca'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('workflow_list',
                    Column('id', BIGINT, primary_key=True),
                    Column('uuid', UUID, nullable=False),
                    Column('title', VARCHAR, nullable=False),
                    Column('description', VARCHAR, nullable=True),
                    Column('parent_id', BIGINT, nullable=True),
                    Column('created_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    Column('updated_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    schema='workflow')

    op.create_foreign_key('parent_fk', 'workflow_list', 'workflow_list', ['parent_id'], ['id'],
                          None, None, None, None, None, 'workflow', 'workflow')


def downgrade():
    op.drop_table('workflow_list', schema='workflow')
