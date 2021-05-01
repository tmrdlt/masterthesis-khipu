"""add temporal_constraint table

Revision ID: b4bd95ae3284
Revises: ef091765299e
Create Date: 2021-03-16 17:12:19.585616

"""
from alembic import op

from sqlalchemy import Column, func
from sqlalchemy.dialects.postgresql import (BIGINT, TIMESTAMP)

# revision identifiers, used by Alembic.
revision = 'b4bd95ae3284'
down_revision = 'ef091765299e'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('temporal_constraint',
                    Column('id', BIGINT, primary_key=True),
                    Column('workflow_list_id', BIGINT, nullable=False, unique=True),
                    Column('start_date', TIMESTAMP, nullable=True),
                    Column('end_date', TIMESTAMP, nullable=True),
                    Column('duration_in_minutes', BIGINT, nullable=True),
                    Column('connected_workflow_list_id', BIGINT, nullable=True),
                    Column('created_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    Column('updated_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    schema='workflow')

    op.create_unique_constraint(constraint_name='temporal_constraint_workflow_list_id_unique_constraint',
                                table_name='temporal_constraint',
                                columns=['workflow_list_id'],
                                schema='workflow')

    op.create_foreign_key(constraint_name='workflow_list_fk',
                          source_table='temporal_constraint',
                          referent_table='workflow_list',
                          local_cols=['workflow_list_id'],
                          remote_cols=['id'],
                          ondelete='CASCADE',
                          source_schema='workflow',
                          referent_schema='workflow')

    op.create_foreign_key(constraint_name='connected_workflow_list_fk',
                          source_table='temporal_constraint',
                          referent_table='workflow_list',
                          local_cols=['connected_workflow_list_id'],
                          remote_cols=['id'],
                          ondelete='CASCADE',
                          source_schema='workflow',
                          referent_schema='workflow')


def downgrade():
    op.drop_table(table_name='temporal_constraint', schema='workflow')
