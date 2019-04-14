package nl.fontys.sebivenlo.randomstudents;

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
        if ( args.length > 0 ) {
            try {
                count = Integer.parseInt( args[ 0 ] );
            } catch ( NumberFormatException ignore ) {
                // stay at default
            }
        }

        Collection<Student> students = new Generator().generate( count );

        students.forEach( s -> {
            System.out.println( s.csvRecord() );
        } );

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
        String email = (firstname + lastname).toLowerCase() + "@student.fantys.nl";
        return new Student( number++, lastname, firstname, dob, now().getYear(),
                email, g, "NEW" );
    }

    Collection<Student> generate( int count ) {
        Set<Student> result = new LinkedHashSet<>();

        while ( result.size() < count ) {
            result.add( generate() );
        }
        return result;
    }

    private String rndFromList( List<String> list ) {
        return list.get( rnd.nextInt( list.size() ) );
    }
}
