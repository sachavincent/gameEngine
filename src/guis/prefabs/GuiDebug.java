package guis.prefabs;

import fontMeshCreator.Text;
import fontMeshCreator.TextMeshCreator;
import guis.Gui;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.*;
import guis.constraints.GuiConstraintsManager.Builder;
import guis.constraints.layout.PatternLayout;
import guis.constraints.layout.RatioedPatternLayout;
import guis.presets.Background;
import guis.presets.GuiTextInput;
import guis.presets.checkbox.GuiRectangleCheckbox;
import guis.presets.sliders.GuiTextScrollable;
import guis.presets.sliders.ScrollBar;
import inputs.Key;
import renderEngine.DisplayManager;
import resources.ResourceManager;
import resources.ResourceManager.Resource;
import resources.ResourceManager.Stock;
import scene.Scene;
import scene.components.ResourceProductionComponent;
import util.Utils;
import util.commands.Command;
import util.commands.CommandManager;
import util.math.Maths;

import java.awt.*;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class GuiDebug extends Gui {

    private final static Background<File> CHECK = Utils.importResourceTexture("check");

    private static GuiDebug instance;

    public static OutputStream outputStream, outputErrorStream;
    private final GuiText           infosGui;
    public final  GuiTextScrollable consoleLogs;

    private GuiRectangle topRight;
    private GuiText[]    resourceInfo;

    public static GuiDebug getInstance() {
        return instance == null ? (instance = new GuiDebug()) : instance;
    }

    private GuiDebug() {
        super(new Background<>(Utils.setAlphaColor(Color.BLACK, 150)));

        setConstraints(new Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(1))
                .setHeightConstraint(new RelativeConstraint(1))
                .create());

        ScrollBar scrollBar = new ScrollBar(Side.RIGHT, new RelativeConstraint(0.006f), 5, Color.WHITE);
        this.consoleLogs = new GuiTextScrollable(this, new Text("", .52f, DEFAULT_FONT, new ArrayList<>()),
                new Builder()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(0.96f))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0.005f))
                        .setyConstraint(new SideConstraint(Side.TOP, 0.02f))
                        .create(), scrollBar);
        this.consoleLogs.setScrollingSpeed(2);
        this.consoleLogs.getText().setCenteredHorizontally(false);
        this.consoleLogs.getText().setCenteredVertically(false);

        createInfoArea();
        createResourceArea();

        List<Color> colors = new ArrayList<>() {{
            add(Color.WHITE);
            add(Color.RED);
            add(Color.WHITE);
            add(Color.RED);
            add(Color.WHITE);
        }};

        this.infosGui = new GuiText(topRight, new Text("", .8f, DEFAULT_FONT, colors));
        outputStream = new OutputStream() {
            private StringBuilder string = new StringBuilder();

            @Override
            public void write(int b) {
                this.string.append((char) b);

                if ((b == TextMeshCreator.SPACE_ASCII || b == '\n') && !this.string.toString().isEmpty()) {
                    consoleLogs.getText().addTextString(this.string.toString(), Color.WHITE);
                    flush();
                }
            }

            @Override
            public String toString() {
                return this.string.toString();
            }

            @Override
            public void flush() {
                this.string = new StringBuilder();
            }
        };

        outputErrorStream = new OutputStream() {
            private StringBuilder string = new StringBuilder();

            @Override
            public void write(int b) {
                this.string.append((char) b);

                if ((b == TextMeshCreator.SPACE_ASCII || b == '\n') && !this.string.toString().isEmpty()) {
                    consoleLogs.getText().addTextString(this.string.toString(), Color.RED);
                    flush();
                }
            }

            @Override
            public String toString() {
                return this.string.toString();
            }

            @Override
            public void flush() {
                this.string = new StringBuilder();
            }
        };

        final PrintStream printStream = new PrintStream(outputStream);
        final PrintStream printErrorStream = new PrintStream(outputErrorStream);
        DisplayManager.outStream = System.out;
        DisplayManager.errStream = System.err;
//        System.setOut(printStream);
//        System.setErr(printErrorStream);
//        printStream.flush();
//        printErrorStream.flush();


        setOnClose(() -> {
//            printStream.flush();
//            printErrorStream.flush();
//            System.setOut(DisplayManager.outStream);
//            System.setErr(DisplayManager.errStream);
//            if (Game.getInstance().getGameState() == GameState.PAUSED)
//                Game.getInstance().resume();
        });

        GuiTextInput commandInputGui = new GuiTextInput(this, new Builder()
                .setWidthConstraint(new RelativeConstraint(.9f))
                .setHeightConstraint(new RelativeConstraint(0.04f))
                .setxConstraint(new CenterConstraint())
                .setyConstraint(new SideConstraint(Side.BOTTOM, 0.008f))
                .create());
        commandInputGui.setOutlineColor(Color.WHITE);
        commandInputGui.setCursorColor(Color.WHITE);
        commandInputGui.setTextColor(Color.WHITE);
        commandInputGui.addCancelInput(Key.DEBUG.getKeyInput());
        commandInputGui.setUnfocusOnClick(false);
        commandInputGui.setOnUnfocusCallback(() -> setDisplayed(false));
        commandInputGui.setOnClose(commandInputGui::clearText);
//        commandInputGui.setOnOpen(commandInputGui::clearText);
        commandInputGui.setOnSend(text -> {
            if (CommandManager.isTextCommand(text)) {
                String[] tokens = CommandManager.parseCommand(text);
                Command command = CommandManager.getCommandFromTokens(tokens);
                if (command != null) {
                    int result = command.execute();
                    String message = command.getMessageFromResult(result);
                    if (result == 0)
                        System.out.println(message);
                    else
                        System.err.println(message);
                } else {
                    System.err.println("Command not found");
                }
            }
            commandInputGui.clearText();
        });
        setOnOpen(() -> {
//            DisplayManager.outStream = System.out;
//            DisplayManager.errStream = System.err;
//            System.setOut(printStream);
//            System.setErr(printErrorStream);
//            printStream.flush();
//            printErrorStream.flush();

            commandInputGui.focus();
            commandInputGui.clearText();
//            if (Game.getInstance().getGameState() == GameState.STARTED)
//                Game.getInstance().pause();
        });

        setDisplayed(false);
    }

    private void createInfoArea() {
        this.topRight = new GuiRectangle(this, Background.NO_BACKGROUND, new Builder()
                .setWidthConstraint(new RelativeConstraint(.48f))
                .setHeightConstraint(new RelativeConstraint(0.13f))
                .setxConstraint(new SideConstraint(Side.RIGHT, 0.005f))
                .setyConstraint(new SideConstraint(Side.TOP, 0.02f))
                .create());

        this.topRight.setLayout(new RatioedPatternLayout(2, 1, 0, 0, 35, 100, 65, 100));
        GuiRectangle optionsContainer = new GuiRectangle(this.topRight, Background.NO_BACKGROUND);
//        optionsContainer.setLayout(new PatternGlobalConstraint(1, 1, 0, 0));
        GuiRectangleCheckbox autoScrollCheckbox = new GuiRectangleCheckbox(optionsContainer, CHECK,
                Color.WHITE, new Builder()
                .setWidthConstraint(new RelativeConstraint(0.15f))
                .setHeightConstraint(new AspectConstraint(1))
                .setxConstraint(new RelativeConstraint(-.5f, optionsContainer))
                .setyConstraint(new CenterConstraint())
                .create());
        autoScrollCheckbox.getShape().setFilled(false);

        GuiText autoScrollText = new GuiText(optionsContainer,
                new Text("Auto-Scroll", .7f, DEFAULT_FONT, new ArrayList<>(Collections.singletonList(Color.WHITE))),
                new Builder()
                        .setWidthConstraint(new RelativeConstraint(.35f))
                        .setHeightConstraint(new RelativeConstraint(1, autoScrollCheckbox))
                        .setxConstraint(new StickyConstraint(Side.RIGHT, autoScrollCheckbox))
                        .setyConstraint(new CenterConstraint())
                        .create());
        autoScrollCheckbox.setOnCheckCallback(() -> {
            autoScrollCheckbox.getShape().setFilled(true);
            this.consoleLogs.setAutoScrolling(true);
        });

        autoScrollCheckbox.setOnUncheckCallback(() -> {
            autoScrollCheckbox.getShape().setFilled(false);
            this.consoleLogs.setAutoScrolling(false);
        });
    }

    public void createResourceArea() {
        GuiRectangle resourceArea = new GuiRectangle(this, Background.NO_BACKGROUND,
                new GuiConstraintsManager.Builder()
                        .setWidthConstraint(new RelativeConstraint(.48f))
                        .setHeightConstraint(new RelativeConstraint(0.13f))
                        .setxConstraint(new SideConstraint(Side.RIGHT, 0.005f))
                        .setyConstraint(new StickyConstraint(Side.BOTTOM, 0.02f, this.topRight))
                        .create());
        this.resourceInfo = new GuiText[Resource.values().length];

        int nbResources = ResourceManager.getResources().keySet().size();
        int halfNbResources = (int) Math.ceil(nbResources / 2d);
        resourceArea.setLayout(new PatternLayout(halfNbResources, 2));
        for (Resource resource : Resource.values()) {
            GuiRectangle resourceFields = new GuiRectangle(resourceArea, Background.NO_BACKGROUND);
            GuiRectangle resourceIcon = new GuiRectangle(resourceFields, resource.getBackgroundTexture(),
                    new GuiConstraintsManager.Builder()
                            .setHeightConstraint(new RelativeConstraint(1))
                            .setWidthConstraint(new AspectConstraint(1))
                            .setxConstraint(new SideConstraint(Side.LEFT, 0))
                            .setyConstraint(new CenterConstraint())
                            .create());

            Text infos = new Text("N/A", .7f, DEFAULT_FONT, new ArrayList<>() {{
                add(Color.WHITE);
                add(Color.WHITE);
                add(Color.WHITE);
                add(Color.WHITE);
                add(Color.WHITE);
                add(Color.RED);
                add(Color.WHITE);
                add(Color.WHITE);
            }});

            float remainingWidth = (resourceFields.getWidth() - resourceIcon.getWidth()) / resourceFields.getWidth();
            this.resourceInfo[resource.ordinal()] = new GuiText(resourceFields, infos,
                    new Builder()
                            .setWidthConstraint(new RelativeConstraint(remainingWidth))
                            .setHeightConstraint(new RelativeConstraint(1))
                            .setxConstraint(new SideConstraint(Side.RIGHT, 0))
                            .setyConstraint(new CenterConstraint())
                            .create());
        }
    }

    public void updateDebugGui() {
        if (!isDisplayed())
            return;

        this.infosGui.getText().setTextString(
                "TPS: " + DisplayManager.TPS + "\r\n" + "MSPT: " + DisplayManager.MSPT + "\r\nFPS: " +
                        DisplayManager.CURRENT_FPS);
        List<Color> colors = this.infosGui.getText().getColors();
        colors.set(1, Utils.getColorForTPS(DisplayManager.TPS));
        colors.set(3, Utils.getColorForMSPT(DisplayManager.MSPT));

        for (Resource resource : Resource.values()) {
            Map<Resource, Double> resourceProductionRates = Scene.getInstance()
                    .getGameObjectsForComponent(ResourceProductionComponent.class, false)
                    .stream()
                    .map(gameObject -> gameObject.getComponent(ResourceProductionComponent.class))
                    .map(ResourceProductionComponent::getProductionRates)
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue, Double::sum));
            String infosString;
            if (!ResourceManager.getResources().containsKey(resource)) {
                infosString = "N/A";
            } else {
                Stock stock = ResourceManager.getResources().get(resource);
                double productionRate = 0;
                if (resourceProductionRates.containsKey(resource))
                    productionRate = resourceProductionRates.get(resource);
                double amount = Maths.roundDown(stock.getAmount(), 2);
                infosString = "Quantity: " + amount + " / " +
                        (stock.getMaxAmount() == Integer.MAX_VALUE ? "Inf." : stock.getMaxAmount()) + "\n" +
                        "Rate: " + Maths.roundDown(productionRate, 2) + " / sec";

                this.resourceInfo[resource.ordinal()].getText().getColors()
                        .set(1, Utils.getColorForResource(amount, stock.getMaxAmount()));
                this.resourceInfo[resource.ordinal()].getText().getColors()
                        .set(5, productionRate > 0 ? Color.GREEN : Color.RED);
            }
            this.resourceInfo[resource.ordinal()].getText().setTextString(infosString);
        }
    }
}
