public class CompteurCycle {
    private int count;
    CompteurCycle()
    {
        this.count=1;
    }
    public void countPlusPlus()
    {
        this.count++;
    }
    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return ""+count;
    }
}
