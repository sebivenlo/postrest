package nl.fontys.sebivenlo.postrest;

import java.util.function.Function;
import javax.ws.rs.NotFoundException;

/**
 * Distinguish between simple JSON object and JSON Array.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public enum JSONType implements Function<String, String> {
    /**
     * Use a JSONOBJECT if you expect one element and want to receive an exception when nothing is
     * found.
     */
    JSONOBJECT( "with t as (%s) select to_jsonb(t) from t" ) {
        @Override
        public void handleNothing() {
            throw new NotFoundException();
        }

    },
    /**
     * No wrapping, when query already does the right thing, such as returning json.
     */
    JSONPASSTHOUGH( "%s" ),
    /**
     * Use when you expect a json array. Not found results in an empty list.
     *
     * Last param, true, is about pretty printing. Cause transport overhead, but looks nicer.
     *
     */
    JSONARRAY( "with t as (%s) select array_to_json(array_agg(t),true) from t" );
    private final String queryTemplate;

    private JSONType( String template ) {
        this.queryTemplate = template;
    }

    /**
     * Hook for resource not available. By default does nothing.
     */
    public void handleNothing() {
    }

    /**
     * Convert the query text into a query producing JSON output.
     *
     * @param queryText template to be turned into one producing json
     *
     * @return the template applied on the query text.
     */
    @Override
    public String apply( String queryText ) {
        return String.format( queryTemplate, queryText );
    }

}
