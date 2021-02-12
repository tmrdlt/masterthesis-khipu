"""add trello tables

Revision ID: da5b7f91e49d
Revises: afb2d9be7aca
Create Date: 2021-02-12 15:34:38.252253

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'da5b7f91e49d'
down_revision = 'afb2d9be7aca'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('trello_board',
                    Column('id', VARCHAR, primary_key=True),
                    Column('name', VARCHAR, nullable=False),
                    Column('desc', VARCHAR, nullable=False),
                    Column('closed', BOOLEAN, nullable=False),
                    Column('url', VARCHAR, nullable=False),
                    schema='workflow')
    op.create_table('trello_list',
                    Column('id', VARCHAR, primary_key=True),
                    Column('name', VARCHAR, nullable=False),
                    Column('closed', BOOLEAN, nullable=False),
                    Column('pos', BIGINT, nullable=False),
                    Column('id_board', VARCHAR, nullable=False),
                    schema='workflow')
    op.create_table('trello_card',
                    Column('id', VARCHAR, primary_key=True),
                    Column('name', VARCHAR, nullable=False),
                    Column('desc', VARCHAR, nullable=False),
                    Column('closed', BOOLEAN, nullable=False),
                    Column('date_last_activity', TIMESTAMP, nullable=False),
                    Column('id_board', VARCHAR, nullable=False),
                    Column('id_list', VARCHAR, nullable=False),
                    schema='workflow')


def downgrade():
    op.drop_table('trello_board', schema='workflow')
    op.drop_table('trello_list', schema='workflow')
    op.drop_table('trello_card', schema='workflow')


