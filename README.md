# postrest
no pojo fidling when the database can do the major parts of the work


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

