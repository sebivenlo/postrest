# postrest
No pojo fidling when the database can do the major parts of the work

This project uses out of the box postgres functioinality. The demo project is built with the standard jee (version 8, but 7 would do) API.
The demo runs on payara5 without modification.

[PostgreSQL](https://www.postgresql.org/) has very powerfull json(b) functionality
built in. This project takes advantage of this by having postgresql do the heavy lifting
of assembling query results into json, or taking json documents apart to do modifying operation such as insert (post in rest) and update (put in rest).

This makes the standard CRUD operations through a rest api a no-brainer.

As an example:

```java
@Stateless
@Path( "students" )
public class StudentsService extends FantysCrudService {

    @Override
    protected String getRelName() {
        return "students";
    }

}
```

With the appropriate imports will make you a full crud service for a students table,
and the mentioned "self coded" FantysCrudService also contains next to nothing:

```java
abstract class FantysCrudService extends AbstractPostRestService {

    @Resource( lookup = "jdbc/fantys" )
    void setDataSource( DataSource ds ) {
        this.dataSource = ds;
    }
}
```

The interface to the heavy lifting delegating code (the delegate being the postgres server)
is in the `AbstractPostRestService` provided by this project.


To get the demo running, you must feed your postgres server with the data definition and initial data in the db folder.

For convenience a `.../db/loaddb.sh` script is provided, which should work on a decent OS-X or Linux with postgresql and psql (the postgres cli) installed.

