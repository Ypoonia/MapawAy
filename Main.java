import java.util.*;
import java.lang.Math;

public class MetroApp {

    // The Graph_M class
    public static class Graph_M {

        // Each vertex stores its neighbours (station name and weight)
        public static class Vertex {
            HashMap<String, Integer> neighbours = new HashMap<>();
        }

        // The metro map (shared across all Graph_M objects)
        public static HashMap<String, Vertex> vtces = new HashMap<>();

        // Constructor
        public Graph_M() {
            vtces.clear();
        }

        public int numVetex() {
            return vtces.size();
        }

        public boolean containsVertex(String vname) {
            return vtces.containsKey(vname);
        }

        public void addVertex(String vname) {
            vtces.put(vname, new Vertex());
        }

        public void removeVertex(String vname) {
            Vertex vtx = vtces.get(vname);
            if (vtx != null) {
                ArrayList<String> keys = new ArrayList<>(vtx.neighbours.keySet());
                for (String key : keys) {
                    Vertex nbrVtx = vtces.get(key);
                    if (nbrVtx != null) {
                        nbrVtx.neighbours.remove(vname);
                    }
                }
            }
            vtces.remove(vname);
        }

        public int numEdges() {
            int count = 0;
            for (Vertex vtx : vtces.values()) {
                count += vtx.neighbours.size();
            }
            return count / 2;
        }

        public boolean containsEdge(String vname1, String vname2) {
            if (!vtces.containsKey(vname1) || !vtces.containsKey(vname2))
                return false;
            return vtces.get(vname1).neighbours.containsKey(vname2);
        }

        public void addEdge(String vname1, String vname2, int value) {
            if (!vtces.containsKey(vname1) || !vtces.containsKey(vname2))
                return;
            if (vtces.get(vname1).neighbours.containsKey(vname2))
                return;
            vtces.get(vname1).neighbours.put(vname2, value);
            vtces.get(vname2).neighbours.put(vname1, value);
        }

        public void removeEdge(String vname1, String vname2) {
            if (!vtces.containsKey(vname1) || !vtces.containsKey(vname2))
                return;
            if (!vtces.get(vname1).neighbours.containsKey(vname2))
                return;
            vtces.get(vname1).neighbours.remove(vname2);
            vtces.get(vname2).neighbours.remove(vname1);
        }

        // Displays the metro map with station names, neighbours, and weights
        public void display_Map() {
            System.out.println("\t Delhi Metro Map");
            System.out.println("\t------------------");
            System.out.println("----------------------------------------------------");
            System.out.println();
            for (Map.Entry<String, Vertex> entry : vtces.entrySet()) {
                String key = entry.getKey();
                String str = key + " =>\n";
                Vertex vtx = entry.getValue();
                for (Map.Entry<String, Integer> nbrEntry : vtx.neighbours.entrySet()) {
                    String nbr = nbrEntry.getKey();
                    str += "\t" + nbr + "\t";
                    if (nbr.length() < 16)
                        str += "\t";
                    if (nbr.length() < 8)
                        str += "\t";
                    str += nbrEntry.getValue() + "\n";
                }
                System.out.println(str);
            }
            System.out.println("\t------------------");
            System.out.println("---------------------------------------------------");
            System.out.println();
        }

        // Lists all the stations
        public void display_Stations() {
            System.out.println();
            System.out.println("*");
            System.out.println();
            int i = 1;
            for (String key : vtces.keySet()) {
                System.out.println(i + ". " + key);
                i++;
            }
            System.out.println();
            System.out.println("*");
            System.out.println();
        }

        // Checks if there is a path between two stations (using recursion)
        public boolean hasPath(String vname1, String vname2, HashMap<String, Boolean> processed) {
            if (containsEdge(vname1, vname2))
                return true;
            processed.put(vname1, true);
            Vertex vtx = vtces.get(vname1);
            for (String nbr : vtx.neighbours.keySet()) {
                if (!processed.containsKey(nbr)) {
                    if (hasPath(nbr, vname2, processed))
                        return true;
                }
            }
            return false;
        }

        // Helper class for Dijkstra's algorithm
        public static class DijkstraPair implements Comparable<DijkstraPair> {
            String vname;
            String psf; // path so far
            int cost;

            public DijkstraPair() {
            }

            public DijkstraPair(String vname, String psf, int cost) {
                this.vname = vname;
                this.psf = psf;
                this.cost = cost;
            }

            @Override
            public int compareTo(DijkstraPair other) {
                return this.cost - other.cost;
            }
        }

        // Dijkstra's algorithm to compute shortest distance/time (controlled by the flag 'nan')
        public int dijkstra(String src, String des, boolean nan) {
            int val = 0;
            ArrayList<String> ans = new ArrayList<>();
            HashMap<String, DijkstraPair> map = new HashMap<>();
            PriorityQueue<DijkstraPair> pq = new PriorityQueue<>();

            for (String key : vtces.keySet()) {
                DijkstraPair np = new DijkstraPair();
                np.vname = key;
                np.cost = Integer.MAX_VALUE;
                np.psf = "";
                if (key.equals(src)) {
                    np.cost = 0;
                    np.psf = key;
                }
                pq.add(np);
                map.put(key, np);
            }

            while (!pq.isEmpty()) {
                DijkstraPair rp = pq.poll();
                if (rp.vname.equals(des)) {
                    val = rp.cost;
                    break;
                }
                map.remove(rp.vname);
                ans.add(rp.vname);
                Vertex v = vtces.get(rp.vname);
                for (Map.Entry<String, Integer> entry : v.neighbours.entrySet()) {
                    String nbr = entry.getKey();
                    if (map.containsKey(nbr)) {
                        int oc = map.get(nbr).cost;
                        int edgeWeight = entry.getValue();
                        int nc;
                        if (nan)
                            nc = rp.cost + 120 + 40 * edgeWeight;
                        else
                            nc = rp.cost + edgeWeight;
                        if (nc < oc) {
                            DijkstraPair gp = map.get(nbr);
                            gp.psf = rp.psf + nbr;
                            gp.cost = nc;
                            pq.add(new DijkstraPair(gp.vname, gp.psf, gp.cost));
                        }
                    }
                }
            }
            return val;
        }

        // Helper class used in the path and fare functions
        public static class Pair {
            String vname;
            String psf;
            int min_dis;
            int min_time;
        }

        // Gets the shortest path (distance-wise) as a string; the total distance is appended at the end.
        public String Get_Minimum_Distance(String src, String dst) {
            int min = Integer.MAX_VALUE;
            String ans = "";
            HashMap<String, Boolean> processed = new HashMap<>();
            Deque<Pair> stack = new ArrayDeque<>();
            Pair sp = new Pair();
            sp.vname = src;
            sp.psf = src + "  ";
            sp.min_dis = 0;
            sp.min_time = 0;
            stack.addFirst(sp);

            while (!stack.isEmpty()) {
                Pair rp = stack.removeFirst();
                if (processed.containsKey(rp.vname))
                    continue;
                processed.put(rp.vname, true);
                if (rp.vname.equals(dst)) {
                    int temp = rp.min_dis;
                    if (temp < min) {
                        ans = rp.psf;
                        min = temp;
                    }
                    continue;
                }
                Vertex rpvtx = vtces.get(rp.vname);
                for (Map.Entry<String, Integer> entry : rpvtx.neighbours.entrySet()) {
                    String nbr = entry.getKey();
                    if (!processed.containsKey(nbr)) {
                        Pair np = new Pair();
                        np.vname = nbr;
                        np.psf = rp.psf + nbr + "  ";
                        np.min_dis = rp.min_dis + entry.getValue();
                        stack.addFirst(np);
                    }
                }
            }
            ans = ans + min;
            return ans;
        }

        // Gets the shortest path (time-wise) as a string; the total time (in minutes) is appended.
        public String Get_Minimum_Time(String src, String dst) {
            int min = Integer.MAX_VALUE;
            String ans = "";
            HashMap<String, Boolean> processed = new HashMap<>();
            Deque<Pair> stack = new ArrayDeque<>();
            Pair sp = new Pair();
            sp.vname = src;
            sp.psf = src + "  ";
            sp.min_dis = 0;
            sp.min_time = 0;
            stack.addFirst(sp);

            while (!stack.isEmpty()) {
                Pair rp = stack.removeFirst();
                if (processed.containsKey(rp.vname))
                    continue;
                processed.put(rp.vname, true);
                if (rp.vname.equals(dst)) {
                    int temp = rp.min_time;
                    if (temp < min) {
                        ans = rp.psf;
                        min = temp;
                    }
                    continue;
                }
                Vertex rpvtx = vtces.get(rp.vname);
                for (Map.Entry<String, Integer> entry : rpvtx.neighbours.entrySet()) {
                    String nbr = entry.getKey();
                    if (!processed.containsKey(nbr)) {
                        Pair np = new Pair();
                        np.vname = nbr;
                        np.psf = rp.psf + nbr + "  ";
                        np.min_time = rp.min_time + 120 + 40 * entry.getValue();
                        stack.addFirst(np);
                    }
                }
            }
            int minutes = (int) Math.ceil((double) min / 60);
            ans = ans + minutes;
            return ans;
        }

        // Splits the path string (created by the above methods) to determine interchanges.
        public ArrayList<String> get_Interchanges(String str) {
            ArrayList<String> arr = new ArrayList<>();
            // Split on exactly two spaces
            String[] res = str.split("  ");
            int count = res.length;
            if (count == 0)
                return arr;
            arr.add(res[0]);
            int c = 0;
            for (int i = 1; i < count - 1; i++) {
                int index = res[i].indexOf('~');
                if (index == -1) {
                    arr.add(res[i]);
                    continue;
                }
                String s = res[i].substring(index + 1);
                if (s.length() == 2) {
                    String prev = "";
                    String next = "";
                    int indexPrev = res[i - 1].indexOf('~');
                    if (indexPrev != -1) {
                        prev = res[i - 1].substring(indexPrev + 1);
                    }
                    int indexNext = res[i + 1].indexOf('~');
                    if (indexNext != -1) {
                        next = res[i + 1].substring(indexNext + 1);
                    }
                    if (prev.equals(next)) {
                        arr.add(res[i]);
                    } else {
                        arr.add(res[i] + " ==> " + res[i + 1]);
                        i++;
                        c++;
                    }
                } else {
                    arr.add(res[i]);
                }
            }
            if (count > 1)
                arr.add(res[count - 1]);
            arr.add(String.valueOf(c));
            return arr;
        }

        // Computes the fare based on the number of interchanges along the shortest path.
        // The parameter 'x' controls whether the distance or time path is used.
        public int[] Get_Minimum_Fare(String src, String dst, int x) {
            int stations = 0;
            int fare = 0;
            ArrayList<String> interchanges;
            if (x == 7) {
                interchanges = get_Interchanges(Get_Minimum_Distance(src, dst));
            } else {
                interchanges = get_Interchanges(Get_Minimum_Time(src, dst));
            }
            stations = interchanges.size() - 1;
            if (stations > 0 && stations <= 3)
                fare = 10;
            else if (stations > 3 && stations <= 7)
                fare = 20;
            else if (stations > 7 && stations <= 11)
                fare = 30;
            else if (stations > 11 && stations <= 20)
                fare = 40;
            else
                fare = 40 + (stations - 20) * 10;
            return new int[]{stations, fare};
        }

        // Populates the metro map with stations (vertices) and connections (edges)
        public static void Create_Metro_Map(Graph_M g) {
            g.addVertex("Noida_Sector_62~B");
            g.addVertex("Botanical_Garden~B");
            g.addVertex("Yamuna_Bank~B");
            g.addVertex("Rajiv_Chowk~BY");
            g.addVertex("Vaishali~B");
            g.addVertex("Moti_Nagar~B");
            g.addVertex("Janak_Puri_West~BO");
            g.addVertex("Dwarka_Sector_21~B");
            g.addVertex("Huda_City_Center~Y");
            g.addVertex("Saket~Y");
            g.addVertex("Vishwavidyalaya~Y");
            g.addVertex("Chandni_Chowk~Y");
            g.addVertex("New_Delhi~YO");
            g.addVertex("AIIMS~Y");
            g.addVertex("Shivaji_Stadium~O");
            g.addVertex("DDS_Campus~O");
            g.addVertex("IGI_Airport~O");
            g.addVertex("Rajouri_Garden~BP");
            g.addVertex("Netaji_Subhash_Place~PR");
            g.addVertex("Punjabi_Bagh_West~P");

            g.addEdge("Noida_Sector_62~B", "Botanical_Garden~B", 8);
            g.addEdge("Botanical_Garden~B", "Yamuna_Bank~B", 10);
            g.addEdge("Yamuna_Bank~B", "Vaishali~B", 8);
            g.addEdge("Yamuna_Bank~B", "Rajiv_Chowk~BY", 6);
            g.addEdge("Rajiv_Chowk~BY", "Moti_Nagar~B", 9);
            g.addEdge("Moti_Nagar~B", "Janak_Puri_West~BO", 7);
            g.addEdge("Janak_Puri_West~BO", "Dwarka_Sector_21~B", 6);
            g.addEdge("Huda_City_Center~Y", "Saket~Y", 15);
            g.addEdge("Saket~Y", "AIIMS~Y", 6);
            g.addEdge("AIIMS~Y", "Rajiv_Chowk~BY", 7);
            g.addEdge("Rajiv_Chowk~BY", "New_Delhi~YO", 1);
            g.addEdge("New_Delhi~YO", "Chandni_Chowk~Y", 2);
            g.addEdge("Chandni_Chowk~Y", "Vishwavidyalaya~Y", 5);
            g.addEdge("New_Delhi~YO", "Shivaji_Stadium~O", 2);
            g.addEdge("Shivaji_Stadium~O", "DDS_Campus~O", 7);
            g.addEdge("DDS_Campus~O", "IGI_Airport~O", 8);
            g.addEdge("Moti_Nagar~B", "Rajouri_Garden~BP", 2);
            g.addEdge("Punjabi_Bagh_West~P", "Rajouri_Garden~BP", 2);
            g.addEdge("Punjabi_Bagh_West~P", "Netaji_Subhash_Place~PR", 3);
        }
    }

    // Prints a list of stations along with their computed codes
    public static String[] printCodelist() {
        System.out.println("\nList of station along with their codes:");
        int size = Graph_M.vtces.size();
        String[] codes = new String[size];
        int i = 1;
        int m = 1;
        for (String key : Graph_M.vtces.keySet()) {
            String code = "";
            int j = 0;
            while (j < key.length()) {
                // Append any digits
                while (j < key.length() && Character.isDigit(key.charAt(j))) {
                    code += key.charAt(j);
                    j++;
                }
                if (j < key.length() && !Character.isDigit(key.charAt(j)) && key.charAt(j) < 123) {
                    code += key.charAt(j);
                }
                j++;
            }
            if (code.length() < 2 && key.length() > 1) {
                code += Character.toUpperCase(key.charAt(1));
            }
            codes[i - 1] = code;
            System.out.print(i + ". " + key + "\t");
            if (key.length() < (22 - m))
                System.out.print("\t");
            if (key.length() < (14 - m))
                System.out.print("\t");
            if (key.length() < (6 - m))
                System.out.print("\t");
            System.out.println("\t" + code);
            i++;
            if (i == Math.pow(10, m))
                m++;
        }
        return codes;
    }

    // Main method implementing the menu-driven Metro App
    public static void main(String[] args) {
        Graph_M g = new Graph_M();
        Graph_M.Create_Metro_Map(g);

        Scanner sc = new Scanner(System.in);
        System.out.println("\n\n\n\n\t\t\t\t\t\t\t\t\tWELCOME TO THE METRO APP");

        while (true) {
            System.out.println("\n\n\t*********************************************************************************************************************************");
            System.out.println("\t*                                                                                                                               *");
            System.out.println("\t*                                                                                                                               *");
            System.out.println("\t*                                                   ~LIST OF ACTIONS                                                            *");
            System.out.println("\t*         1. LIST ALL THE STATIONS IN THE MAP                                                                                   *");
            System.out.println("\t*         2. SHOW THE METRO MAP                                                                                                 *");
            System.out.println("\t*         3. GET SHORTEST DISTANCE FROM A 'SOURCE' STATION TO 'DESTINATION' STATION                                             *");
            System.out.println("\t*         4. GET SHORTEST TIME TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION                                        *");
            System.out.println("\t*         5. GET SHORTEST PATH (DISTANCE WISE) TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION                        *");
            System.out.println("\t*         6. GET SHORTEST PATH (TIME WISE) TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION                            *");
            System.out.println("\t*         7. GET FARE FOR SHORTEST PATH(DISTANCE WISE)                                                                          *");
            System.out.println("\t*         8. GET FARE FOR SHORTEST PATH(TIME WISE)                                                                              *");
            System.out.println("\t*         9. EXIT THE MENU                                                                                                      *");
            System.out.println("\t*                                                                                                                               *");
            System.out.println("\t*                                                                                                                               *");
            System.out.println("\t*                                                                                                                               *");
            System.out.println("\t*********************************************************************************************************************************\n");
            System.out.print("\nENTER YOUR CHOICE FROM THE ABOVE LIST (1 to 9) :");
            int choice = sc.nextInt();
            sc.nextLine();
            if (choice == 9)
                break;

            switch (choice) {
                case 1:
                    g.display_Stations();
                    break;

                case 2:
                    g.display_Map();
                    break;

                case 3: {
                    String[] keys = printCodelist();
                    System.out.print("\nENTER THE SOURCE STATION: ");
                    String st1 = sc.nextLine();
                    System.out.print("ENTER THE DESTINATION STATION: ");
                    String st2 = sc.nextLine();

                    HashMap<String, Boolean> processed = new HashMap<>();
                    if (!g.containsVertex(st1) || !g.containsVertex(st2) || !g.hasPath(st1, st2, processed))
                        System.out.println("\nTHE INPUTS ARE INVALID");
                    else
                        System.out.println("\nSHORTEST DISTANCE: " + g.dijkstra(st1, st2, false) + " KM");
                    break;
                }

                case 4: {
                    String[] keys = printCodelist();
                    System.out.print("\nENTER THE SOURCE STATION: ");
                    String sat1 = sc.nextLine();
                    System.out.print("ENTER THE DESTINATION STATION: ");
                    String sat2 = sc.nextLine();

                    HashMap<String, Boolean> processed1 = new HashMap<>();
                    int time = g.dijkstra(sat1, sat2, true);
                    System.out.println("\nSHORTEST TIME: " + (time / 60) + " MINUTES");
                    break;
                }

                case 5: {
                    String[] keys = printCodelist();
                    System.out.print("\nENTER THE SOURCE STATION: ");
                    String s1 = sc.nextLine();
                    System.out.print("ENTER THE DESTINATION STATION: ");
                    String s2 = sc.nextLine();

                    HashMap<String, Boolean> processed2 = new HashMap<>();
                    if (!g.containsVertex(s1) || !g.containsVertex(s2) || !g.hasPath(s1, s2, processed2))
                        System.out.println("THE INPUTS ARE INVALID");
                    else {
                        ArrayList<String> str = g.get_Interchanges(g.Get_Minimum_Distance(s1, s2));
                        int len = str.size();
                        System.out.println("\n\nSOURCE STATION : " + s1);
                        System.out.println("DESTINATION STATION : " + s2);
                        System.out.println("DISTANCE : " + str.get(len - 2));
                        System.out.println("NUMBER OF INTERCHANGES : " + str.get(len - 1));
                        System.out.println("\n\nTHE PATH IS AS FOLLOWS:\n");
                        for (int i = 0; i < len - 2; i++) {
                            System.out.println(str.get(i));
                        }
                    }
                    break;
                }

                case 6: {
                    String[] keys = printCodelist();
                    System.out.print("\nENTER THE SOURCE STATION: ");
                    String ss1 = sc.nextLine();
                    System.out.print("ENTER THE DESTINATION STATION: ");
                    String ss2 = sc.nextLine();

                    HashMap<String, Boolean> processed3 = new HashMap<>();
                    if (!g.containsVertex(ss1) || !g.containsVertex(ss2) || !g.hasPath(ss1, ss2, processed3))
                        System.out.println("THE INPUTS ARE INVALID");
                    else {
                        ArrayList<String> str = g.get_Interchanges(g.Get_Minimum_Time(ss1, ss2));
                        int len = str.size();
                        System.out.println("\n\nSOURCE STATION : " + ss1);
                        System.out.println("DESTINATION STATION : " + ss2);
                        System.out.println("TIME : " + str.get(len - 2) + " MINUTES");
                        System.out.println("NUMBER OF INTERCHANGES : " + str.get(len - 1));
                        System.out.println("\n\nTHE PATH IS AS FOLLOWS:\n");
                        for (int i = 0; i < len - 2; i++) {
                            System.out.println(str.get(i));
                        }
                        System.out.println();
                    }
                    break;
                }

                case 7: {
                    String[] keys = printCodelist();
                    System.out.print("\nENTER THE SOURCE STATION: ");
                    String ss1 = sc.nextLine();
                    System.out.print("ENTER THE DESTINATION STATION: ");
                    String ss2 = sc.nextLine();

                    HashMap<String, Boolean> processed3 = new HashMap<>();
                    if (!g.containsVertex(ss1) || !g.containsVertex(ss2) || !g.hasPath(ss1, ss2, processed3))
                        System.out.println("THE INPUTS ARE INVALID");
                    else {
                        int[] fareData = g.Get_Minimum_Fare(ss1, ss2, 7);
                        System.out.println("\nNUMBER OF STATIONS IN SHORTEST DISTANCE :" + fareData[0]);
                        System.out.println("CALCULATED FARE : Rs" + fareData[1]);
                    }
                    break;
                }

                case 8: {
                    String[] keys = printCodelist();
                    System.out.print("\nENTER THE SOURCE STATION: ");
                    String ss1 = sc.nextLine();
                    System.out.print("ENTER THE DESTINATION STATION: ");
                    String ss2 = sc.nextLine();

                    HashMap<String, Boolean> processed3 = new HashMap<>();
                    if (!g.containsVertex(ss1) || !g.containsVertex(ss2) || !g.hasPath(ss1, ss2, processed3))
                        System.out.println("THE INPUTS ARE INVALID");
                    else {
                        int[] fareData = g.Get_Minimum_Fare(ss1, ss2, 8);
                        System.out.println("\nNUMBER OF STATIONS IN SHORTEST TIME :" + fareData[0]);
                        // For time-based fare, the original C++ code used the result of Get_Minimum_Fare with x==7.
                        int[] fareDataTime = g.Get_Minimum_Fare(ss1, ss2, 7);
                        System.out.println("CALCULATED FARE : Rs" + fareDataTime[1]);
                    }
                    break;
                }

                default:
                    System.out.println("Invalid choice");
            }
        }
        sc.close();
    }
}
