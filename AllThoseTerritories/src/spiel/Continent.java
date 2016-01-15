package spiel;

/**
 * Created by aaronzingerle on 15.01.16.
 */
public class Continent
{
    private String name;
    private int bonus;
    private Territory[] territories;

    public Continent(String name, int bonus)
    {
        this.name = name;
        this.bonus = bonus;
    }

    public void setTerritories(Territory[] territories)
    {
        this.territories = territories;
    }
}
