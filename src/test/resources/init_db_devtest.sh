#!/bin/bash

APP_DB_USER=devtest
APP_DB_PASS=OurLocalPassword
APP_DB_NAME=workflowdbtest

psql --command "CREATE USER ${APP_DB_USER} WITH PASSWORD '${APP_DB_PASS}';"
psql --command "CREATE DATABASE ${APP_DB_NAME} WITH OWNER=${APP_DB_USER} LC_COLLATE='en_US.utf8' LC_CTYPE='en_US.utf8' ENCODING='UTF8' TEMPLATE=template0;"
psql -U postgres -d $APP_DB_NAME -c "CREATE SCHEMA workflow AUTHORIZATION ${APP_DB_USER};"
