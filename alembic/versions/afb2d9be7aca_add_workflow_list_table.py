"""Add workflow list table


Revision ID: afb2d9be7aca
Revises: 
Create Date: 2020-12-04 16:21:56.293515

"""
from alembic import op

from sqlalchemy import Column, func, Enum
from sqlalchemy.dialects.postgresql import (BIGINT, TIMESTAMP, UUID, VARCHAR, ENUM)

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
                    Column('usage_type', Enum('BOARD', 'LIST', 'ITEM', name='usage_type', schema='workflow'),
                           nullable=False),
                    Column('parent_id', BIGINT, nullable=True),
                    Column('order', BIGINT, nullable=False),
                    Column('created_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    Column('updated_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    schema='workflow')

    op.create_foreign_key('parent_fk', 'workflow_list', 'workflow_list', ['parent_id'], ['id'],
                          None, 'CASCADE', None, None, None, 'workflow', 'workflow')


def downgrade():
    op.drop_table('workflow_list', schema='workflow')
    usage_type_enum = ENUM('BOARD', 'LIST', 'ITEM', name='usage_type', schema='workflow')
    usage_type_enum.drop(op.get_bind())
