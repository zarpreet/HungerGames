package games;

import java.util.ArrayList;

/**
 * This class contains methods to represent the Hunger Games using BSTs.
 * Moves people from input files to districts, eliminates people from the game,
 * and determines a possible winner.
 * 
 * @author Pranay Roni
 * @author Maksims Kurjanovics Kravcenko
 * @author Kal Pandit
 */
public class HungerGames {

    private ArrayList<District> districts;  // all districts in Panem.
    private TreeNode            game;       // root of the BST. The BST contains districts that are still in the game.

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Default constructor, initializes a list of districts.
     */
    public HungerGames() {
        districts = new ArrayList<>();
        game = null;
        StdRandom.setSeed(2023);
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Sets up Panem, the universe in which the Hunger Games takes place.
     * Reads districts and people from the input file.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPanem(String filename) { 
        StdIn.setFile(filename);  // open the file - happens only once here
        setupDistricts(filename); 
        setupPeople(filename);
    }

    /**
     * Reads the following from input file:
     * - Number of districts
     * - District ID's (insert in order of insertion)
     * Insert districts into the districts ArrayList in order of appearance.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupDistricts (String filename) {

        int numDistricts = StdIn.readInt();

        // Loop through each district ID and add it to the districts ArrayList
        for (int i = 0; i < numDistricts; i++) {
            int districtId = StdIn.readInt();
            District newDistrict = new District(districtId); // Assuming District constructor takes districtId as a parameter
            districts.add(newDistrict);
        }
    }

    /**
     * Reads the following from input file (continues to read from the SAME input file as setupDistricts()):
     * Number of people
     * Space-separated: first name, last name, birth month (1-12), age, district id, effectiveness
     * Districts will be initialized to the instance variable districts
     * 
     * Persons will be added to corresponding district in districts defined by districtID
     * 
     * @param filename will be provided by client to read from using StdIn
     */
public void setupPeople(String filename) {
    // Open the input file for reading
    StdIn.setFile(filename);

    // Read the number of districts
    int numDistricts = StdIn.readInt();
    StdIn.readLine(); // Consume the newline

    // Read and skip districts
    for (int i = 0; i < numDistricts; i++) {
        StdIn.readLine(); // Consume the newline
    }

    // Read the number of players
    int numPlayers = StdIn.readInt();
    StdIn.readLine(); // Consume the newline

    // Read and add people (players) to their respective districts
    for (int i = 0; i < numPlayers; i++) {
        String firstName = StdIn.readString();
        String lastName = StdIn.readString();
        int birthMonth = StdIn.readInt();
        int age = StdIn.readInt();
        int districtID = StdIn.readInt();
        int effectiveness = StdIn.readInt();

        // Create a new Person object
        Person person = new Person(birthMonth, firstName, lastName, age, districtID, effectiveness);

        // Check if the person's age is between 12 and 18 (inclusive)
        if (age >= 12 && age < 18) {
            person.setTessera(true);
        }

        // Find the corresponding district based on districtID
        District targetDistrict = null;
        for (District district : districts) {
            if (district.getDistrictID() == districtID) {
                targetDistrict = district;
                break; // Found the district, so exit the loop
            }
        }

        // Add the person to the appropriate population list based on birth month
        if (targetDistrict != null) {
            if (birthMonth % 2 == 1) {
                targetDistrict.addOddPerson(person);
            } else {
                targetDistrict.addEvenPerson(person);
            }
        }
    }
}





    /**
     * Adds a district to the game BST.
     * If the district is already added, do nothing
     * 
     * @param root        the TreeNode root which we access all the added districts
     * @param newDistrict the district we wish to add
     */
public void addDistrictToGame(TreeNode root, District newDistrict) {
    if (root == null) {
        // If the root is null, it means the tree is empty,
        // so create a new TreeNode with the given district.
        game = new TreeNode(newDistrict, null, null);
    }
    else addDistrictToGameHelper(root,newDistrict);
}
private void addDistrictToGameHelper(TreeNode root, District newDistrict){
        if (newDistrict.getDistrictID() < root.getDistrict().getDistrictID()) {
        // If the new district's ID is less than the root's district ID,
        // insert it in the left subtree.
        if (root.getLeft() == null) {
            // If the left child is null, create a new TreeNode for the district.
            root.setLeft(new TreeNode(newDistrict, null, null));
        } else {
            // Otherwise, recursively insert in the left subtree.
            addDistrictToGame(root.getLeft(), newDistrict);
        }
    } else if (newDistrict.getDistrictID() > root.getDistrict().getDistrictID()) {
        // If the new district's ID is greater than the root's district ID,
        // insert it in the right subtree.
        if (root.getRight() == null) {
            // If the right child is null, create a new TreeNode for the district.
            root.setRight(new TreeNode(newDistrict, null, null));
        } else {
            // Otherwise, recursively insert in the right subtree.
            addDistrictToGameHelper(root.getRight(), newDistrict);
        }
    } 
}




    /**
     * Searches for a district inside of the BST given the district id.
     * 
     * @param id the district to search
     * @return the district if found, null if not found
     */
public District findDistrict(int id) {
    // Call a recursive helper method to search for the district.
    return findDistrictRecursive(game, id);
}

private District findDistrictRecursive(TreeNode node, int id) {
    // Base case: If the node is null, the district is not found.
    if (node == null) {
        return null;
    }

    // If the current node's district ID matches the target ID, return the district.
    if (node.getDistrict().getDistrictID() == id) {
        return node.getDistrict();
    }

    // If the target ID is less than the current node's district ID, search in the left subtree.
    if (id < node.getDistrict().getDistrictID()) {
        return findDistrictRecursive(node.getLeft(), id);
    }

    // If the target ID is greater than the current node's district ID, search in the right subtree.
    return findDistrictRecursive(node.getRight(), id);
}


    /**
     * Selects two duelers from the tree, following these rules:
     * - One odd person and one even person should be in the pair.
     * - Dueler with Tessera (age 12-18, use tessera instance variable) must be
     * retrieved first.
     * - Find the first odd person and even person (separately) with Tessera if they
     * exist.
     * - If you can't find a person, use StdRandom.uniform(x) where x is the respective 
     * population size to obtain a dueler.
     * - Add odd person dueler to person1 of new DuelerPair and even person dueler to
     * person2.
     * - People from the same district cannot fight against each other.
     * 
     * @return the pair of dueler retrieved from this method.
     */
public DuelPair selectDuelers() {
    // Initialize two DuelPair objects to store the selected duelers
    DuelPair duelPair = new DuelPair();

    // Lists to store potential duelers from odd and even populations
    ArrayList<Person> oddDuelersWithTessera = new ArrayList<>();
    ArrayList<Person> evenDuelersWithTessera = new ArrayList<>();

    // Traverse the game tree to find duelers
    selectDuelersHelper(game, oddDuelersWithTessera, evenDuelersWithTessera);

    // Check if there are children (tessera = true) available in odd population
    if (!oddDuelersWithTessera.isEmpty()) {
        duelPair.setPerson1(oddDuelersWithTessera.get(0));
    } else {
        // If no children are available, select a random person from the odd population
        int oddPopulationSize = countOddPopulation(game);
        int randomOddIndex = StdRandom.uniform(oddPopulationSize)-1;
        duelPair.setPerson1(selectRandomDueler(game, true, randomOddIndex));
    }

    // Check if there are children (tessera = true) available in even population
    if (!evenDuelersWithTessera.isEmpty()) {
        duelPair.setPerson2(evenDuelersWithTessera.get(0));
    } else {
        // If no children are available, select a random person from the even population
        int evenPopulationSize = countEvenPopulation(game);
        int randomEvenIndex = StdRandom.uniform(evenPopulationSize)-1;
        duelPair.setPerson2(selectRandomDueler(game, false, randomEvenIndex));
    }

    // Remove the selected duelers from their respective districts
    removeDuelersFromDistricts(game, duelPair);

    return duelPair;
}




// Helper method to select duelers with Tessera from distinct districts
private void selectDuelersHelper(TreeNode node, ArrayList<Person> oddDuelers, ArrayList<Person> evenDuelers) {
    if (node == null) {
        return;
    }

    District district = node.getDistrict();

    // Check for duelers with Tessera in the odd and even populations
    for (Person person : district.getOddPopulation()) {
        if (person.getTessera()) {
            oddDuelers.add(person);
        }
    }

    for (Person person : district.getEvenPopulation()) {
        if (person.getTessera()) {
            evenDuelers.add(person);
        }
    }

    // Recursively traverse left and right subtrees
    selectDuelersHelper(node.getLeft(), oddDuelers, evenDuelers);
    selectDuelersHelper(node.getRight(), oddDuelers, evenDuelers);
}

// Helper method to count the size of the odd population in a district
private int countOddPopulation(TreeNode node) {
    if (node == null) {
        return 0;
    }
    District district = node.getDistrict();
    return district.getOddPopulation().size() + countOddPopulation(node.getLeft()) + countOddPopulation(node.getRight());
}

// Helper method to count the size of the even population in a district
private int countEvenPopulation(TreeNode node) {
    if (node == null) {
        return 0;
    }
    District district = node.getDistrict();
    return district.getEvenPopulation().size() + countEvenPopulation(node.getLeft()) + countEvenPopulation(node.getRight());
}


// Helper method to select a random dueler from the odd or even population in the current district
private Person selectRandomDueler(TreeNode node, boolean isOdd, int index) {
    if (node == null) {
        return null;
    }

    District district = node.getDistrict();
    ArrayList<Person> population = isOdd ? district.getOddPopulation() : district.getEvenPopulation();
    
    if (index < population.size()) {
        return population.get(index);
    }

    // If the population is empty, try the left and right districts
    Person dueler = selectRandomDueler(node.getLeft(), isOdd, index - population.size());
    if (dueler == null) {
        dueler = selectRandomDueler(node.getRight(), isOdd, index - population.size());
    }

    return dueler;
}

// Helper method to remove selected duelers from their districts
private void removeDuelersFromDistricts(TreeNode node, DuelPair duelPair) {
    if (node == null) {
        return;
    }

    District district = node.getDistrict();
    Person person1 = duelPair.getPerson1();
    Person person2 = duelPair.getPerson2();

    if (person1 != null && district.getOddPopulation().contains(person1)) {
        district.getOddPopulation().remove(person1);
    } else if (person1 != null && district.getEvenPopulation().contains(person1)) {
        district.getEvenPopulation().remove(person1);
    }

    if (person2 != null && district.getOddPopulation().contains(person2)) {
        district.getOddPopulation(). remove(person2);
    } else if (person2 != null && district.getEvenPopulation().contains(person2)) {
        district.getEvenPopulation().remove(person2);
    }

    removeDuelersFromDistricts(node.getLeft(), duelPair);
    removeDuelersFromDistricts(node.getRight(), duelPair);
}



    /**
     * Deletes a district from the BST when they are eliminated from the game.
     * Districts are identified by id's.
     * If district does not exist, do nothing.
     * 
     * This is similar to the BST delete we have seen in class.
     * 
     * @param id the ID of the district to eliminate
     */
public void eliminateDistrict(int id) {
    game = deleteDistrict(game, id);
}

// Helper method to delete a district node with the given ID
private TreeNode deleteDistrict(TreeNode node, int id) {
    if (node == null) {
        // District not found, no action needed
        return null;
    }

    District district = node.getDistrict();
    if (id < district.getDistrictID()) {
        // Search in the left subtree
        node.setLeft(deleteDistrict(node.getLeft(), id));
    } else if (id > district.getDistrictID()) {
        // Search in the right subtree
        node.setRight(deleteDistrict(node.getRight(), id));
    } else {
        // Found the district node to eliminate

        // Case 1: No child or one child
        if (node.getLeft() == null) {
            return node.getRight();
        } else if (node.getRight() == null) {
            return node.getLeft();
        }

        // Case 2: Two children
        // Find the in-order successor in the right subtree
        TreeNode successor = findInOrderSuccessor(node.getRight());

        // Replace the node's district with the successor's district
        node.setDistrict(successor.getDistrict());

        // Delete the in-order successor from the right subtree
        node.setRight(deleteDistrict(node.getRight(), successor.getDistrict().getDistrictID()));
    }
    return node;
}

// Helper method to find the in-order successor (minimum node in the right subtree)
private TreeNode findInOrderSuccessor(TreeNode node) {
    while (node.getLeft() != null) {
        node = node.getLeft();
    }
    return node;
}


    /**
     * Eliminates a dueler from a pair of duelers.
     * - Both duelers in the DuelPair argument given will duel
     * - Winner gets returned to their District
     * - Eliminate a District if it only contains a odd person population or even
     * person population
     * 
     * @param pair of persons to fight each other.
     */
public void eliminateDueler(DuelPair pair) {
    Person person1 = pair.getPerson1();
    Person person2 = pair.getPerson2();

    // Check if the DuelPair is complete
    if (person1 == null || person2 == null) {
        // Incomplete pair, return the person to their respective district
        if (person1 != null) {
            returnDuelerToDistrict(person1);
        }
        if (person2 != null) {
            returnDuelerToDistrict(person2);
    } else {
        // Complete pair, have the duel and determine the winner and loser
        Person winner = person1.duel(person2);
        Person loser = (winner == person1) ? person2 : person1;

        // Return the winner to their district
        returnDuelerToDistrict(winner);

        // Return the loser to their district with an updated status (eliminated)
        returnDuelerToDistrict(loser);
    }
}
}


// Helper method to return a dueler to their respective district
private void returnDuelerToDistrict(Person dueler) {
    District district = findDistrict(dueler.getDistrictID());
    if (dueler.getBirthMonth() % 2 == 0) {
        district.getEvenPopulation().add(dueler);
    } else {
        district.getOddPopulation().add(dueler);
    }
}

// Helper method to check the population sizes of a district and eliminate it if needed
private void checkDistrictPopulation(int districtID) {
    District district = findDistrict(districtID);
    if (district.getOddPopulation().isEmpty() || district.getEvenPopulation().isEmpty()) {
        eliminateDistrict(districtID);
    }
}



    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Obtains the list of districts for the Driver.
     * 
     * @return the ArrayList of districts for selection
     */
    public ArrayList<District> getDistricts() {
        return this.districts;
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Returns the root of the BST
     */
    public TreeNode getRoot() {
        return game;
    }
}
