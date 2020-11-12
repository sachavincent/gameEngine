package guis.presets.graphs;

import com.sun.istack.internal.NotNull;
import guis.GuiInterface;
import guis.basics.GuiEllipse;
import guis.constraints.GuiConstraintsManager;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import util.math.Vector2f;

public class GuiDonutGraph<ValueType> extends GuiGraph {

    private GuiEllipse outerCircle;
    private GuiEllipse innerCircle;

    private final Set<Sector<ValueType>> sectors;

    public GuiDonutGraph(GuiInterface parent,
            GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        this.sectors = new LinkedHashSet<>();
    }

    public void addSector(Sector<ValueType> sector) {
        this.sectors.add(sector);
    }

    public Set<Sector<ValueType>> getSectors() {
        return this.sectors;
    }

    public GuiEllipse getInnerCircle() {
        return this.innerCircle;
    }

    public GuiEllipse getOuterCircle() {
        return this.outerCircle;
    }

    public void setInnerCircle(@NotNull GuiEllipse innerCircle) {
        this.innerCircle = innerCircle;
        this.innerCircle.setOutlineWidth(1);

        addBasic(this.innerCircle);
    }

    public void setOuterCircle(@NotNull GuiEllipse outerCircle) {
        this.outerCircle = outerCircle;
        this.outerCircle.setOutlineWidth(1);

        addBasic(this.outerCircle);
    }

    /**
     * Get points on the outer circle where lines are drawn to separate Sectors
     */
    public List<Vector2f> getRenderPoints() {
        List<Vector2f> points = new LinkedList<>();
        if (this.sectors.isEmpty() || this.sectors.size() == 1)
            return points;

        float width = this.innerCircle.getFinalWidth() / this.outerCircle.getFinalWidth();
        Vector2f baseLine = new Vector2f(0, width);
        points.add(baseLine);
        Vector2f prevLine = baseLine;
        double total = total();

        int lastSector = this.sectors.size();
        int i = 1;
        for (Sector<?> sector : this.sectors) {
//            if (i++ == lastSector)
//                break;

            double alpha = 2 * Math.PI * sector.getValue() / total;
            double x = BigDecimal.valueOf(prevLine.x * Math.cos(alpha) + prevLine.y * Math.sin(alpha)).setScale(3, RoundingMode.HALF_UP).doubleValue();
            double y = BigDecimal.valueOf(-prevLine.x * Math.sin(alpha) + prevLine.y * Math.cos(alpha)).setScale(3, RoundingMode.HALF_UP).doubleValue();
            points.add(prevLine = new Vector2f(x, y));
        }

//        System.out.println(points);

        return points;
    }

    /**
     * Get colors for rendering
     */
    public Set<Color> getRenderColors() {
        return this.sectors.stream().map(Sector::getColor).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public double total() {
        return this.sectors.stream().mapToDouble(Sector::getValue).sum();
    }

    public static class Sector<ValueType> {

        private final double value;
        private final Color  color;
        private final String description;

        // Optional
        private ValueType shownValue;

        public Sector(double value, ValueType shownValue, Color color, String description) {
            this.shownValue = shownValue;
            this.color = color;
            this.description = description;
            this.value = value;
        }

        public Sector(double value, Color color, String description) {
            this.value = value;
            this.color = color;
            this.description = description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Sector<?> sector = (Sector<?>) o;
            return Objects.equals(color, sector.color);
        }

        @Override
        public int hashCode() {
            return Objects.hash(color);
        }

        public double getValue() {
            return this.value;
        }

        public ValueType getShownValue() {
            return this.shownValue;
        }

        public Color getColor() {
            return this.color;
        }

        public String getDescription() {
            return this.description;
        }
    }
}
