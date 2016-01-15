package spiel;

/**
 * Created by aaronzingerle on 15.01.16.
 */
public class Territory
{
    private String name;
    private int[] capital;
    private Territory[] neighbors;
    private int[][] patches;

    public Territory(String name, int capitalX, int capitalY, int[][] patches)
    {
        capital         = new int[2];
        capital[0]      = capitalX;
        capital[1]      = capitalY;

        this.name       = name;
        this.patches    = patches;
    }

    public void setNeighbors(Territory[] neighbors)
    {
        this.neighbors = neighbors;
    }

    public Territory[] getNeighbors()
    {
        return neighbors;
    }

    public String getName()
    {
        return name;
    }
}
