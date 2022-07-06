public class Lemma implements Comparable<Lemma> {

    private String name;
    private double frequency;

    public Lemma(String name, double frequency) {
        this.name = name;
        this.frequency = frequency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    @Override
    public int compareTo(Lemma lemma) {
        return (int) (this.frequency - lemma.getFrequency());
    }
}
