import java.util.ArrayList;

public class Superhero {
        String id;
        String name;
        String createdAt;

        ArrayList<Superhero>  superheroes;

        Superhero(String id, String name, String createdAt) {
            this.id = id;
            this.name = name;
            this.createdAt = createdAt;
            this.superheroes = new ArrayList<>();
        }

    @Override
    public String toString() {
        return "Superhero{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}

