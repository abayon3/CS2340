package frontpage.controller;

import frontpage.FXMain;
import frontpage.bind.errorhandling.BackendRequestException;
import frontpage.bind.report.SourceReportManager;
import frontpage.model.report.SourceReport;
import frontpage.model.report.WaterCondition;
import frontpage.model.report.WaterType;
import frontpage.utils.DialogueUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.log4j.Logger;

import java.util.LinkedList;

/**
 * @author willstuckey
 * <p></p>
 */
@SuppressWarnings({"unused", "FeatureEnvy", "TypeMayBeWeakened",
        "UseOfSystemOutOrSystemErr", "ChainedMethodCall", "LawOfDemeter",
        "AssignmentToNull", "CyclicClassDependency", "OverlyLongMethod"})
public final class CreateSourceReportController implements Updatable {
    private static final String VIEW_URI =
            "/frontpage/view/CreateSourceReportScreen.fxml";

    private static final Logger LOGGER;
    private static Parent root;
    private static CreateSourceReportController loginController;

    static {
        LOGGER = Logger.getLogger(
                CreateSourceReportController.class.getName());
    }

    /**
     * creates an instance of the controller and its accompanying view
     */
    public static void create() {
        try {
            LOGGER.debug("loading view: " + VIEW_URI);
            FXMLLoader loader = new FXMLLoader(
                    FXMain.class.getResource(VIEW_URI));
            loginController = new CreateSourceReportController();
            loader.setController(loginController);
            root = loader.load();
        } catch (Exception e) {
            LOGGER.error("failed to load view", e);
        }
    }

    /**
     * gets the root node of the bound view
     * @return root node
     */
    public static Parent getRoot() {
        return root;
    }

    /**
     * gets the controller
     * @return controller
     */
    public static CreateSourceReportController
    getCreateSourceReportController() {
        return loginController;
    }

    private SourceReport activeReport;
    @FXML private TextField reportID;
    @FXML private TextField submitter;
    @FXML private TextField title;
    @FXML private TextArea loc;
    @FXML private ComboBox<WaterType> type;
    @FXML private ComboBox<WaterCondition> condition;
    @FXML private TextField date;
    @FXML private TextArea description;

    /**
     * FXML initialization routine
     */
    @FXML
    public void initialize() {
        type.setItems(FXCollections.observableList(new LinkedList<WaterType>() {
            {
                for (WaterType wt : WaterType.values()) {
                    add(wt);
                }
            }
        }));

        condition.setItems(FXCollections.observableList(
                new LinkedList<WaterCondition>() {
            {
                for (WaterCondition wc : WaterCondition.values()) {
                    add(wc);
                }
            }
        }));

        reportID.setDisable(true);
        submitter.setDisable(true);
        type.setValue(WaterType.UNAVAILABLE);
        condition.setValue(WaterCondition.UNAVAILABLE);
        date.setDisable(true);

        //TODO find solution for encoding new lines
        loc.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                event.consume();
            }
        });

        //TODO find solution for encoding new lines
        description.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                event.consume();
            }
        });
    }

    /**
     * view change update call
     * @return success
     */
    @Override
    public boolean update() {
        SourceReportManager rm = FXMain.getBackend().getSourceReportManager();
        activeReport = null;
        try {
            activeReport = SourceReport.createReport(rm, FXMain.getUser());
            activeReport.populateFromBackend(rm);
        } catch (BackendRequestException e) {
            DialogueUtils.showMessage("could not create report template");
            if (activeReport != null) {
                rm.__deleteSourceReport_fs_na(FXMain.getUser().getEmail(),
                        FXMain.getUser().getTok(),
                        activeReport.getReportid());
            }
            return false;
        } catch (Exception e) {
            DialogueUtils.showMessage("internal error on update view call "
                    + "for create source report controller"
                    + " (type: " + e.getClass()
                    + "message: " + e.getMessage() + ")");
            e.printStackTrace();
            if (activeReport != null) {
                rm.__deleteSourceReport_fs_na(FXMain.getUser().getEmail(),
                        FXMain.getUser().getTok(),
                        activeReport.getReportid());
            }
            return false;
        }

        try {
            System.out.println("-------------------------------------------");
            System.out.println(activeReport);
            reportID.setText(activeReport.getReportid());
            submitter.setText(activeReport.getUsername());
            title.setText(activeReport.getTitle());
            loc.setText(activeReport.getLoc());
            WaterType wt = activeReport.getType();
            if (wt != null) {
                type.setValue(wt);
            }
            WaterCondition wc = activeReport.getCondition();
            if (wc != null) {
                condition.setValue(wc);
            }
            date.setText(activeReport.getReportTime().getMonthValue() + "/"
                    + activeReport.getReportTime().getDayOfMonth() + "/"
                    + activeReport.getReportTime().getYear());
            description.setText(activeReport.getDescription());
        } catch (Exception e) {
            DialogueUtils.showMessage(e.getClass() + ", "
                    + e.getMessage() + ", "
                    + e.getCause());
            e.printStackTrace();
        }
        return true;
    }

    @FXML
    private void handleCancelAction() {
        SourceReportManager rm = FXMain.getBackend().getSourceReportManager();
        try {
            activeReport.deleteFromBackend(rm, FXMain.getUser());
        } catch (Exception e) {
            LOGGER.info("failed to clean up after cancel action");
        }
        FXMain.setView("main");
    }

    @FXML
    private void handleSubmitAction() {
        String titleStr = this.title.getText();
        if (valid(titleStr)) {
            activeReport.setTitle(titleStr);
        } else {
            DialogueUtils.showMessage("Title must be filled.");
            return;
        }

        String locStr = this.loc.getText();
        if (valid(locStr)) {
            activeReport.setLoc(locStr);
        } else {
            DialogueUtils.showMessage("Location must be filled.");
        }

        activeReport.setType(type.getValue());
        activeReport.setCondition(condition.getValue());
        activeReport.setDescription(description.getText());

        SourceReportManager rm = FXMain.getBackend().getSourceReportManager();
        try {
            activeReport.writeToBackend(rm, FXMain.getUser());
        } catch (BackendRequestException e) {
            DialogueUtils.showMessage("failed to update report (problem: "
                    + e.getClass() + ", message: "
                    + e.getMessage() + ")");
        } catch (Exception e) {
            DialogueUtils.showMessage("internal exception in "
                    + "handleSubmitReport action handler (problem: "
                    + e.getClass() + ", message: "
                    + e.getMessage() + ")");
        }
        FXMain.setView("main");
    }

    private static boolean valid(final String dat) {
        return ((dat != null) && (dat.length() > 1));
    }
}
