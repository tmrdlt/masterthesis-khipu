"""add resource tables

Revision ID: b4bd95ae3284
Revises: ef091765299e
Create Date: 2021-03-16 17:12:19.585616

"""
from alembic import op

from sqlalchemy import Column, func
from sqlalchemy.dialects.postgresql import (BIGINT, FLOAT, TIMESTAMP, VARCHAR)

# revision identifiers, used by Alembic.
revision = 'b4bd95ae3284'
down_revision = '938102055d55'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('temporal_resource',
                    Column('id', BIGINT, primary_key=True),
                    Column('workflow_list_id', BIGINT, nullable=False, unique=True),
                    Column('start_date', TIMESTAMP, nullable=True),
                    Column('due_date', TIMESTAMP, nullable=True),
                    Column('duration_in_minutes', BIGINT, nullable=True),
                    Column('created_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    Column('updated_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    schema='workflow')

    op.create_foreign_key(constraint_name='workflow_list_fk',
                          source_table='temporal_resource',
                          referent_table='workflow_list',
                          local_cols=['workflow_list_id'],
                          remote_cols=['id'],
                          ondelete='CASCADE',
                          source_schema='workflow',
                          referent_schema='workflow')

    op.create_unique_constraint(constraint_name='temporal_resource_workflow_list_id_unique_constraint',
                                table_name='temporal_resource',
                                columns=['workflow_list_id'],
                                schema='workflow')

    op.create_table('numeric_resource',
                    Column('id', BIGINT, primary_key=True),
                    Column('workflow_list_id', BIGINT, nullable=False),
                    Column('label', VARCHAR, nullable=False),
                    Column('value', FLOAT, nullable=False),
                    Column('created_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    Column('updated_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    schema='workflow')

    op.create_foreign_key(constraint_name='workflow_list_fk',
                          source_table='numeric_resource',
                          referent_table='workflow_list',
                          local_cols=['workflow_list_id'],
                          remote_cols=['id'],
                          ondelete='CASCADE',
                          source_schema='workflow',
                          referent_schema='workflow')

    op.create_unique_constraint(constraint_name='numeric_resource_workflow_list_id_label_unique_constraint',
                                table_name='numeric_resource',
                                columns=['workflow_list_id', 'label'],
                                schema='workflow')

    op.create_table('textual_resource',
                    Column('id', BIGINT, primary_key=True),
                    Column('workflow_list_id', BIGINT, nullable=False),
                    Column('label', VARCHAR, nullable=False),
                    Column('value', VARCHAR, nullable=True),
                    Column('created_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    Column('updated_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    schema='workflow')

    op.create_foreign_key(constraint_name='workflow_list_fk',
                          source_table='textual_resource',
                          referent_table='workflow_list',
                          local_cols=['workflow_list_id'],
                          remote_cols=['id'],
                          ondelete='CASCADE',
                          source_schema='workflow',
                          referent_schema='workflow')

    op.create_unique_constraint(constraint_name='textual_resource_workflow_list_id_label_unique_constraint',
                                table_name='textual_resource',
                                columns=['workflow_list_id', 'label'],
                                schema='workflow')

    op.create_table('user_resource',
                    Column('id', BIGINT, primary_key=True),
                    Column('workflow_list_id', BIGINT, nullable=False),
                    Column('user_id', BIGINT, nullable=True),
                    Column('created_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    Column('updated_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    schema='workflow')

    op.create_foreign_key(constraint_name='workflow_list_fk',
                          source_table='user_resource',
                          referent_table='workflow_list',
                          local_cols=['workflow_list_id'],
                          remote_cols=['id'],
                          ondelete='CASCADE',
                          source_schema='workflow',
                          referent_schema='workflow')

    op.create_foreign_key(constraint_name='user_fk',
                          source_table='user_resource',
                          referent_table='user',
                          local_cols=['user_id'],
                          remote_cols=['id'],
                          ondelete='CASCADE',
                          source_schema='workflow',
                          referent_schema='workflow')

    op.create_unique_constraint(constraint_name='user_resource_workflow_list_id_unique_constraint',
                                table_name='user_resource',
                                columns=['workflow_list_id'],
                                schema='workflow')


def downgrade():
    op.drop_table(table_name='temporal_resource', schema='workflow')
    op.drop_table(table_name='numeric_resource', schema='workflow')
    op.drop_table(table_name='textual_resource', schema='workflow')
    op.drop_table(table_name='user_resource', schema='workflow')
