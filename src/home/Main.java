package home;

import java.awt. * ;
import java.io.IOException;
import java.util.function.Predicate;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class Main extends Application {

    private double x, y;
    public SimpleObjectProperty<Stage> primaryStage = new SimpleObjectProperty<>(this, "primaryStage");
    private boolean iconAdded;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws AWTException,
            IOException {
        if (SystemTray.isSupported()) {
            installSystemTray();
            Platform.setImplicitExit(false);
            Platform.isFxApplicationThread();
            Platform.isAccessibilityActive();

            primaryStage.setOnCloseRequest(this::promptUserForDesiredAction);
            primaryStage.setOnHidden(this::promptUserForDesiredAction);

            Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.initStyle(StageStyle.UNDECORATED);
            root.setOnMousePressed(event -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });
            root.setOnMouseDragged(event -> {

                primaryStage.setX(event.getScreenX() - x);
                primaryStage.setY(event.getScreenY() - y);

            });
            primaryStage.show();
            this.primaryStage.set(primaryStage);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Your system does not support System Tray icons. AutoGit will exit.");
            alert.showAndWait();
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        if (iconAdded) {
            SystemTray tray = SystemTray.getSystemTray();
            for (TrayIcon icon : tray.getTrayIcons()) {
                tray.remove(icon);
            }
        }
    }


    public void promptUserForDesiredAction(WindowEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner((Window) event.getSource());
        alert.setTitle("Close or Hide AutoGit");
        alert.setHeaderText(null);
        alert.setContentText("Closing AutoGit fully will disable all backup, push, pull, and analytics services. Hiding AutoGit will allow it to be re-opened from the Windows tray menu.");
        ButtonType exit = new ButtonType("Exit");
        ButtonType hide = new ButtonType("Hide");
        alert.getDialogPane().getButtonTypes().setAll(exit, hide);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("images/alert.css").toExternalForm());
        alert.showAndWait().filter(Predicate.isEqual(exit)).ifPresent(unused -> Platform.exit());

    }

    private void installSystemTray() throws AWTException {

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getDefaultToolkit().createImage(getClass().getResource("images/pinnoTransparent.png"));
        TrayIcon icon = new TrayIcon(image, "");
        icon.setImageAutoSize(true);
        //Double click (Win10)
        icon.addActionListener(e -> Platform.runLater(() -> {
            if (primaryStage.get().isShowing()) {
                primaryStage.get().requestFocus();
            } else {
                primaryStage.get().show();
            }
        }));
        SystemTray.getSystemTray().add(icon);
        iconAdded = true;
    }
}
