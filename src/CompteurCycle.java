public class CompteurCycle {
    private int count;
    private int countUpgrade;

    public int getCountUpgrade() {
        return countUpgrade;
    }

    CompteurCycle()
    {
        this.count=1;
        this.countUpgrade=1;
    }
    public void countPlusPlus()
    {
        this.count++;
    }
    public void countPlusPlusUpgrade(){this.countUpgrade++;}
    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return ""+count;
    }
}
