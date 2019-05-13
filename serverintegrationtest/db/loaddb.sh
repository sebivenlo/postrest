#!/bin/bash
scriptdir=$(dirname $0)
cd ${scriptdir}
dropdb --if-exists fantysuniversity 
createdb fantysuniversity -O exam
cat fantysuniversity-dump.sql  | psql -X fantysuniversity 

