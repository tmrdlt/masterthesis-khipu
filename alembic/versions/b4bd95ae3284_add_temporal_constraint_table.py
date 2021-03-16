"""add temporal_constraint table

Revision ID: b4bd95ae3284
Revises: ef091765299e
Create Date: 2021-03-16 17:12:19.585616

"""
from alembic import op

from sqlalchemy import Column, func, Enum
from sqlalchemy.dialects.postgresql import (BIGINT, TIMESTAMP, VARCHAR, ENUM)

# revision identifiers, used by Alembic.
revision = 'b4bd95ae3284'
down_revision = 'ef091765299e'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('temporal_constraint',
                    Column('id', BIGINT, primary_key=True),
                    Column('workflow_list_id', VARCHAR, nullable=False),
                    Column('temporal_constraint_type', Enum('projectDueDate', 'itemToBeInList', 'dependsOn',
                                                            name='temporal_constraint_type', schema='workflow'),
                           nullable=False),
                    Column('due_date', TIMESTAMP, nullable=True),
                    Column('connected_workflow_list_id', BIGINT, nullable=True),
                    Column('created_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    Column('updated_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    schema='workflow')


def downgrade():
    op.drop_table('temporal_constraint', schema='workflow')

    temporal_constraint_type_enum = ENUM('projectDueDate', 'itemToBeInList', 'dependsOn',
                                         name='temporal_constraint_type', schema='workflow')
    temporal_constraint_type_enum.drop(op.get_bind())