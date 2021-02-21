package guis.presets.graphs;

import static guis.Gui.DEFAULT_FONT;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static renderEngine.GuiRenderer.DONUT;
import static util.math.Vector2f.cross_product;
import static util.math.Vector2f.dot;

import com.sun.istack.internal.NotNull;
import fontMeshCreator.Text;
import guis.Gui;
import guis.GuiInterface;
import guis.basics.GuiEllipse;
import guis.basics.GuiText;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;
import inputs.MouseUtils;
import java.awt.Color;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.DisplayManager;
import renderEngine.GuiRenderer;
import util.math.Maths;
import util.math.Vector2f;

public class GuiDonutGraph<ValueType> extends GuiGraph {

    private final Set<Sector<ValueType>> sectors;

    private final List<Vector2f> renderPoints;

    private GuiEllipse outerCircle;
    private GuiEllipse innerCircle;

    // Hovering stuff
    private final Gui       hoverSectorGui;
    private       Sector<?> lastHoveredSector;
    private       GuiText   currentGuiHoverText;

    public GuiDonutGraph(GuiInterface parent,
            GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        this.type = DONUT;

        this.sectors = new LinkedHashSet<>();
        this.renderPoints = new LinkedList<>();

        hoverSectorGui = new Gui(new Background<>(new Color(64, 64, 64, 100)));
        GuiConstraintsManager guiConstraintsManager = new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.05f))
                .setHeightConstraint(new PixelConstraint(30))
                .create();

        hoverSectorGui.setConstraints(guiConstraintsManager);
        hoverSectorGui.setDisplayed(false);

        hoverSectorGui.setOnUpdate(() -> {
            Vector2f cursorPos = MouseUtils.getCursorPos();

            float preX = hoverSectorGui.getX();
            float preY = hoverSectorGui.getY();
            hoverSectorGui
                    .handleXConstraint(new PixelConstraint((int) (cursorPos.x * DisplayManager.WIDTH) - 70));
            hoverSectorGui
                    .handleYConstraint(new PixelConstraint((int) (cursorPos.y * DisplayManager.HEIGHT) - 70));
            float postX = hoverSectorGui.getX();
            float postY = hoverSectorGui.getY();

            Sector<?> sector = getSectorAtMouseCoordinates();

            boolean sectorsEqual = sector.equals(lastHoveredSector);
            if (currentGuiHoverText != null && sectorsEqual) {
                currentGuiHoverText.setX(currentGuiHoverText.getX() + (postX - preX));
                currentGuiHoverText.setY(currentGuiHoverText.getY() + (postY - preY));
            } else {
                if (!sectorsEqual)
                    hoverSectorGui.removeComponent(currentGuiHoverText);

                hoverSectorGui.addComponent(currentGuiHoverText = hoverSectorGui
                        .setupText(new Text(sector.getDescription(), .6f, DEFAULT_FONT, Color.LIGHT_GRAY)));
                currentGuiHoverText.setDisplayed(true);
            }

            lastHoveredSector = sector;
        });
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
    }

    public void setOuterCircle(@NotNull GuiEllipse outerCircle) {
        this.outerCircle = outerCircle;
        this.outerCircle.setOutlineWidth(1);
    }

    public void setupListeners() {
        setOnHover(() -> {
            if (MouseUtils.isCursorInGuiComponent(this.outerCircle) &&
                    !MouseUtils.isCursorInGuiComponent(this.innerCircle)) {
                Sector<?> lastSector = lastHoveredSector;
                hoverSectorGui.update();

                if (!lastHoveredSector.equals(lastSector))
                    Gui.showGui(hoverSectorGui);
            } else {
                onLeaveDonut();
            }
        });

        setOnLeave(this::onLeaveDonut);
    }

    private void onLeaveDonut() {
        hoverSectorGui.removeComponent(currentGuiHoverText);
        lastHoveredSector = null;
        currentGuiHoverText = null;
        Gui.hideGui(hoverSectorGui);
    }

    /**
     * Get points on the outer circle where lines are drawn to separate Sectors
     */
    public List<Vector2f> getRenderPoints() {
        if (this.renderPoints.isEmpty()) {
            if (this.sectors.isEmpty() || this.sectors.size() == 1)
                return this.renderPoints;

            float height = this.innerCircle.getFinalHeight() / this.outerCircle.getFinalHeight();
            Vector2f baseLine = new Vector2f(0, height);
            this.renderPoints.add(baseLine);
            Vector2f prevLine = baseLine;
            double total = total();

            int lastSector = this.sectors.size();
            int i = 1;
            for (Sector<?> sector : this.sectors) {
                if (i++ == lastSector)
                    break;

                double alpha = 2 * PI * sector.getValue() / total;
                double x = BigDecimal.valueOf(prevLine.x * Math.cos(alpha) + prevLine.y * Math.sin(alpha))
                        /*.setScale(3, RoundingMode.HALF_UP)*/.doubleValue();
                double y = BigDecimal.valueOf(-prevLine.x * Math.sin(alpha) + prevLine.y * Math.cos(alpha))
                        /*.setScale(3, RoundingMode.HALF_UP)*/.doubleValue();
                this.renderPoints.add(prevLine = new Vector2f(x, y));
            }
            this.renderPoints.add(baseLine);
        }

        return this.renderPoints;
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

    public void reset() {
        this.renderPoints.clear();
        this.sectors.clear();
    }

    public Sector<?> getSectorAtMouseCoordinates() {
//        System.out.println("Cursor at : " + MouseUtils.getCursorPos());
//        System.out.println("Donut at : " + this.getX() + ", " + this.getY());

        Vector2f normalizedVector = MouseUtils.getCursorPos();
        Vector2f.sub(normalizedVector, new Vector2f(getX(), getY()), normalizedVector);
        double width = this.innerCircle.getFinalWidth() / this.outerCircle.getFinalWidth();
        double height = this.innerCircle.getFinalHeight() / this.outerCircle.getFinalHeight();
        normalizedVector.x = (float) Maths.scale(normalizedVector.x, -getWidth(), getWidth(), -width, width);
        normalizedVector.y = (float) Maths.scale(normalizedVector.y, -getHeight(), getHeight(), -height, height);

        Sector<?>[] sectors = this.sectors.toArray(new Sector<?>[0]);
        Sector<?> lastSector = sectors[sectors.length - 1];

        for (int i = 0; i < this.renderPoints.size() - 1; i++) {
            if (i >= sectors.length)
                return lastSector;

            Vector2f normalizedLine1 = this.renderPoints.get(i);
            Vector2f normalizedLine2 = this.renderPoints.get(i + 1);
            float crossAngle1 = cross_product(normalizedLine1, normalizedVector);
            float crossAngle2 = cross_product(normalizedVector, normalizedLine2);
            float crossAreaAngle = cross_product(normalizedLine1, normalizedLine2);

            double angle1 = atan2(abs(crossAngle1), dot(normalizedLine1, normalizedVector));
            double angle2 = atan2(abs(crossAngle2), dot(normalizedVector, normalizedLine2));

            double areaAngle = atan2(abs(crossAreaAngle), dot(normalizedLine1, normalizedLine2));

//             angle1 = Math.acos(dot(normalizedLine1, normalizedVector));
//             angle2 = Math.acos(dot(normalizedVector, normalizedLine2));
//            areaAngle = Math.acos(dot(normalizedLine1, normalizedLine2));
            if (crossAngle1 > 0) {
                angle1 = 2 * PI - angle1;
            }

            if (crossAngle2 > 0) {
                angle2 = 2 * PI - angle2;
            }

            if (crossAreaAngle > 0) {
                areaAngle = 2 * PI - areaAngle;
            }

            if (angle1 <= areaAngle && angle2 <= areaAngle) {
                return sectors[i];
            }
        }

        return lastSector;
    }

    @Override
    public void setDisplayed(boolean displayed) {
        super.setDisplayed(displayed);

        this.innerCircle.setDisplayed(displayed);
        this.outerCircle.setDisplayed(displayed);
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

        @Override
        public String toString() {
            return "Sector{" +
                    "value=" + value +
                    ", color=" + color +
                    ", description='" + description + '\'' +
                    ", shownValue=" + shownValue +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GuiDonutGraph{" +
                "outerCircle=" + outerCircle +
                ", innerCircle=" + innerCircle +
                ", sectors=" + sectors +
                "} ";
    }

    @Override
    public void render() {
        GL30.glBindVertexArray(GuiRenderer.unfilledCircle.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        GuiRenderer.renderDonutGraph(this);
    }
}
