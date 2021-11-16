"""insert initial study state

Revision ID: d43ab27d3450
Revises: 43073cb55d13
Create Date: 2021-11-01 14:43:20.156695

"""
from alembic import op

# revision identifiers, used by Alembic.
revision = 'd43ab27d3450'
down_revision = '43073cb55d13'
branch_labels = None
depends_on = None


def upgrade():
    import os
    script_dir = os.path.dirname(__file__)

    # Read user.sql
    user_path = os.path.join(script_dir, "sql/user.sql")
    fd = open(user_path, 'r')
    user = fd.read()
    fd.close()
    op.execute(user)
    op.execute("SELECT pg_catalog.setval(pg_get_serial_sequence('workflow.user', 'id'), MAX(id)) FROM workflow.user;")

    # Read workflow_list.sql
    workflow_list_path = os.path.join(script_dir, "sql/workflow_list.sql")
    fd = open(workflow_list_path, 'r')
    workflow_list = fd.read()
    fd.close()
    op.execute(workflow_list)
    op.execute(
        "SELECT pg_catalog.setval(pg_get_serial_sequence('workflow.workflow_list', 'id'), MAX(id)) FROM workflow.workflow_list;")

    # Read temporal_resource.sql
    temporal_resource_path = os.path.join(script_dir, "sql/temporal_resource.sql")
    fd = open(temporal_resource_path, 'r')
    temporal_resource = fd.read()
    fd.close()
    op.execute(temporal_resource)
    op.execute(
        "SELECT pg_catalog.setval(pg_get_serial_sequence('workflow.temporal_resource', 'id'), MAX(id)) FROM workflow.temporal_resource;")

    # '# Read event.sql
    # event_path = os.path.join(script_dir, "sql/event.sql")
    # fd = open(event_path, 'r')
    # event = fd.read()
    # fd.close()
    # op.execute(event)
    # op.execute("SELECT pg_catalog.setval(pg_get_serial_sequence('workflow.event', 'id'), MAX(id)) FROM workflow.event;")


def downgrade():
    pass
