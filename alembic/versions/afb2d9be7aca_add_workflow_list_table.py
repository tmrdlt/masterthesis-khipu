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
                    Column('api_id', VARCHAR, nullable=False),
                    Column('title', VARCHAR, nullable=False),
                    Column('description', VARCHAR, nullable=True),
                    Column('parent_id', BIGINT, nullable=True),
                    Column('position', BIGINT, nullable=False),
                    Column('list_type', Enum('BOARD', 'LIST', 'ITEM', name='list_type', schema='workflow'),
                           nullable=False),
                    Column('state', Enum('OPEN', 'CLOSED', name='state', schema='workflow'),
                           nullable=True),
                    Column('data_source', Enum('Khipu', 'GitHub', 'Trello', name='data_source', schema='workflow'),
                           nullable=False),
                    Column('use_case', Enum('softwareDevelopment', 'roadmap', 'personal', name='use_case', schema='workflow'),
                           nullable=True),
                    Column('created_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    Column('updated_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    schema='workflow')

    op.create_foreign_key('parent_fk', 'workflow_list', 'workflow_list', ['parent_id'], ['id'],
                          None, 'CASCADE', None, None, None, 'workflow', 'workflow')


def downgrade():
    op.drop_table('workflow_list', schema='workflow')

    list_type_enum = ENUM('BOARD', 'LIST', 'ITEM', name='list_type', schema='workflow')
    list_type_enum.drop(op.get_bind())

    state_enum = ENUM('OPEN', 'CLOSED', name='state', schema='workflow')
    state_enum.drop(op.get_bind())

    data_source_enum = ENUM('Khipu', 'GitHub', 'Trello', name='data_source', schema='workflow')
    data_source_enum.drop(op.get_bind())

    use_case_enum = ENUM('softwareDevelopment', 'roadmap', 'personal', name='use_case', schema='workflow')
    use_case_enum.drop(op.get_bind())


