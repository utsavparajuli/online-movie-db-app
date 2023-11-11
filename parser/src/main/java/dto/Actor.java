package dto;

import java.time.Year;
import java.util.Objects;
import java.util.UUID;

public class Actor {
    private UUID id;
    private String name;
    private Year birthYear;

    public boolean isInValid() {
        return inValid;
    }

    public void setInValid(boolean inValid) {
        this.inValid = inValid;
    }

    private boolean inValid;


    public Actor() {
        birthYear = null;
        inValid = false;
        id = UUID.randomUUID();
    }

    public Actor(String name, Year birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public boolean isNameValid() {
        return this.name.matches("^[a-zA-Z.'\\-\\s]+$");
    }



    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Year getBirthYear() {
        if (birthYear == null) {
            return Year.parse("0000");
        }
        else {
            return birthYear;
        }
    }

    public void setBirthYear(Year birthYear) {
        this.birthYear = birthYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Actor actor = (Actor) o;
        return Objects.equals(getName(), actor.getName());

//        return Objects.equals(getName(), actor.getName()) && Objects.equals(getBirthYear(), actor.getBirthYear());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return String.format("Actor " +
                "\t%s = %-10s" + // Left-justify id with a width of 10
                "\t%s = %-50s" + // Left-justify title with a width of 10
                "\t%s = %-8s" +   // Left-justify year with a width of 10
                "", "id", id, "name", name, "dob", birthYear);
    }

}
