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

    public String getName()                 { return name; }
    public int getBonus()                   { return bonus; }
    public Territory[] getTerritories()     { return territories; }
    public int Bonus(int User){
        boolean allbelong = true;
        for(Territory t:territories){
            if(User != t.getOwnership())
                return 0;
        }
        return bonus;
    }


}
