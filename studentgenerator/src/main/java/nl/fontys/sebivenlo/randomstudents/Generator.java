package nl.fontys.sebivenlo.randomstudents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import static java.nio.charset.Charset.defaultCharset;
import java.nio.file.Files;
import static java.nio.file.Files.readAllLines;
import java.nio.file.Paths;
import java.time.LocalDate;
import static java.time.LocalDate.now;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Pieter van den Hombergh {@code pieter.van.den.hombergh@gmail.com}
 */
public class Generator {

    public static void main( String[] args ) throws IOException {

        int count = 5;

        int a = 0;
        boolean json = false;
        boolean pretty = false;
        while ( a < args.length ) {
            try {
                count = Integer.parseInt( args[ a ] );
            } catch ( NumberFormatException ignore ) {
                // stay at default
            }
            if ( args[ a ].startsWith( "-j" ) ) {
                json = true;
            }
            if ( args[ a ].startsWith( "-p" ) ) {
                json=true;
                pretty = true;
            }
            a++;
        }

        Collection<Student> students = new Generator().generate( count );

        if ( json ) {
            System.out.println( toJson( students, pretty ) );
        } else {
            students.forEach( s -> {
                System.out.println( s.csvRecord() );
            } );
        }

    }

    String girlsNameFile = "girlsnames.txt";
    String boysNameFile = "boysnames.txt";
    String lastnameFile = "lastnames.txt";
    int number = 1;
    List<String> girls;
    List<String> boys;
    List<String> lastnames;

    List<String> gender = Arrays.asList( "M", "F" );

    public Generator() throws IOException {

        girls = readAllLines( Paths.get( girlsNameFile ), defaultCharset() );
        boys = readAllLines( Paths.get( boysNameFile ), defaultCharset() );
        lastnames = readAllLines( Paths.get( lastnameFile ), defaultCharset() );
    }

    Random rnd = new Random();

    Student generate() {
        String g = rndFromList( gender );
        String firstname = null;
        switch (g) {
            case "M":
                firstname = rndFromList( boys );
                break;
            case "F":
                firstname = rndFromList( boys );
                break;

        }
        LocalDate dob = now().minusYears( 25 ).
                plusDays( rnd.nextInt( 7 * 365 ) );
        String lastname = rndFromList( lastnames );
        String email = ( firstname + lastname ).toLowerCase()
                + "@student.fantys.nl";
        return new Student( number++, lastname, firstname, dob, now().getYear(),
                email, g, "NEW" );
    }

    /**
     * Generate a collection of students with random names and birth dates. The
     * generated collection is guaranteed to contain students that are unique in
     * firstname+lastname+birthdate+gender
     *
     * @param count
     * @return
     */
    public Collection<Student> generate( int count ) {
        Set<Student> result = new LinkedHashSet<>();

        while ( result.size() < count ) {
            result.add( generate() );
        }
        return result;
    }

    private String rndFromList( List<String> list ) {
        return list.get( rnd.nextInt( list.size() ) );
    }

    /**
     * Return collection as json array.
     *
     * @param <E> the generic type of the collection.
     * @param col to convert
     * @return a json array
     */
    public static <E> String toJson( Collection<E> col, boolean pretty ) {
        GsonBuilder builder
                = new GsonBuilder().registerTypeAdapter( LocalDate.class,
                        new LocalDateJsonAdapter() );
        if ( pretty ) {
            builder.setPrettyPrinting();
        }
        Gson gson = builder.create();
        return gson.toJson( col );
    }

}
