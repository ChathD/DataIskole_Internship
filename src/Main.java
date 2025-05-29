import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        String nodesFile = "superheroes.csv";
        String edgesFile = "links.csv";

        ArrayList<Superhero> superheroNodes = new ArrayList<>();
        Map<String, Superhero> superheroMap = new HashMap<>();


        loadSuperheroes(nodesFile, superheroNodes, superheroMap);
        int edgeCount = loadConnections(edgesFile, superheroMap);
        System.out.println("Total superheroes (nodes): " + superheroNodes.size());
        System.out.println("Total connections (edges): " + edgeCount);

        Set<LocalDate> uniqueDates = new HashSet<>();
        for (Superhero s2 : superheroNodes) {
            LocalDate date1 = LocalDate.parse(s2.createdAt);
            uniqueDates.add(date1);
        }
        List<LocalDate> sortedDates = new ArrayList<>(uniqueDates);
        Collections.sort(sortedDates);
        int size = sortedDates.size();
        List<LocalDate> lastThree = sortedDates.subList(Math.max(size - 3, 0), size);

        System.out.println("Superheroes added in the last 3 days:");
        for (Superhero s3 : superheroNodes) {
            LocalDate date1 = LocalDate.parse(s3.createdAt);
            if (lastThree.contains(date1)) {
                System.out.println(s3);
            }
        }

        System.out.println("top 3 most connected superheroes:");
        superheroNodes.sort((s1, s2) -> Integer.compare(s2.superheroes.size(), s1.superheroes.size()));

        int topCount = Math.min(3, superheroNodes.size());
        for (int i = 0; i < topCount; i++) {
            System.out.println(superheroNodes.get(i));
        }

        String date = "";
        ArrayList<Superhero> d_superheroes = new ArrayList<>();
        ;
        for (Superhero s6 : superheroNodes) {
            if (Objects.equals(s6.name, "dataiskole")) {
                date = s6.createdAt;
                d_superheroes = s6.superheroes;
            }
        }
        if (!date.isEmpty()) {
            System.out.println("Data iskole was created At:" + date);
            System.out.println("Data iskole friends are:");
            for (Superhero friend : d_superheroes) {
                System.out.println(friend);
            }
        } else {
            System.out.println("Superhero 'dataiskole' not found.");
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Superhero Menu ===");
            System.out.println("1. Add new superhero");
            System.out.println("2. Add new connection");
            System.out.println("3. Exit");
            System.out.print("Choose an option (1-3): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addSuperhero(scanner, nodesFile);
                    break;
                case "2":
                    addConnection(scanner, nodesFile, edgesFile);
                    break;
                case "3":
                    System.out.println("Exiting program.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addSuperhero(Scanner scanner, String FILE_PATH) {
        int newId = getNextSuperheroId(FILE_PATH);

        System.out.print("Enter superhero name: ");
        String name = scanner.nextLine().trim();
        if (!isValidName(name)) {
            System.out.println("❌ Invalid name. Only letters are allowed.");
            return;
        }
        String createdAt = LocalDate.now().toString();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    String existingName = parts[1].trim();
                    if (existingName.equalsIgnoreCase(name)) {
                        System.out.println("⚠️ Superhero with name '" + name + "' already exists.");
                        return;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
            return;
        }

        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(newId + "," + name + "," + createdAt + "\n");
            System.out.println("✅ Superhero added with ID: " + newId);
        } catch (IOException e) {
            System.out.println("❌ Error writing to file: " + e.getMessage());
        }
        Map<String, Superhero> superheroMap = new HashMap<>();
        ArrayList<Superhero> superheroNodes = new ArrayList<>();
        loadSuperheroes(FILE_PATH, superheroNodes, superheroMap);
        System.out.println("Total superheroes (nodes): " + superheroNodes.size());

    }

    private static int getNextSuperheroId(String filePath) {
        int nextId = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line;
            String lastLine = null;

            while ((line = br.readLine()) != null) {
                lastLine = line;
            }

            if (lastLine != null) {
                String[] parts = lastLine.split(",", -1);
                if (parts.length >= 1) {
                    nextId = Integer.parseInt(parts[0].trim()) + 1;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("⚠️ Error reading file for ID: " + e.getMessage());
        }

        return nextId;
    }

    private static void addConnection(Scanner scanner, String SUPERHEROES_FILE, String CONNECTIONS_FILE) {
        Map<String, String> idNameMap = loadSuperheroIdNameMap(SUPERHEROES_FILE);
        if (idNameMap.isEmpty()) {
            System.out.println("⚠️ No superheroes available to connect.");
            return;
        }

        System.out.println("Available superheroes:");
        idNameMap.forEach((id, name) -> System.out.println("ID: " + id + " - Name: " + name));

        System.out.print("Enter source superhero ID: ");
        String source = scanner.nextLine().trim();
        System.out.print("Enter target superhero ID: ");
        String target = scanner.nextLine().trim();

        if (!idNameMap.containsKey(source) || !idNameMap.containsKey(target)) {
            System.out.println("❌ Invalid superhero ID(s).");
            return;
        }
        String newConnection = "";
        HashMap<String, ArrayList<String>> existingConnections = loadExistingConnections(CONNECTIONS_FILE);

        if (existingConnections.containsKey(source)) {
            ArrayList<String> list = existingConnections.get(source);
            if (list.contains(target)) {
                System.out.println("⚠️ Connection already exists.");
                return;
            }

        }

        if (existingConnections.containsKey(target)) {
            ArrayList<String> list = existingConnections.get(target);
            if (list.contains(source)) {
                System.out.println("⚠️ Connection already exists.");
                return;
            }

        }

        if (!source.equals(target)) {
            newConnection = source + "," + target;
        } else {
            System.out.println("Source and target cannot be the same");
            return;
        }


        try (FileWriter writer = new FileWriter(CONNECTIONS_FILE, true)) {
            writer.write(newConnection + "\n");

            System.out.println("✅ Connection added between " + idNameMap.get(source) + " and " + idNameMap.get(target));
        } catch (IOException e) {
            System.out.println("❌ Error writing to connections file: " + e.getMessage());
        }
        Map<String, Superhero> superheroMap = new HashMap<>();
        ArrayList<Superhero> superheroNodes = new ArrayList<>();
        loadSuperheroes(SUPERHEROES_FILE, superheroNodes, superheroMap);
        int edgeCount = loadConnections(CONNECTIONS_FILE, superheroMap);
        System.out.println("Total connections (edges): " + edgeCount);
    }

    private static Map<String, String> loadSuperheroIdNameMap(String SUPERHEROES_FILE) {
        Map<String, String> idNameMap = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SUPERHEROES_FILE))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    idNameMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error reading superhero file: " + e.getMessage());
        }
        return idNameMap;
    }

    private static HashMap<String, ArrayList<String>> loadExistingConnections(String CONNECTIONS_FILE) {
        HashMap<String, ArrayList<String>> connections = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CONNECTIONS_FILE))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 2) {
                    String source = parts[0].trim();
                    String target = parts[1].trim();
                    if (connections.containsKey(source)) {
                        ArrayList<String> list = connections.get(source);
                        list.add(target);
                    } else {
                        connections.putIfAbsent(source, new ArrayList<>());
                        ArrayList<String> list = connections.get(source);
                        list.add(target);
                    }

                }
            }
            return connections;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isValidName(String name) {
        return name.matches("[a-zA-Z\\s\\-]+");
    }


    public static int loadConnections(String edgesFile, Map<String, Superhero> superheroMap) {
        int edgeCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(edgesFile))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 2) {
                    String source = parts[0].trim();
                    String target = parts[1].trim();

                    Superhero sourceHero = superheroMap.get(source);
                    Superhero targetHero = superheroMap.get(target);
                    if (sourceHero != null && targetHero != null) {
                        sourceHero.superheroes.add(targetHero);
                        edgeCount++;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading edges file: " + e.getMessage());
        }
        return edgeCount;
    }

    public static void loadSuperheroes(String nodesFile, ArrayList<Superhero> superheroNodes, Map<String, Superhero> superheroMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(nodesFile))) {
            br.readLine(); // Skip header line

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String createdAt = parts[2].trim();
                    Superhero hero = new Superhero(id, name, createdAt);
                    superheroNodes.add(hero);
                    superheroMap.put(id, hero);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading nodes file: " + e.getMessage());
        }
    }


}




