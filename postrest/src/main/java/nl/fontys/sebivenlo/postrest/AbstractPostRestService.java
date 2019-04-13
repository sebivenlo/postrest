package nl.fontys.sebivenlo.postrest;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import javax.ws.rs.core.UriInfo;
import static nl.fontys.sebivenlo.postrest.JSONQueryUtils.queryToJsonString;
import static nl.fontys.sebivenlo.postrest.JSONType.JSONOBJECT;
import static nl.fontys.sebivenlo.postrest.QueryRegistry.QUERY_REGISTRY;

/**
 * Generic CRUD service with GET, getAll, POST, PUT and DELETE. There is one
 * injection point, the data source. Otherwise the service has no knowledge of
 * the database or schema, other than that it relies on PostgreSQL
 * functionality. The convention that this service is based upon is that each
 * table or view has one numerical primary column. The primary column can have
 * any name, a example of good style would be to use 'student_id' from the table
 * 'students'.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public abstract class AbstractPostRestService {

    private Logger logger = null;

    protected Logger getLogger() {
        if ( logger == null ) {
            logger = Logger.getLogger( getClass().getName() );
        }
        return logger;
    }

    /**
     * The data source to be used by this CrudService.
     * 
     * This data source must be injected at the client side, for instance by 
     * providing a construct like:
     * <pre>
       &#64;Resource( lookup = "jdbc/fantys" )
       void setDataSource( DataSource ds ) {
            this.dataSource = ds;
       }
     * 
     * </pre>
     * Failing to do so will make this API useless.
     */
    protected DataSource dataSource;

    /**
     * Get the table or view name for the crud operations of this service.
     *
     * @return the name of the table or view.
     */
    protected abstract String getRelName();

    /**
     * Get the name of the primary column. Note that the primary column of views
     * is not well defined, which is why you need to overwrite this method if
     * you access a table (or view) without a primary key. If the key is not
     * primary, it must be unique and not null for the relation, otherwise this
     * service will not work.     *
     *
     * @return the name of the primary column.
     * @throws java.sql.SQLException when the data provided does not comply with
     * the database, or the database is not available.
     */
    protected String getIdName() throws SQLException {
        return QUERY_REGISTRY.getPrimaryColumnName( dataSource, getRelName() );
    }

    /**
     * Get all entities of this resource as a json array.
     *
     * @return the json array with the entities, if any.
     */
    @GET
    @Produces( value = { MediaType.APPLICATION_JSON } )
    public Response getEntities() {
        String qt = String.format( "select * from %s", getRelName() );
        getLogger().log( Level.INFO, "qt={0}", qt );
        String qr = "";
        try {
            qr = JSONQueryUtils
                    .queryToJsonString( dataSource, qt, JSONType.JSONARRAY );
        } catch ( SQLException ex ) {
            Logger.getLogger(AbstractPostRestService.class.getName() ).log(
                    Level.SEVERE, null, ex );
            return Response.serverError().build();
        }
        if ( qr.isEmpty() ) {
            return Response.status( NOT_FOUND ).build();
        }
        return Response.ok( qr ).build();
    }

    static final String getByIdQuery = "select * from %s where %s=?";

    /**
     * Get one entity. 
     * 
     * Implementation detailed steps:
     * <ul>
     * <li>Declare method getEntityById that returns Response.</li>
     * <li>Annotate method as RESTful service.</li>
     * <li>Use the above defined query string "getByIdQuery".</li>
     * <li>Use the method getIdName() to obtain the primary key column name</li>
     * <li>If query is successful, return Response OK, otherwise return Response
     * status NOT_FOUND.</li>
     * <li>In case of an SQLException return a serverError Response.</li>
     * </ul>
     *
     * @param id of the entity as an integer.
     *
     * @return the entity as json or status 404 (Not Found).
     */
    @GET
    @Path( value = "{id}" )
    @Produces( value = { MediaType.APPLICATION_JSON } )
    public Response getEntityById(
            @PathParam( value = "id" ) Integer id ) {
        String qresult = "";
        try {
            final String queryText
                    = String.format( getByIdQuery, getRelName(), getIdName() );
            qresult = queryToJsonString( dataSource, queryText, JSONOBJECT, id );

            if ( qresult.isEmpty() ) {
                return Response.status( NOT_FOUND ).build();
            }
            return Response.ok( qresult ).build();

        } catch ( SQLException ex ) {
            Logger.getLogger(AbstractPostRestService.class.getName() ).log(
                    Level.SEVERE, null, ex );
            return Response.serverError().build();
        }
    }

    /**
     * Delete the entity via ID.
     *
     * @param id of the entity
     *
     * @return the removed entity.
     */
    @DELETE
    @Produces( value = { MediaType.APPLICATION_JSON } )
    @Path( "{id}" )
    public Response delete( @PathParam( "id" ) Integer id ) {
        String qt = "delete from %s where %s= ? returning *";
        System.out.println( "about to delete " + id );
        String result = "";
        try {
            final String queryText = String.format( qt, getRelName(),
                    getIdName() );
            result = queryToJsonString( dataSource, queryText, JSONOBJECT, id );
        } catch ( SQLException sqe ) {
            return Response.status( NOT_FOUND ).build();
        }

        return Response.ok( result ).build();
    }

    /**
     * Insert an new entity. The primary key is generated by the database.
     *
     * @param json data defining the new entity
     * @param uriInfo the uri context from the server
     *
     * @return the new entity, including the generated key and any other default
     * values it may have, such as creation datestamp.
     */
    @POST
    @Consumes( value = { MediaType.APPLICATION_JSON } )
    @Produces( value = { MediaType.APPLICATION_JSON } )
    public Response insertEntity( String json, @Context UriInfo uriInfo ) {
        String rn = getRelName();
        String qt = QUERY_REGISTRY.getQuery( "newentity" );
        System.out.println( "qt = " + qt );
        String result = "";
        try {
            String idn = getIdName();
            String seqn = rn + '_' + idn + "_seq";
            final String queryText = String.format( qt, rn, rn, idn, seqn );
            getLogger().info( "about to insert " + json + " with query "
                    + queryText );
            result
                    = queryToJsonString( dataSource, queryText, JSONOBJECT, json );
        } catch ( SQLException ex ) {
            Logger.getLogger(AbstractPostRestService.class.getName() ).log(
                    Level.SEVERE, null, ex );
            return Response.notModified().build();
        }
        return Response.ok( result ).build();
    }

    /**
     * Update an entity.
     *
     * The json object is expected to be complete with updated values. The
     * primary key value is taken from the json object. If the record with that
     * primary key does not pre-exists in the database, a 404 will be returned.
     *
     * @param json to be processed
     *
     * @return the updated record.
     */
    @PUT
    @Consumes( value = { MediaType.APPLICATION_JSON } )
    @Produces( value = { MediaType.APPLICATION_JSON } )
    public Response updateEntity( String json ) {
        String rn = getRelName();
        String result = "";
        try {
            final String queryText = JSONQueryUtils.createUpdateQueryText( 
                    dataSource, rn, this.getIdName() );
            System.out.println( "queryText = " + queryText );
            System.out.println( "json = " + json );
            result = queryToJsonString( dataSource, queryText,
                    JSONType.JSONOBJECT, json );
            System.out.println( "result = " + result );
        } catch ( SQLException ex ) {
            Logger.getLogger(AbstractPostRestService.class.getName() ).log( 
                    Level.SEVERE, null, ex );
            return Response.serverError().build(); // explain
        }

        return Response.ok( result ).build();
    }
}
