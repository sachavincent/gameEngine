package engineTester;

import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import people.Farmer;
import people.Person;
import scene.Scene;
import scene.components.Component;
import scene.components.ConstructionComponentSingle;
import scene.components.ConstructionComponentSingle.ConstructionTier;
import scene.components.ResidenceComponent;
import scene.components.requirements.ResourceRequirementComponent;
import scene.gameObjects.GameObject;
import util.TimeSystem;
import util.Utils;

public class Game {

    public static final TimeSystem COOLDOWN_BEFORE_SETTLEMENT      = new TimeSystem(
            TimeSystem.TICK_RATE * 60); // 1 Minute
    public static final TimeSystem MAX_TIME_BEFORE_FULL_SETTLEMENT = new TimeSystem(
            TimeSystem.TICK_RATE * 60 * 5); // 5 Minutes
    public static final TimeSystem TIME_BETWEEN_SETTLEMENTS        = new TimeSystem(
            TimeSystem.TICK_RATE * 20); // 20 seconds
    public static final int        COOLDOWN_GLOBAL_MOVE_AWAY       = TimeSystem.TICK_RATE * 10; // 10 seconds

    public static final int   TERRAIN_WIDTH      = 128;
    public static final int   TERRAIN_DEPTH      = 128;
    public static final float TERRAIN_MAX_HEIGHT = 32;

    private final Scene scene;

    private GameState gameState;

    private int lastSettlementAttemptTime = TimeSystem.getCurrentTimeTicks();
    private int lastGlobalMovingAwayTime  = TimeSystem.getCurrentTimeTicks();

    public Game() {
        this.gameState = GameState.DEFAULT;

        this.scene = new Scene();
    }

    public Scene getScene() {
        return this.scene;
    }

    public boolean addPerson() {
        Map<Integer, Integer> eligibleHouses = getEligibleHousesSettlement();

        if (eligibleHouses.entrySet().isEmpty())
            return false;

        GameObject house = Rome.getGame().getScene().getGameObjectFromId(Utils.pickRandomWeightedMap(eligibleHouses));
        if (house == null || !house.hasComponent(ResidenceComponent.class))
            return false;

//        System.out.println("Adding person");
        boolean res = house.getComponent(ResidenceComponent.class).addPerson(new Farmer());
        GuiHouseDetails.getInstance().update();

        return res;
    }

    public boolean removePerson() {
        int currentTime = TimeSystem.getCurrentTimeTicks();
        if (currentTime - this.lastGlobalMovingAwayTime < COOLDOWN_GLOBAL_MOVE_AWAY)
            return false;

        Map<Person, Integer> eligiblePersonsForMovingAway = getEligiblePersonsForMovingAway();

        if (eligiblePersonsForMovingAway.entrySet().isEmpty())
            return false;

        Person selectedPerson = Utils.pickRandomWeightedMap(eligiblePersonsForMovingAway);
        GameObject house = Rome.getGame().getScene().getGameObjectFromId(selectedPerson.getIdHouse());
        if (house == null || !house.hasComponent(ResidenceComponent.class))
            return false;

//        System.out.println("Removing person");
        boolean res = house.getComponent(ResidenceComponent.class).removePerson(selectedPerson);
        GuiHouseDetails.getInstance().update();
        return res;
    }

    /**
     * Different factors:
     * Time since place available
     * Place available
     * TODO: WIP: Houses levels should be considered
     * Tier reached
     * More? TODO
     *
     * @return the list of all eligible houses with their probability
     */
    private Map<Integer, Integer> getEligibleHousesSettlement() {
        return Rome.getGame().getScene().getGameObjectsForComponent(ResourceRequirementComponent.class).stream()
                .collect(Collectors.toMap(gameObject -> gameObject, gameObject -> {
                    ResidenceComponent residenceComponent = gameObject.getComponent(ResidenceComponent.class);
                    if (residenceComponent == null ||
                            residenceComponent.getCurrentPeopleCount() == residenceComponent.getMaxPeopleCapacity())
                        return 0;

                    double probability;

                    int nbTicksSincePlaceAvailable = 0;
                    // Time since built
                    if (gameObject.hasComponent(ConstructionComponentSingle.class)) {
                        ConstructionComponentSingle cstrtionComp = gameObject
                                .getComponent(ConstructionComponentSingle.class);
                        if (cstrtionComp.isFinishedBuilding()) {
                            ConstructionTier currentConstructionTier = cstrtionComp.getCurrentConstructionTier();
                            if (currentConstructionTier != null) {
                                int nbTicksSinceConstruction =
                                        currentConstructionTier.getEndTime().getNbTicks() -
                                                TimeSystem.getCurrentTimeTicks();
                                if (nbTicksSinceConstruction < COOLDOWN_BEFORE_SETTLEMENT.getNbTicks())
                                    return 0;

                                nbTicksSincePlaceAvailable = nbTicksSinceConstruction;
                            }
                        }
                    }

                    nbTicksSincePlaceAvailable = Math
                            .max(nbTicksSincePlaceAvailable, residenceComponent.getLastLocalMoveAwayTime());
                    if (nbTicksSincePlaceAvailable < COOLDOWN_BEFORE_SETTLEMENT.getNbTicks())
                        probability = 1;
                    else {
                        double nbTicks = MAX_TIME_BEFORE_FULL_SETTLEMENT.getNbTicks() -
                                COOLDOWN_BEFORE_SETTLEMENT.getNbTicks();
                        probability =
                                Math.min(nbTicks,
                                        nbTicksSincePlaceAvailable - COOLDOWN_BEFORE_SETTLEMENT.getNbTicks()) /
                                        nbTicks; // Linear probability
                    }
                    // Place available
                    int placesAvailable =
                            residenceComponent.getMaxPeopleCapacity() - residenceComponent.getCurrentPeopleCount();
                    probability = probability *
                            ((double) placesAvailable / (double) residenceComponent.getMaxPeopleCapacity());

                    // Needs Tier reached
                    if (gameObject.hasComponent(ResourceRequirementComponent.class)) {
                        ResourceRequirementComponent resourceRequirementComponent = gameObject.getComponent(
                                ResourceRequirementComponent.class);
                        double weight;
                        if (!resourceRequirementComponent.areTier1RequirementsMet())
                            weight = 0;
                        else if (!resourceRequirementComponent.areTier2RequirementsMet())
                            weight = 0.33;
                        else if (!resourceRequirementComponent.areTier3RequirementsMet())
                            weight = 0.66;
                        else
                            weight = 1;

                        probability *= weight;
                    }

                    return (int) (probability * 100);
                })).entrySet().stream().filter(entry -> entry.getValue() > 0)
                .collect(Collectors
                        .toMap(entry -> entry.getKey().getId(), Entry::getValue, (u, v) -> u, LinkedHashMap::new));
    }

    /**
     * Different factors:
     * See Trello: https://trello.com/c/10ZX68ic
     * One person per eligible house is selected
     *
     * @return the list of all eligible persons with their probability
     */
    private Map<Person, Integer> getEligiblePersonsForMovingAway() {
        return Rome.getGame().getScene().getGameObjectsForComponent(ResidenceComponent.class).stream()
                .map(gameObject -> {
                    ResidenceComponent residenceComponent = gameObject.getComponent(ResidenceComponent.class);
                    int currentPeopleCount = residenceComponent.getCurrentPeopleCount();
                    if (currentPeopleCount == 0)
                        return null;
                    if (TimeSystem.getCurrentTimeTicks() - residenceComponent.getLastLocalMoveAwayTime() <=
                            ResidenceComponent.COOLDOWN_LOCAL_MOVE_AWAY)
                        return null;

                    // Places taken
                    double probability = (
                            (double) (residenceComponent.getMaxPeopleCapacity() + 1 - currentPeopleCount) /
                                    (double) residenceComponent.getMaxPeopleCapacity() + 1);

                    int cooldown = ResidenceComponent.MIN_TICKS_BETWEEN_SETTLEMENT_AND_MOVING_AWAY_TIER_2;
                    // Needs Tier reached
                    if (gameObject.hasComponent(ResourceRequirementComponent.class)) {
                        ResourceRequirementComponent resourceRequirementComponent = gameObject.getComponent(
                                ResourceRequirementComponent.class);
                        if (resourceRequirementComponent.areTier3RequirementsMet() ||
                                resourceRequirementComponent.areTier2RequirementsMet())
                            return null;

                        if (!resourceRequirementComponent.areTier1RequirementsMet())
                            cooldown = ResidenceComponent.MIN_TICKS_BETWEEN_SETTLEMENT_AND_MOVING_AWAY_TIER_1;
                    }

                    final int finalCooldown = cooldown;
                    Person eligiblePerson = residenceComponent.getPersons().values().stream().map(
                                    people -> people.stream()
                                            .filter(person -> TimeSystem.getCurrentTimeTicks() - person.getSettlementTime() >
                                                    finalCooldown)
                                            .collect(Collectors.toList())).flatMap(Collection::stream)
                            .findFirst().orElse(null);
                    return new SimpleEntry<>(eligiblePerson, (int) (probability * 100));
                })
                .filter(Objects::nonNull)
                .filter(entry -> entry.getKey() != null)
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

    public void pause() {
        if (this.gameState == GameState.DEFAULT)
            this.gameState = GameState.PAUSED;
        else
            throw new IllegalStateException("Incorrect GameState=" + this.gameState.name());
    }

    public void resume() {
        if (this.gameState == GameState.PAUSED)
            this.gameState = GameState.DEFAULT;
        else
            throw new IllegalStateException("Incorrect GameState=" + this.gameState.name());
    }

    public GameState getGameState() {
        return this.gameState;
    }

    /**
     * Called every in-game Tick
     */
    protected void processLogic() {
        if (this.gameState == GameState.DEFAULT) {
            TimeSystem.nextTick();
//            EnumMap<SocialClass, Integer> peopleList = new EnumMap<>(SocialClass.class);
//
//            Rome.getGame().getScene()
//                    .getGameObjectsForComponent(ResidenceComponent.class, false).stream()
//                    .map(gameObject -> gameObject.getComponent(ResidenceComponent.class).getPersons())
//                    .forEach(map -> {
//                        map.forEach((socialClass, people) -> {
//                            if (!peopleList.containsKey(socialClass))
//                                peopleList.put(socialClass, 0);
//
//                            peopleList.put(socialClass, peopleList.get(socialClass) + people.size());
//                        });
//                    }); //TODO: Save this list
//            peopleList.forEach((socialClass, nb) -> {
//                socialClass.getPersonalResourceInfos().forEach(personalResourceInfos -> {
//                    double depletion = personalResourceInfos.getDepletionRate() * nb;
//                    ResourceManager.removeFromResource(personalResourceInfos.getResource(), depletion);
//                });
//            });

//            Rome.getGame().getScene().getGameObjectsForComponent(ProductionComponent.class, false).forEach(gameObject -> {
//                Map<Resource, Double> productionRates = gameObject.getComponent(ProductionComponent.class)
//                        .getProductionRates();
//                productionRates.forEach(ResourceManager::addToResource);
//            });

            Rome.getGame().getScene().getGameObjects()
                    .forEach(gameObject -> gameObject.getComponents().values().forEach(Component::tick));

//            NPC.updatePositions();
            Rome.getGame().getScene().updateHighlightedPaths();
            int nbTicksForOneHouse = Game.TIME_BETWEEN_SETTLEMENTS.getNbTicks();
            Scene instance = Rome.getGame().getScene();
            int nbHouses = instance.getIdGameObjectsForComponentClass(ResidenceComponent.class).size();
            //TODO: Update this number when house is added/removed, not every tick
            //TODO: Also, this should be nbEligibleHouses not nbHouses
            if (nbHouses > 0) {
                int currentTime = TimeSystem.getCurrentTimeTicks();
                int nbTicks = currentTime - this.lastSettlementAttemptTime;
                int i = nbTicksForOneHouse / nbHouses;
                if (nbTicks > i) {
                    addPerson();
                    this.lastSettlementAttemptTime = currentTime;
                }

                if (removePerson())
                    this.lastGlobalMovingAwayTime = currentTime;
            }
        }
    }

    public enum GameState {
        DEFAULT,
        PAUSED
    }
}
