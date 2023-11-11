package dto;

import java.util.Objects;

public class Genre {
    private int id;
    private String name;
    private boolean isValid;



    public Genre() {
        isValid = true;
    }

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
        isValid = true;
    }


    public boolean isGenreNameValid() {
        return this.name.matches("^[a-zA-Z\\s\\-]+$");
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return id + "-" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return Objects.equals(getId(), genre.getId()) && Objects.equals(getName(), genre.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
