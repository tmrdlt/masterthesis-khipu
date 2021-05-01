"""add user table

Revision ID: 938102055d55
Revises: b4bd95ae3284
Create Date: 2021-05-01 12:27:06.313662

"""
from alembic import op

from sqlalchemy import Column, func, ForeignKey
from sqlalchemy.dialects.postgresql import (BIGINT, TIMESTAMP, VARCHAR)

# revision identifiers, used by Alembic.
revision = '938102055d55'
down_revision = 'b4bd95ae3284'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('user',
                    Column('id', BIGINT, primary_key=True),
                    Column('api_id', VARCHAR, nullable=False),
                    Column('username', VARCHAR, nullable=False),
                    Column('created_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    Column('updated_at', TIMESTAMP, nullable=False, server_default=func.now()),
                    schema='workflow')

    op.add_column(table_name='workflow_list',
                  column=Column('created_by_user_api_id', VARCHAR, nullable=True),
                  schema='workflow')

    # ToDo maybe add later
    # op.create_foreign_key(constraint_name='created_by_user_fk',
    #                       source_table='workflow_list',
    #                       referent_table='user',
    #                       local_cols=['created_by_user_id'],
    #                       remote_cols=['id'],
    #                       source_schema='workflow',
    #                       referent_schema='workflow')


def downgrade():
    op.drop_column(table_name='workflow_list', column_name='created_by_user_id', schema='workflow')
    op.drop_table(table_name='user', schema='workflow')
