package frontpage.controller;

import frontpage.FXMain;
import frontpage.bind.errorhandling.BackendRequestException;
import frontpage.bind.report.SourceReportManager;
import frontpage.model.report.SourceReport;
import frontpage.utils.DialogueUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.log4j.Logger;

import java.util.Map;


/**
 * @author Devan
 * <p>Controller for View Source Report View</p>
 */
@SuppressWarnings({"unused", "FeatureEnvy", "ChainedMethodCall",
        "LawOfDemeter", "CyclicClassDependency"})
public final class ViewSourceReportScreenController implements Updatable {
    private static final String VIEW_URI =
            "/frontpage/view/ViewWaterSourceReports.fxml";

    private static final Logger LOGGER;
    private static Parent root;
    private static ViewSourceReportScreenController viewReportsController;

    static {
        LOGGER = Logger.getLogger(
                ViewSourceReportScreenController.class.getName());
    }

    /**
     * creates an instance of the controller and accompanying view
     */
    public static void create() {
        try {
            LOGGER.debug("loading view: " + VIEW_URI);
            FXMLLoader loader = new FXMLLoader(
                    FXMain.class.getResource(VIEW_URI));
            viewReportsController = new ViewSourceReportScreenController();
            loader.setController(viewReportsController);
            root = loader.load();
        } catch (Exception e) {
            LOGGER.error("failed to load view", e);
        }
    }

    /**
     * gets the root node
     * @return root node
     */
    public static Parent getRoot() {
        return root;
    }

    /**
     * gets the controller
     * @return controller
     */
    public static ViewSourceReportScreenController getViewReportController() {
        return viewReportsController;
    }


    private final ObservableList<SourceReport> reports =
            FXCollections.observableArrayList();
    @FXML private TableView<SourceReport> viewReportsTable;
    @FXML private TableColumn<SourceReport, String> dateTimeCol;
    @FXML private TableColumn<SourceReport, String> reportNumCol;
    @FXML private TableColumn<SourceReport, String> reporterCol;
    @FXML private TableColumn<SourceReport, String> locationCol;
    @FXML private TableColumn<SourceReport, String> waterSourceTypeCol;
    @FXML private TableColumn<SourceReport, String> waterConditionCol;
    @FXML private Button viewReportsReturnBtn;

    private ViewSourceReportScreenController() {

    }

    /**
     * FXML initialization routine
     */
    @FXML
    private void initialize() {
        dateTimeCol.setCellValueFactory(cellData
                -> cellData.getValue().getReportTimeT());
        reportNumCol.setCellValueFactory(cellData
                -> cellData.getValue().getReportIDT());
        reporterCol.setCellValueFactory(cellData
                -> cellData.getValue().getUsernameT());
        locationCol.setCellValueFactory(cellData
                -> cellData.getValue().getLocationT());
        waterSourceTypeCol.setCellValueFactory(cellData
                -> cellData.getValue().getTypeT());
        waterConditionCol.setCellValueFactory(cellData
                -> cellData.getValue().getConditionT());
    }

    /**
     * update call from view switch
     * @return success
     */
    @Override
    public boolean update() {
        SourceReportManager rm = FXMain.getBackend().getSourceReportManager();
        try {
            Map<String, String>[] reportsData = rm.getSourceReports(0);
            for (Map<String, String> reportData : reportsData) {
                if (reportData.containsKey("index")) {
                    reports.add(new SourceReport(reportData));
                }
            }

            viewReportsTable.setItems(reports);
            return true;
        } catch (BackendRequestException e) {
            DialogueUtils.showMessage("view report bre");
        } catch (Exception e) {
            DialogueUtils.showMessage("view report exception (type: "
                    + e.getClass()
                    + ", message: " + e.getMessage()
                    + ", cause: " + e.getCause());
        }

        return false;
    }

    @FXML
    private void handleReturnAction() {
        reports.clear();
        FXMain.setView("main");
    }
}
