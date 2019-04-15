# Run demo

To run this demo you must have psotgresql installed and psql on the command line.

Next there must be a postgres user named exam with password exam.

To create and load the database run
./db/load.sh

then build the project using maven in the usual way and deploy the war to
your web container.
