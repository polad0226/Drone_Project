package com.cs420.group7.agribot;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import main.java.surelyhuman.jdrone.control.physical.tello.TelloDrone;

import java.io.IOException;
import java.util.Optional;

public class MainController {
    @FXML
    private TreeView<AbstractItem> itemTreeView;
    @FXML
    private VBox itemCommandsVBox;
    @FXML
    private VBox itemContainerCommandsVBox;
    @FXML
    private Pane farmPane;
    @FXML
    private ToggleGroup droneActionsToggleGroup;
    @FXML
    private VBox pricesVBox;
    @FXML
    private Text purchasePriceValueLabel;
    @FXML
    private Text currentMarketValueLabel;

    // Other class variables
    private static final MainController singleton = new MainController();
    public final int WINDOW_WIDTH = 1000;
    public final int WINDOW_HEIGHT = 800;
    public static final int FARM_WIDTH = 600;
    public static final int FARM_HEIGHT = 800;

    private DroneAction selectedDroneAction;
    private CommandCenter commandCenter;
    private Drone drone;
    private TelloAdapter droneReal;

    /**
     * Retrieves the singleton instance for the UI MainController.
     * @return MainController
     */
    static public MainController getSingleton() {
        return MainController.singleton;
    }

    @FXML
    public void initialize() {
        // Hide the commands VBoxes
        itemCommandsVBox.setVisible(false);
        itemContainerCommandsVBox.setVisible(false);
        // Hide the prices labels
        pricesVBox.setVisible(false);

        // Build the default items and containers for itemTreeView
        // Create Containers
        ItemContainer rootItemContainer = new ItemContainer("Root");
        rootItemContainer.setLocationX(0);
        rootItemContainer.setLocationY(0);
        rootItemContainer.setWidth(FARM_WIDTH);
        rootItemContainer.setHeight(FARM_HEIGHT);
        commandCenter = new CommandCenter("Command Center", rootItemContainer);
        ItemContainer barn = new ItemContainer("Barn", rootItemContainer);
        ItemContainer storageBuilding = new ItemContainer("Storage Building", rootItemContainer);
        ItemContainer cropField = new ItemContainer("Crop Field", rootItemContainer);
        ItemContainer milkStorage = new ItemContainer("Milk Storage", barn);
        // Create Items
        drone = new Drone("Drone", commandCenter);
        Item cow = new Item("Cow", barn);
        Item tractor = new Item("Tractor", storageBuilding);
        Item tiller = new Item("Tiller", storageBuilding);
        Item soyCrop = new Item("Soy Crop", cropField);
        // Command Center
        commandCenter.setLocationX(200);
        commandCenter.setLocationY(10);
        commandCenter.setLength(100);
        commandCenter.setWidth(200);
        commandCenter.setHeight(100);
        rootItemContainer.addItem(commandCenter);
        // Barn
        barn.setLocationX(70);
        barn.setLocationY(160);
        barn.setLength(220);
        barn.setWidth(100);
        barn.setHeight(100);
        rootItemContainer.addItem(barn);
        // Storage Building
        storageBuilding.setLocationX(350);
        storageBuilding.setLocationY(160);
        storageBuilding.setLength(220);
        storageBuilding.setWidth(200);
        storageBuilding.setHeight(100);
        rootItemContainer.addItem(storageBuilding);
        // Crop Field
        cropField.setLocationX(50);
        cropField.setLocationY(450);
        cropField.setLength(300);
        cropField.setWidth(500);
        cropField.setHeight(100);
        rootItemContainer.addItem(cropField);
        // Milk Storage
        milkStorage.setLocationX(80);
        milkStorage.setLocationY(320);
        milkStorage.setLength(50);
        milkStorage.setWidth(80);
        milkStorage.setHeight(100);
        barn.addItem(milkStorage);
        // Drone
        drone.setLocationX(275);
        drone.setLocationY(35);
        drone.setLength(50);
        drone.setWidth(50);
        drone.setHeight(50);
        commandCenter.addItem(drone);
        // Cow
        cow.setLocationX(95);
        cow.setLocationY(200);
        cow.setLength(50);
        cow.setWidth(50);
        cow.setHeight(100);
        barn.addItem(cow);
        // Tractor
        tractor.setLocationX(416);
        tractor.setLocationY(200);
        tractor.setLength(75);
        tractor.setWidth(75);
        tractor.setHeight(100);
        storageBuilding.addItem(tractor);
        // Tiller
        tiller.setLocationX(430);
        tiller.setLocationY(310);
        tiller.setLength(50);
        tiller.setWidth(50);
        tiller.setHeight(100);
        storageBuilding.addItem(tiller);
        // Soy Crop
        soyCrop.setLocationX(50);
        soyCrop.setLocationY(450);
        soyCrop.setLength(300);
        soyCrop.setWidth(100);
        soyCrop.setHeight(100);
        cropField.addItem(soyCrop);

        itemTreeView.setRoot(rootItemContainer.getTreeItem());
        itemTreeView.setShowRoot(true);

        refreshFarmPane();
    }

    @FXML
    protected void handleItemTreeViewClick() {
        AbstractItem selectedItem = getSelectedItem();
        if (selectedItem != null) {
            if (selectedItem instanceof ItemContainer) {
                // Shows the item container commands
                itemCommandsVBox.setVisible(false);
                itemContainerCommandsVBox.setVisible(true);
            } else {
                // Shows the item commands
                itemCommandsVBox.setVisible(true);
                itemContainerCommandsVBox.setVisible(false);
            }
            // Updates all prices labels and shows
            PriceVisitor priceVisitor = new PriceVisitor();
            MarketVisitor marketVisitor = new MarketVisitor();
            selectedItem.accept(priceVisitor);
            selectedItem.accept(marketVisitor);
            purchasePriceValueLabel.setText(String.valueOf(priceVisitor.purchasePrice()));
            currentMarketValueLabel.setText(String.valueOf(marketVisitor.marketValue()));
            pricesVBox.setVisible(true);
        } else {
            // Hides both commands
            itemCommandsVBox.setVisible(false);
            itemContainerCommandsVBox.setVisible(false);
            // Hide all price labels
            pricesVBox.setVisible(false);
        }
    }

    @FXML
    protected void handleRenameClick() {
        AbstractItem selectedItem = getSelectedItem();
        if (selectedItem == null) return;

        TextInputDialog dialog = createSingleInputDialog(
                "Rename Item / Item Container",
                "Enter the new name:",
                selectedItem.getName()
        );

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String name = result.get();

            // Verify if name is not empty
            if (name.isEmpty()) {
                System.out.println("ERROR: Name cannot be empty.");
                return;
            }

            selectedItem.setName(result.get());
            // Refresh to force update the TreeItem name
            itemTreeView.refresh();
        }
    }

    @FXML
    protected void handleChangeLocationClick() {
        AbstractItem selectedItem = getSelectedItem();
        if (selectedItem == null) return;

        TextField locationXTextField = new TextField(String.valueOf(selectedItem.getLocationX()));
        TextField locationYTextField  = new TextField(String.valueOf(selectedItem.getLocationY()));
        TextField locationZTextField  = new TextField(String.valueOf(selectedItem.getLocationZ()));

        TextInputDialog dialog = createTripleInputDialog(
                "Change Location",
                "Enter the new location:",
                "Location X",
                "Location Y",
                "Location Z",
                locationXTextField,
                locationYTextField,
                locationZTextField
        );

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int locationX = Integer.parseInt(locationXTextField.getText());
                int locationY = Integer.parseInt(locationYTextField.getText());
                int locationZ = Integer.parseInt(locationZTextField.getText());

                // Verify if location is inside the farm dimensions
                if (locationX < 0 || locationX > FARM_WIDTH || locationY < 0 || locationY > FARM_HEIGHT) {
                    System.out.println("ERROR: Location X and Y must be inside the farm dimensions.");
                    return;
                }

                // Verify if location is not too high
                if (locationZ < 0 || locationZ > 500) {
                    System.out.println("ERROR: Location Z cannot be larger than 500 (20ft).");
                    return;
                }

                selectedItem.setLocationX(locationX);
                selectedItem.setLocationY(locationY);
                selectedItem.setLocationZ(locationZ);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Location X and Y must be an integer.");
            }
        }
    }

    @FXML
    protected void handleChangePriceClick() {
        AbstractItem selectedItem = getSelectedItem();
        if (selectedItem == null) return;

        TextInputDialog dialog = createSingleInputDialog(
                "Change Price",
                "Enter the new price:",
                String.valueOf(selectedItem.getPrice())
        );

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int price = Integer.parseInt(result.get());

                // Verify if price is valid
                if (price < 0) {
                    System.out.println("ERROR: Price must be greater than or equal to zero.");
                    return;
                }

                selectedItem.setPrice(price);
                // Refresh tree view
                handleItemTreeViewClick();
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Price must be an integer.");
            }
        }
    }

    @FXML
    protected void handleChangeMarketValueClick() {
        AbstractItem selectedItem = getSelectedItem();
        if (!(selectedItem instanceof Item)) return;

        TextInputDialog dialog = createSingleInputDialog(
                "Change Market Value",
                "Enter the new market value:",
                String.valueOf(((Item) selectedItem).getMarketValue())
        );

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int price = Integer.parseInt(result.get());

                // Verify if price is valid
                if (price < 0) {
                    System.out.println("ERROR: Price must be greater than or equal to zero.");
                    return;
                }

                ((Item) selectedItem).setMarketValue(price);
                // Refresh tree view
                handleItemTreeViewClick();
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Price must be an integer.");
            }
        }
    }

    @FXML
    protected void handleChangeDimensionsClick() {
        AbstractItem selectedItem = getSelectedItem();
        if (selectedItem == null) return;

        TextField widthTextField = new TextField(String.valueOf(selectedItem.getWidth()));
        TextField heightTextField  = new TextField(String.valueOf(selectedItem.getHeight()));
        TextField lengthTextField = new TextField(String.valueOf(selectedItem.getLength()));

        TextInputDialog dialog = createTripleInputDialog(
                "Change Dimensions",
                "Enter the new dimensions:",
                "Width",
                "Height",
                "Length",
                widthTextField,
                heightTextField,
                lengthTextField
        );

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int width = Integer.parseInt(widthTextField.getText());
                int height = Integer.parseInt(heightTextField.getText());
                int length = Integer.parseInt(lengthTextField.getText());

                // Verify if the dimension is within the farm dimensions
                if (width < 0 || width > FARM_WIDTH || length < 0 || length > FARM_HEIGHT) {
                    System.out.println("ERROR: Dimensions width and length cannot be larger than the farm.");
                    return;
                }

                // Verify if the length is within an acceptable range
                if (height < 0 || height > 250) {
                    System.out.println("ERROR: Dimension height cannot be larger than 250 (10ft).");
                    return;
                }

                selectedItem.setWidth(width);
                selectedItem.setHeight(height);
                selectedItem.setLength(length);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Dimensions width, height, and length must be an integer.");
            }
        }
    }

    @FXML
    protected void handleAddItemClick() {
        TextInputDialog dialog = createSingleInputDialog(
                "Add Item",
                "Enter the name of the new item:",
                "New Item"
        );

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String name = result.get();

            // Verify if name is not empty.
            if (name.isEmpty()) {
                System.out.println("ERROR: Name cannot be empty.");
                return;
            }

            // Maybe prevent the user form creating a new drone
            AbstractItem selectedItem = getSelectedItem();
            if (selectedItem instanceof ItemContainer) {
                if (name.equals("Drone")) {
                    Drone newDrone = new Drone(name);
                    newDrone.setWidth(50);
                    newDrone.setHeight(50);
                    if (selectedItem instanceof CommandCenter) {
                        // Center drone in the command center
                        int offsetX = (selectedItem.getWidth() / 2) - (newDrone.getWidth() / 2);
                        int offsetY = (selectedItem.getHeight() / 2) - (newDrone.getHeight() / 2);
                        newDrone.setLocationX(selectedItem.getLocationX() + offsetX);
                        newDrone.setLocationY(selectedItem.getLocationY() + offsetY);
                    }
                    ((ItemContainer) selectedItem).addItem(newDrone);
                } else {
                    Item newItem = new Item(name, (ItemContainer) selectedItem);
                    ((ItemContainer) selectedItem).addItem(newItem);
                }
                refreshFarmPane();
            }
        }
    }

    @FXML
    protected void handleAddItemContainerClick() {
        TextInputDialog dialog = createSingleInputDialog(
                "Add Item Container",
                "Enter the name of the new item container:",
                "New Item Container"
        );

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String name = result.get();

            // Verify if name is not empty.
            if (name.isEmpty()) {
                System.out.println("ERROR: Name cannot be empty.");
                return;
            }

            AbstractItem selectedItem = getSelectedItem();
            if (selectedItem instanceof ItemContainer) {
                // Maybe prevent the user from creating a new command center
                if (name.equals("Command Center")){
                    CommandCenter newCommandCenter = new CommandCenter(name);
                    // Preset location and dimensions
                    newCommandCenter.setLength(100);
                    newCommandCenter.setWidth(200);
                    newCommandCenter.setHeight(100);
                    newCommandCenter.setLocationX(200);
                    newCommandCenter.setLocationY(10);
                    ((ItemContainer) selectedItem).addItem(newCommandCenter);
                } else {
                    ItemContainer newItemContainer = new ItemContainer(name, (ItemContainer) selectedItem);
                    ((ItemContainer) selectedItem).addItem(newItemContainer);
                }
                refreshFarmPane();
            }
        }
    }

    @FXML
    protected void handleDeleteClick() {
        AbstractItem selectedItem = getSelectedItem();
        if (selectedItem == null) return;
        AbstractItem selectedItemParent = selectedItem.getParent();
        if (!(selectedItemParent instanceof ItemContainer)) return;

        // Prevent deleting Command Center
        if (selectedItem instanceof CommandCenter) return;
        // Prevent deleting Drone
        if (selectedItem instanceof Drone) return;
        // Delete the item
        ((ItemContainer) selectedItemParent).deleteItem(selectedItem);

        refreshFarmPane();
        // Refresh tree view
        handleItemTreeViewClick();
    }

    @FXML
    protected void handleDroneActionsClick() {
        RadioButton radioButton = (RadioButton) droneActionsToggleGroup.getSelectedToggle();
        switch (radioButton.getId()) {
            case "visitItemRB" -> selectedDroneAction = DroneAction.VisitItem;
            case "scanFarmRB" -> selectedDroneAction = DroneAction.ScanFarm;
            case "goHomeRB" -> selectedDroneAction = DroneAction.GoHome;
            default -> System.out.println("ERROR: Selected an unidentified radio button.");
        }
    }

    @FXML
    protected void handleLaunchDroneClick() throws IOException, InterruptedException {
        if (droneReal == null){
            droneReal = new TelloAdapter(new TelloDrone(), commandCenter);
        }
        if (selectedDroneAction == null) return;
        switch (selectedDroneAction) {
            case VisitItem -> {
                AbstractItem selectedItem = getSelectedItem();
                droneReal.visitItem(selectedItem);
            }
            case ScanFarm -> {
                droneReal.setLocationZ(getBestDroneHeight(getRootItemContainer(), droneReal.getLocationZ()));
                droneReal.scanFarm(commandCenter);
            }
            case GoHome -> {
                System.out.println("Physical drone returns to home after every action. No Action being taken.");
            }
            default -> System.out.println("ERROR: A drone action must be selected.");
        }
    }

    @FXML
    protected void handleLaunchSimulatorClick() {
        if (selectedDroneAction == null) return;
        switch (selectedDroneAction) {
            case VisitItem -> {
                AbstractItem selectedItem = getSelectedItem();
                visitItem(selectedItem);
            }
            case ScanFarm -> {
                scanFarm(commandCenter);
            }
            case GoHome -> {
                visitItem(commandCenter);
            }
            default -> System.out.println("ERROR: A drone action must be selected.");
        }
    }

    private void visitItem(AbstractItem item){
        drone.setHeight(getBestDroneHeight(getRootItemContainer(), drone.getHeight()));
        drone.visitItem(item);
    }

    private void scanFarm(AbstractItem commandCenter){
        drone.setHeight(getBestDroneHeight(getRootItemContainer(), drone.getHeight()));
        drone.scanFarm(commandCenter);
    }

    /**
     * Refreshes the farm pane rendering all items in the farm.
     * This method should only be called when a new item is added or removed.
     */
    private void refreshFarmPane() {
        ItemContainer rootItemContainer = getRootItemContainer();
        if (rootItemContainer == null) return;
        farmPane.getChildren().clear();
        buildFarmPane(rootItemContainer);
    }

    /**
     * Runs a Depth-First Search on items and item containers to build the farmPane component.
     * This method should only be called through the refreshFarmPane method.
     * @param rootItemContainer The root item container.
     */
    private void buildFarmPane(ItemContainer rootItemContainer) {
        if (rootItemContainer == null) return;
        for (AbstractItem item : rootItemContainer.getItems()) {
            if (item instanceof ItemContainer) {
                buildFarmPane((ItemContainer) item);
            }
            farmPane.getChildren().add(item.getNode());
        }
    }

    /**
     * Retrieves the currently selected item from the itemTreeView.
     * @return AbstractItem
     */
    private AbstractItem getSelectedItem() {
        TreeItem<AbstractItem> selectedTreeViewItem = itemTreeView.getSelectionModel().getSelectedItem();
        if (selectedTreeViewItem != null && selectedTreeViewItem.getValue() != null) {
            return selectedTreeViewItem.getValue();
        }
        return null;
    }

    /**
     * Creates a new text input dialog with the provided data.
     * @param title The title of the window.
     * @param header The header of the dialog.
     * @param text The preset text of the text input.
     * @return TextInputDialog
     */
    private TextInputDialog createSingleInputDialog(String title, String header, String text) {
        TextInputDialog dialog = new TextInputDialog(text);
        dialog.getDialogPane().setMinWidth(300);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        return dialog;
    }

    /**
     * Creates a new double text input dialog with the provided data.
     * The provided text fields will be stacked vertically.
     * @param title The title of the window.
     * @param header The header of the dialog.
     * @param firstLabel The text for the first label.
     * @param secondLabel The text for the second label.
     * @param firstTextField The first text field.
     * @param secondTextField The second text field.
     * @return TextInputDialog
     */
    private TextInputDialog createDoubleInputDialog(
            String title,
            String header,
            String firstLabel,
            String secondLabel,
            TextField firstTextField,
            TextField secondTextField
    ) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.getDialogPane().setMinWidth(300);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(20);

        grid.add(new Label(firstLabel), 0, 0);
        grid.add(new Label(secondLabel), 0, 1);
        grid.add(firstTextField, 1, 0);
        grid.add(secondTextField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        return dialog;
    }

    /**
     * Creates a new triple text input dialog with the provided data.
     * The provided text fields will be stacked vertically.
     * @param title The title of the window.
     * @param header The header of the dialog.
     * @param firstLabel The text for the first label.
     * @param secondLabel The text for the second label.
     * @param thirdLabel The text for the third label.
     * @param firstTextField The first text field.
     * @param secondTextField The second text field.
     * @param thirdTextField The third text field.
     * @return TextInputDialog
     */
    private TextInputDialog createTripleInputDialog(
            String title,
            String header,
            String firstLabel,
            String secondLabel,
            String thirdLabel,
            TextField firstTextField,
            TextField secondTextField,
            TextField thirdTextField
    ) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.getDialogPane().setMinWidth(300);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(20);

        grid.add(new Label(firstLabel), 0, 0);
        grid.add(new Label(secondLabel), 0, 1);
        grid.add(new Label(thirdLabel), 0, 2);
        grid.add(firstTextField, 1, 0);
        grid.add(secondTextField, 1, 1);
        grid.add(thirdTextField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        return dialog;
    }

    /**
     * Calculates the best drone height.
     * Runs a depth-first search finding the tallest item in the farm.
     * @param rootItemContainer The root item container.
     * @param currentHeight The current max drone height.
     * @return Int
     */
    private int getBestDroneHeight(ItemContainer rootItemContainer, int currentHeight) {
        if (rootItemContainer == null) return currentHeight;
        for (AbstractItem item : rootItemContainer.getItems()) {
            if (item instanceof ItemContainer) {
                currentHeight = getBestDroneHeight((ItemContainer) item, currentHeight);
            }
            currentHeight = Math.max(currentHeight, item.getHeight());
        }
        return currentHeight;
    }

    /**
     * Fetches the root item container of the item tree view.
     * @return ItemContainer
     */
    private ItemContainer getRootItemContainer() {
        TreeItem<AbstractItem> rootTreeItem = itemTreeView.getRoot();
        AbstractItem rootItemContainer = rootTreeItem.getValue();
        if (!(rootItemContainer instanceof ItemContainer)) return null;
        return (ItemContainer) rootItemContainer;
    }
}
