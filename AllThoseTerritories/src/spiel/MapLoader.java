package spiel;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
/**
 * Created by aaronzingerle on 14.01.16.
 */

// TODO: implementing getter methods

public class MapLoader
{
    private String dateiPfad;
    private LinkedList<String> lines = new LinkedList<String>();
    private String[] linesArr;
    private String[][] cuttetLinesArr;

    private HashMap<String, ArrayList<int[]>> patches   = new HashMap<String, ArrayList<int[]>>();
    private HashMap<String, int[]> capitals             = new HashMap<String, int[]>();
    private HashMap<String, String[]> neighbors         = new HashMap<String, String[]>();
    private HashMap<String, String[]> continents        = new HashMap<String, String[]>();
    private HashMap<String, Integer> continentsBonus    = new HashMap<String, Integer>();

    public MapLoader(String dateiPfad)
    {
        this.dateiPfad = dateiPfad;
    }

    public void execute()
    {
        fileAuslesen(dateiPfad);
        cut();
        loadPatches();
        loadCapitals();
        loadNeighbors();
        loadContinents();
    }

    private void cut()   // zerschneidet die eingelesenen Zeilen an Leerzeichen
    {
        cuttetLinesArr = new String[linesArr.length][];
        int i=0;
        for (String line:linesArr)
        {
            String[] unterArr = linesArr[i].split(" ");
            cuttetLinesArr[i] = unterArr;
            i++;
        }


        /*      // for testing
        for (String s: cuttetLinesArr[150])
        {
            System.out.println(s);
        }*/
    }

    private void loadPatches()       // filtert die Patches, aus den eingelesenen Zeilen
    {
        for (int i=0; i<cuttetLinesArr.length; i++)
        {
            String[] current = cuttetLinesArr[i];
            if (current[0].equals("patch-of"))
            {
                String patchName = current[1];
                int j=2;

                for (j=j;  j<current.length;j++)    // lese die Teile des Namens des Patches aus
                {
                    if (!isInteger(current[j]))
                    {
                        patchName += (" " + current[j]);
                    }
                    else break;
                }

                int[] coordinates = new int[current.length-j+1];
                int offset = j;

                for (j=j; j<current.length; j++)    // lese die einzelnen Koordianten eins Patches aus
                {
                    coordinates[j-offset] = Integer.parseInt(current[j]);
                }

                if (patches.containsKey(patchName))
                {
                    patches.get(patchName).add(coordinates);
                }
                else
                {
                    ArrayList<int[]> coordinatesList = new ArrayList<int[]>();
                    coordinatesList.add(coordinates);
                    patches.put(patchName, coordinatesList);
                }
            }
        }

        /*  // for testing
        ArrayList<int[]> test = patches.get("Central America");
        for (int[] a: test)
        {
            String s = "-->";
            for (int x: a){
                s += "."+x+".";
            }
            s += "; ";
            System.out.println(s);
        }*/
    }

    private void loadCapitals()      // filtert die Hauptstaedte der Patches, aus den eingelesenen Zeilen
    {
        for (int i=0; i<cuttetLinesArr.length; i++)
        {
            String[] current = cuttetLinesArr[i];
            if (current[0].equals("capital-of"))
            {
                int[] capitalCoordinates = new int[2];
                String patchName = current[1];
                int j=2;

                for (j=j;  j<current.length;j++)    // lese die Teile des Namens des Patches aus
                {
                    if (!isInteger(current[j]))
                    {
                        patchName += (" " + current[j]);
                    }
                    else break;
                }
                capitalCoordinates[0] = Integer.parseInt(current[j]);
                capitalCoordinates[1] = Integer.parseInt(current[j+1]);

                capitals.put(patchName, capitalCoordinates);
            }
        }

        /*  // for testing
        for (String s : capitals.keySet())
        {
            int[] c = capitals.get(s);
            System.out.println("Capital of: " + s + " : " + c[0]+ "," + c[1]);
        }
        */
    }

    private void loadNeighbors()     // filtert die Nachbarn der Patches, aus den eingelesenen Zeilen
    {
        String neighbor = "";
        ArrayList<String> neighborsGroup = new ArrayList<String>();

        for (int i=0; i<cuttetLinesArr.length; i++)
        {
            String[] current = cuttetLinesArr[i];
            if (current[0].equals("neighbors-of"))
            {
                String patchName = current[1];
                int j=2;

                for (j=j;  j<current.length;j++)
                {
                    if (!current[j].equals(":"))
                    {
                        patchName += (" " + current[j]);
                    }
                    else break;
                }
                j++;
                for (j=j; j<current.length;j++)     // die einzelnen Nachbarn eines Patches lesen
                {
                    if (!current[j].equals("-"))
                    {
                        if (neighbor.equals(""))
                        {
                            neighbor += current[j];
                        }
                        else
                        {
                            neighbor += (" " + current[j]);
                        }
                    }
                    else    // ist String ein '-' dann ist der N.Name fertig und fuege es zur Nachbargruppe
                    {
                        neighborsGroup.add(neighbor);
                        neighbor = "";
                    }
                    if (j == current.length-1)      // gleiches Verhalten am erreichen des Ende des Arrays, wie bei '-'
                    {
                        neighborsGroup.add(neighbor);
                        neighbor = "";
                    }
                }
                String[] neighborsGroupArr = new String[neighborsGroup.size()];
                neighborsGroupArr = neighborsGroup.toArray(neighborsGroupArr);

                neighbors.put(patchName, neighborsGroupArr);
                neighborsGroup = new ArrayList<String>();
            }
        }

        /* // for testing
        for (String k: neighbors.keySet())
        {
            String[] test = neighbors.get(k);
            String o = k + "..";
            for (String s:test)
            {
                o += ", " + s;
            }
            System.out.println(o);
        }
        */
    }

    private void loadContinents()    // filtert die Kontinente, aus den eingelesenen Zeilen
    {
        String patch = "";
        ArrayList<String> patchesGroup = new ArrayList<String>();

        for (int i=0; i<cuttetLinesArr.length; i++)
        {
            String[] current = cuttetLinesArr[i];
            if (current[0].equals("continent"))
            {
                Integer bonus;
                String continentName = current[1];
                int j = 2;

                for (j = j; j < current.length; j++)
                {
                    if (!isInteger(current[j]))
                    {
                        continentName += (" " + current[j]);
                    } else break;
                }
                bonus = Integer.parseInt(current[j]);
                j += 2;

                for (j=j; j<current.length;j++)     // die einzelnen Nachbarn eines Patches lesen
                {
                    if (!current[j].equals("-"))
                    {
                        if (patch.equals(""))
                        {
                            patch += current[j];
                        }
                        else
                        {
                            patch += (" " + current[j]);
                        }
                    }
                    else    // ist String ein '-' dann ist der Patch Name fertig und fuege es zur Nachbargruppe
                    {
                        patchesGroup.add(patch);
                        patch = "";
                    }
                    if (j == current.length-1)      // gleiches Verhalten am erreichen des Ende des Arrays, wie bei '-'
                    {
                        patchesGroup.add(patch);
                        patch = "";
                    }
                }
                String[] patchesGroupArr = new String[patchesGroup.size()];
                patchesGroupArr = patchesGroup.toArray(patchesGroupArr);

                continents.put(continentName, patchesGroupArr);
                continentsBonus.put(continentName, bonus);

                patchesGroup = new ArrayList<String>();
            }
        }

        /*      // for testing
        for (String k: continents.keySet())
        {
            String[] test = continents.get(k);
            String o = k + "..";
            for (String s:test)
            {
                o += ", " + s;
            }
            System.out.println(o);
        }
        for (String k: continentsBonus.keySet())
        {
            int i = continentsBonus.get(k);
            System.out.println(k + " : " + i);
        }
        */
    }

    private static boolean isInteger(String str)     // kontrolliert ob der String ein int darstellt
    {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }
    }

    private void fileAuslesen(String pfad)      // liest die Datei der uebergebenen url aus
    {
        try
        {
            FileReader ausleser         = new FileReader(pfad);
            BufferedReader buffAusleser = new BufferedReader(ausleser);
            String helfer;
            while((helfer = buffAusleser.readLine()) != null)   // lese Datei zeilenweise aus
            {
                lines.add(helfer);
            }
            buffAusleser.close();
            ausleser.close();

            linesArr = new String[lines.size()];
            linesArr = lines.toArray(linesArr);     // erzeuge aus der Zeilen-Liste ein Zeilen-Array
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

    public void inhaltAusgeben()       // gibt die eingelesenen Zeilen aus
    {
        int i=0;
        String[] arr = new String[lines.size()];
        for (String s : lines.toArray(arr))
        {
            System.out.println(i + ": " + s);
            i++;
        }
    }

    public Territory[] getTerritories()     // gibt ein Array mit allen Territorien zurueck
    {
        Territory[] territories = new Territory[patches.size()];
        int i=0;
        for (String patch: patches.keySet())
        {
            ArrayList<int[]> land = patches.get(patch);
            int[][] landArr = new int[land.size()][];
            landArr = land.toArray(landArr);
            int[] capital = capitals.get(patch);

            Territory t = new Territory(patch, capital[0], capital[1], landArr);
            territories[i] = t;
            i++;
        }
        for (i=0; i<territories.length; i++)    // finde die Nachbarn, die es selber schon besitzt
        {
            String territoryName        = territories[i].getName();
            String[] neighborsNames     = neighbors.get(territoryName);

            if (neighborsNames == null) {continue;}     // falls Territorium selber keine Nachbarn besitzt

            Territory[] thisNeighbors   = new Territory[neighborsNames.length];
            int neighborsIndex          = 0;

            for (String n: neighborsNames)
            {
                for (int j=0; j<territories.length; j++)
                {
                    String thatName = territories[j].getName();

                    if (thatName.equals(n))
                    {
                        thisNeighbors[neighborsIndex] = territories[j];
                        neighborsIndex++;
                    }
                }
            }

            territories[i].setNeighbors(thisNeighbors);
        }
        for (i=0; i<territories.length; i++)    // finde die Nachbarn, wo das Territorium auf der rechten Seite des ':' steht
        {
            Territory thisTerritory         = territories[i];
            String territoryName            = thisTerritory.getName();
            Territory[] alreadyNeighbors    = thisTerritory.getNeighbors();
            String[] alreadyNeighborsNames  = null;
            ArrayList<String> newNeighbors  = new ArrayList<String>();

            boolean hasAlready=true;
            if (alreadyNeighbors == null) { hasAlready = false; }

            if (hasAlready)
            {
                alreadyNeighborsNames = new String[alreadyNeighbors.length];
                for (int k=0; k<alreadyNeighbors.length; k++)
                {
                    alreadyNeighborsNames[k] = alreadyNeighbors[k].getName();
                }
            }

            for (String t: neighbors.keySet())
            {
                String[] ts = neighbors.get(t);
                if (Arrays.asList(ts).contains(territoryName))
                {
                    if (hasAlready)
                    {
                        if (Arrays.asList(alreadyNeighborsNames).contains(t)){}
                        else
                        {
                            newNeighbors.add(t);
                        }
                    }
                    else
                    {
                        newNeighbors.add(t);
                    }
                }
            }

            if (newNeighbors.size() > 0)
            {
                String[] newNeighborsArr = new String[newNeighbors.size()];
                newNeighborsArr = newNeighbors.toArray(newNeighborsArr);

                Territory[] allNeighbors;
                if (hasAlready)
                {
                    allNeighbors = new Territory[newNeighborsArr.length + alreadyNeighbors.length];
                    for (int k=0; k<alreadyNeighbors.length; k++)
                    {
                        allNeighbors[k] = alreadyNeighbors[k];
                    }
                }
                else
                {
                    allNeighbors = new Territory[newNeighborsArr.length];
                }
                for (int k=0; k<newNeighborsArr.length; k++)    // hole die Territory Objekte zu den entsprechenden Namen
                {
                    for (Territory t: territories)
                    {
                        if (t.getName().equals(newNeighborsArr[k]))
                        {
                            if (hasAlready)
                            {
                                allNeighbors[k + alreadyNeighbors.length] = t;
                            }
                            else
                            {
                                allNeighbors[k] = t;
                            }
                        }
                    }

                }
                thisTerritory.setNeighbors(allNeighbors);
            }
        }
        return territories;
    }

    public Continent[] getContinents(Territory[] terr)      // gibt ein Array mit allen Kontinenten zuruck
    {
        Territory[] territories     = terr;     //  Pointerstruktur aufrecht erhalten
        Continent[] continentsArr   = new Continent[continents.size()];
        int continentsArrIndex      = 0;

        for (String continent: continents.keySet())
        {
            int bonus                       = continentsBonus.get(continent);
            String[] territoriesNames       = continents.get(continent);
            Territory[] partsOfContinent    = new Territory[territoriesNames.length];
            int partsOfContinentIndex       = 0;

            for (int i=0; i<territoriesNames.length; i++)
            {
                for (int k=0; k<territories.length; k++)
                {
                    if (territoriesNames[i].equals(territories[k].getName()))
                    {
                        partsOfContinent[partsOfContinentIndex] = territories[k];
                        partsOfContinentIndex++;
                    }
                }
            }

            Continent c = new Continent(continent, bonus);
            c.setTerritories(partsOfContinent);

            continentsArr[continentsArrIndex] = c;
            continentsArrIndex++;
        }

        return continentsArr;
    }

    public static boolean checkIfFileExists(String path)
    {
        File f = new File(path);
        if(f.exists() && !f.isDirectory())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}







