package nl.fontys.sebivenlo.randomstudents;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author Pieter van den Hombergh {@code pieter.van.den.hombergh@gmail.com}
 */
public class Student implements Serializable {

    Integer snummer;
    String lastname;
    String tussenvoegsel;
    String firstname;
    LocalDate dob;
    int cohort;
    String email;
    String gender;
    String student_class;

    public Integer getNaturalId() {
        return getId();
    }

    public int getId() {
        return snummer;
    }

    public Student( Integer snummer, String lastname, String firstname,
            LocalDate dob, int cohort, String email, String gender,
            String student_class ) {
        this( snummer, lastname, null, firstname, dob, cohort, email, gender,
                student_class );
    }

    public Student( Integer snummer, String lastname, String tussenvoegsel,
            String firstname, LocalDate dob, int cohort, String email,
            String gender, String studet_class ) {
        this.snummer = snummer;
        this.lastname = lastname;
        this.tussenvoegsel = tussenvoegsel;
        this.firstname = firstname;
        this.dob = dob;
        this.cohort = cohort;
        this.email = email;
        this.gender = gender;
        this.student_class = studet_class;
    }

    public Integer getSnummer() {
        return snummer;
    }

    public void setSnummer( Integer snummer ) {
        this.snummer = snummer;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname( String lastname ) {
        this.lastname = lastname;
    }

    public String getTussenvoegsel() {
        return tussenvoegsel;
    }

    public void setTussenvoegsel( String tussenvoegsel ) {
        this.tussenvoegsel = tussenvoegsel;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname( String firstname ) {
        this.firstname = firstname;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob( LocalDate dob ) {
        this.dob = dob;
    }

    public int getCohort() {
        return cohort;
    }

    public void setCohort( int cohort ) {
        this.cohort = cohort;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender( String gender ) {
        this.gender = gender;
    }

    public String getStudent_class() {
        return student_class;
    }

    public void setStudent_class( String student_class ) {
        this.student_class = student_class;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode( this.lastname );
        hash = 79 * hash + Objects.hashCode( this.tussenvoegsel );
        hash = 79 * hash + Objects.hashCode( this.firstname );
        hash = 79 * hash + Objects.hashCode( this.dob );
        hash = 79 * hash + this.cohort;
        hash = 79 * hash + Objects.hashCode( this.email );
        hash = 79 * hash + Objects.hashCode( this.gender );
        return hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Student other = (Student) obj;
        if ( this.cohort != other.cohort ) {
            return false;
        }
        if ( !Objects.equals( this.lastname, other.lastname ) ) {
            return false;
        }
        if ( !Objects.equals( this.tussenvoegsel, other.tussenvoegsel ) ) {
            return false;
        }
        if ( !Objects.equals( this.firstname, other.firstname ) ) {
            return false;
        }
        if ( !Objects.equals( this.email, other.email ) ) {
            return false;
        }
        if ( !Objects.equals( this.gender, other.gender ) ) {
            return false;
        }
        return Objects.equals( this.dob, other.dob );
    }

    @Override
    public String toString() {
        return "Student{" + "snummer=" + snummer + ", lastname=" + lastname
                + ", tussenvoegsel="
                + tussenvoegsel + ", firstname=" + firstname + ", cohort="
                + cohort + ", email=" + email + ", gender=" + gender
                + ", student_class=" + student_class + '}';
    }

    String csvRecord() {
        return "" + snummer + ";" + lastname + ";" //+ tussenvoegsel + ";"
                + firstname + ";" + dob + ";" + cohort + ";" + email + ";"
                + gender + ";" + student_class;

    }

}
