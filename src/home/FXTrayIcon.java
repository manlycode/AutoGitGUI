package home;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing. * ;
import java.awt. * ;
import java.io.IOException;
import java.net.URL;
import java.util.StringJoiner;

public class FXTrayIcon {

    private final SystemTray tray = SystemTray.getSystemTray();
    private final Stage parentStage;
    private String appTitle;
    private final TrayIcon trayIcon;
    private final PopupMenu popupMenu = new PopupMenu();
    /**
     * Assume this as {@code true} by default. Otherwise
     * a user would have to implement this MenuItem themselves
     * and thus we would need to expose AWT objects.
     */
    private boolean addExitMenuItem = true;

    public FXTrayIcon(Stage parentStage, URL iconImagePath) {
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException("SystemTray icons are not " + "supported by the current desktop environment.");
        }

        // Keeps the JVM running even if there are no
        // visible JavaFX Stages
        Platform.setImplicitExit(false);

        // Set the SystemLookAndFeel, if not available, use default
        // User could change this by calling UIManager.setLookAndFeel themselves
        // after instantiating the FXTrayIcon
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {}

        try {
            final Image iconImage = ImageIO.read(iconImagePath)
                    // Some OSes do not behave well if the icon is larger than 16x16
                    // Image.SCALE_SMOOTH will provide the best quality icon in most instances
                    .getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            this.parentStage = parentStage;
            this.trayIcon = new TrayIcon(iconImage, parentStage.getTitle(), popupMenu);
        } catch(IOException e) {
            throw new RuntimeException("Unable to read the Image at the provided path.");
        }
    }

    /**
     * Adds the FXTrayIcon to the system tray.
     * This will add the TrayIcon with the image initialized in the
     * {@code FXTrayIcon}'s constructor. By default, an empty popup
     * menu it shown
     */
    public void show() {
        SwingUtilities.invokeLater(() ->{
            try {
                tray.add(this.trayIcon);

                // Add a MenuItem with the main Stage's title, this will show the
                // main JavaFX stage when clicked.
                String miTitle;
                if (this.appTitle != null) {
                    miTitle = appTitle;
                } else {
                    miTitle = parentStage.getTitle().isEmpty() ? "Show application": parentStage.getTitle();
                }
                MenuItem miStage = new MenuItem(miTitle);
                miStage.setFont(Font.decode(null).deriveFont(Font.BOLD));
                miStage.addActionListener(e ->Platform.runLater(parentStage::show));
                this.popupMenu.add(miStage);

                // If Platform.setImplicitExit(false) then the JVM will continue to run after
                // no more Stages remain, thus we provide a way to terminate it by default.
                // User will be able to override this by calling new FXTrayIcon(...).addExitItem(false)
                if (addExitMenuItem) {
                    MenuItem miExit = new MenuItem("Exit program");
                    miExit.addActionListener(e ->{
                        this.tray.remove(this.trayIcon);
                        Platform.exit();
                    });
                    this.popupMenu.add(miExit);
                }

                // Show parent stage when user double-clicks the icon
                this.trayIcon.addActionListener(e ->Platform.runLater(this.parentStage::show));
            } catch(AWTException e) {
                throw new RuntimeException("Unable to add TrayIcon", e);
            }
        });
    }

    /**
     * Adds a MenuItem to the {@code FXTrayIcon} that will close the JavaFX application
     * and terminate the JVM. If this is not set to @{code true}, a developer will have
     * to implement this functionality themselves.
     * @param addExitMenuItem If true, the FXTrayIcon's popup menu will display an option for
     *                        exiting the application entirely.
     */
    public void addExitItem(boolean addExitMenuItem) {
        this.addExitMenuItem = addExitMenuItem;
    }

    /**
     * Removes the MenuItem at the given index
     * @param index Index of the MenuItem to remove
     */
    public void removeMenuItem(int index) {
        EventQueue.invokeLater(() ->this.popupMenu.remove(index));
    }

    /**
     * Removes the specified item from the Menu
     * @param menuItem Item to be removed, this method does
     *                 nothing if the item is not in the
     *                 Menu.
     */
    @Deprecated
    public void removeMenuItem(MenuItem menuItem) {
        EventQueue.invokeLater(() ->this.popupMenu.remove(menuItem));
    }

    /**
     * Removes the specified item from the FXTrayIcon's menu. Does nothing
     * if the item is not in the menu.
     * @param fxMenuItem The JavaFX MenuItem to remove from the menu.
     */
    public void removeMenuItem(javafx.scene.control.MenuItem fxMenuItem) {
        EventQueue.invokeLater(() ->{
            MenuItem toBeRemoved = null;
            for (int i = 0; i < this.popupMenu.getItemCount(); i++) {
                MenuItem awtItem = this.popupMenu.getItem(i);
                if (awtItem.getLabel().equals(fxMenuItem.getText()) || awtItem.getName().equals(fxMenuItem.getText())) {
                    toBeRemoved = awtItem;
                }
            }
            if (toBeRemoved != null) {
                this.popupMenu.remove(toBeRemoved);
            }
        });
    }

    /**
     * Adds a separator line to the Menu at the current position.
     */
    public void addSeparator() {
        EventQueue.invokeLater(this.popupMenu::addSeparator);
    }

    /**
     * Adds a separator line to the Menu at the given position.
     * @param index The position at which to add the separator
     */
    public void insertSeparator(int index) {
        EventQueue.invokeLater(() ->this.popupMenu.insertSeparator(index));
    }

    /**
     * Adds the specified MenuItem to the FXTrayIcon's menu
     * @param item Item to be added
     */
    @Deprecated
    public void addMenuItem(MenuItem item) {
        EventQueue.invokeLater(() ->this.popupMenu.add(item));
    }

    /**
     * Adds the specified MenuItem to the FXTrayIcon's menu
     * @param menuItem MenuItem to be added
     */
    public void addMenuItem(javafx.scene.control.MenuItem menuItem) {
        EventQueue.invokeLater(() ->this.popupMenu.add(convertFromJavaFX(menuItem)));
    }

    /**
     * Returns the MenuItem at the given index. The MenuItem
     * returned is the AWT MenuItem, and not the JavaFX MenuItem,
     * thus this should only be called when extending the functionality
     * of the AWT MenuItem.
     * <p>
     *     NOTE: This should be called via the
     *     {@code EventQueue.invokeLater()} method as well as any
     *     subsequent operations on the MenuItem that is returned.
     * @param index Index of the MenuItem to be returned.
     * @return The MenuItem at {@code index}
     */
    public MenuItem getMenuItem(int index) {
        return this.popupMenu.getItem(index);
    }

    /**
     * Sets the FXTrayIcon's tooltip that is displayed on mouse hover.
     * @param tooltip The text of the tooltip
     */
    public void setTrayIconTooltip(String tooltip) {
        EventQueue.invokeLater(() ->this.trayIcon.setToolTip(tooltip));
    }

    /**
     * Sets the application's title. This is used in the FXTrayIcon where appropriate.
     */
    public void setApplicationTitle(String title) {
        this.appTitle = title;
    }

    /**
     * Removes the {@code FXTrayIcon} from the system tray.
     * Also calls {@code Platform.setImplicitExit(true)}, thereby
     * allowing the JVM to terminate after the last JavaFX {@code Stage}
     * is hidden.
     */
    public void hide() {
        EventQueue.invokeLater(() ->{
            tray.remove(trayIcon);
            Platform.setImplicitExit(true);
        });
    }

    /**
     * Converts a JavaFX MenuItem to a AWT MenuItem
     * @param fxItem The JavaFX MenuItem
     * @return The converted AWT MenuItem
     */
    private MenuItem convertFromJavaFX(javafx.scene.control.MenuItem fxItem) {
        MenuItem awtItem = new MenuItem(fxItem.getText());

        StringJoiner sj = new StringJoiner(",");
        if (fxItem.getGraphic() != null) {
            sj.add("setGraphic()");
        }
        if (fxItem.getAccelerator() != null) {
            sj.add("setAccelerator()");
        }
        if (fxItem.getCssMetaData().size() > 0) {
            sj.add("getCssMetaData().add()");
        }
        if (fxItem.getOnMenuValidation() != null) {
            sj.add("setOnMenuValidation()");
        }
        if (fxItem.getStyle() != null) {
            sj.add("setStyle()");
        }
        String errors = sj.toString();
        if (!errors.isEmpty()) {
            throw new RuntimeException(String.format("The following methods were called on the " + "passed JavaFX MenuItem (%s), these methods are not" + "supported by FXTrayIcon.", errors));
        }

        // Set the onAction event to be performed via ActionListener action
        if (fxItem.getOnAction() != null) {
            awtItem.addActionListener(e ->Platform.runLater(() ->fxItem.getOnAction().handle(new ActionEvent())));
        }
        // Disable the MenuItem if the FX item is disabled
        awtItem.setEnabled(!fxItem.isDisable());

        return awtItem;
    }

}