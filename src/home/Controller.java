package home;

import animatefx.animation.BounceIn;
import animatefx.animation.FadeIn;
import animatefx.animation.Shake;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import java.awt. *;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.util.Timer;
import java.util.TimerTask;

class TrayIconDemo {

    public static void main(Stage finalStage) throws AWTException {
        if (SystemTray.isSupported()) {
            TrayIconDemo td = new TrayIconDemo();
            td.displayTray();
        } else {
            System.err.println("System tray not supported!");
        }

    }

    public void displayTray() throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/pinnoTransparent.png"));
        TrayIcon trayIcon = new TrayIcon(image, "AutoGit");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("AutoGit");
        tray.add(trayIcon);
        trayIcon.displayMessage("AutoGit", "Automatic on-close-save has completed successfully.", MessageType.INFO);

        trayIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    if (Platform.isFxApplicationThread()) {
                        Platform.runLater(new Runnable() {@Override public void run() {
                        }
                        });
                    } else {
                        Application.launch(Main.class,"");
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

    }

}

public class Controller extends Main {

    @FXML
    private VBox vbox;

    @FXML
    private Button btnOverview;

    @FXML
    private Button btnRepos;

    @FXML
    private Button btnAnalytics;

    @FXML
    private Button btnAbout;

    @FXML
    private Button btnHelp;

    @FXML
    private Button btnSettings;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnConfirmExit;

    @FXML
    private Pane pnlMain;

    @FXML
    private Pane pnlRepo;

    @FXML
    private Pane pnlAnalytics;

    @FXML
    private Pane pnlAbout;

    @FXML
    private Pane pnlHelp;

    @FXML
    private Pane pnlSettings;

    @FXML
    private Pane pnlConfirmExit;

    @FXML
    private Pane pnlExiting;

    @FXML
    private Pane AnchorPanel;

    @FXML
    public void handleClicks(ActionEvent actionEvent) throws InterruptedException, MalformedURLException {
        // TODO: 11/2/2020 Fix Panel Transitions by 11/4/2020.
        AnchorPanel.setVisible(true);
        pnlMain.setVisible(true);
        vbox.setVisible(true);

        new FadeIn(AnchorPanel);
        new FadeIn(pnlMain);
        new FadeIn(vbox);

        if (actionEvent.getSource() == btnOverview) {
            pnlMain.setStyle("-fx-background-color : #05071f");
            pnlMain.toFront();
            pnlMain.setVisible(true);
            new BounceIn(pnlMain);
        }
        if (actionEvent.getSource() == btnRepos) {
            pnlRepo.setStyle("-fx-background-color : #05071f");
            pnlRepo.toFront();
            pnlRepo.setVisible(true);
            new BounceIn(pnlRepo);
        }
        if (actionEvent.getSource() == btnAnalytics) {
            pnlAnalytics.setStyle("-fx-background-color : #05071f");
            pnlAnalytics.toFront();
            pnlAnalytics.setVisible(true);
            new BounceIn(pnlAnalytics);
        }
        if (actionEvent.getSource() == btnAbout) {
            pnlAbout.setStyle("-fx-background-color : #05071f");
            pnlAbout.toFront();
            pnlAbout.setVisible(true);
            new BounceIn(pnlAbout);
        }
        if (actionEvent.getSource() == btnHelp) {
            pnlHelp.setStyle("-fx-background-color : #05071f");
            pnlHelp.toFront();
            pnlHelp.setVisible(true);
            new BounceIn(pnlHelp);
        }
        if (actionEvent.getSource() == btnSettings) {
            pnlSettings.setStyle("-fx-background-color : #05071f");
            pnlSettings.toFront();
            pnlSettings.setVisible(true);
            new BounceIn(pnlSettings);
        }
        if (actionEvent.getSource() == btnExit) {
            pnlConfirmExit.setStyle("-fx-background-color: #05071f");
            pnlConfirmExit.toFront();
            pnlConfirmExit.setVisible(true);
            new Shake(pnlConfirmExit);
        }

        if (actionEvent.getSource() == btnConfirmExit) {
            pnlExiting.setStyle("-fx-background-color: #05071f");
            pnlExiting.setVisible(true);
            pnlExiting.toFront();

            TimerTask task = new TimerTask() {@Override
            public void run() {
                try {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Platform.setImplicitExit(false);
                            Stage primaryStage = (Stage) btnConfirmExit.getScene().getWindow();
                            primaryStage.close();
                            //primaryStage.setOnCloseRequest(this::promptUserForDesiredAction);
                            //primaryStage.setOnHidden(this::promptUserForDesiredAction);
                        }
                    });
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            };

            Timer timer = new Timer("Timer");
            long delay = 4000L;
            timer.schedule(task, delay);
        }

    }

}