package dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DirectorFilms {
    private String name;
    private List<Film> films;

    public DirectorFilms(String name, List<Film> films) {
        this.name = name;
        this.films = films;
    }

    public DirectorFilms () {
        films = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Film> getFilms() {
        return films;
    }

    public void setFilms(List<Film> films) {
        this.films = films;
    }

    public void addFilms(Film f) {
        this.films.add(f);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectorFilms that = (DirectorFilms) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getFilms(), that.getFilms());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getFilms());
    }

    @Override
    public String toString() {
        return "DirectorFilms{" +
                "\n\tname='" + name + '\'' +
                "\n\tfilms=" + films.toString() +
                "}\n\n";
    }
}
