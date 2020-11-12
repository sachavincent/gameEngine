package entities.people;

public enum SocialClass {
    FARMER;

    public static int getNbClasses() {
        return values().length;
    }
}
