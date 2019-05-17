package proz.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import proz.models.CategoryDataModel;
import proz.models.CategoryFxModel;
import proz.models.TestDataModel;
import proz.models.TestFxModel;
import proz.utils.DialogsUtils;
import proz.utils.FxmlUtils;
import proz.utils.exceptions.ApplicationException;

import java.util.Optional;

public class StudentChoiceWindowController
{
    @FXML
    private TableView<CategoryFxModel> categoryTable;
    @FXML
    private TableView<TestFxModel> testNameTable;
    @FXML
    private Button beginTestButton;
    @FXML
    private Pane userChoicePanel;

    private CategoryDataModel categoryDataModel = new CategoryDataModel();
    private TestDataModel testDataModel = new TestDataModel();

    private void disableBeginButtonUntilTestChosen()
    {
        beginTestButton.disableProperty().bind(testNameTable.getSelectionModel().selectedItemProperty().isNull());
    }

    private void showAvailableTestsOnCategoryPicked()
    {
        categoryTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            categoryDataModel.setCategory(newValue);
            if (categoryDataModel.getCategory() != null)
            {
                try {
                    testDataModel.getTestsFromCategory(categoryDataModel.getCategory().getCategoryId());
                } catch (Exception e) {
                    DialogsUtils.errorDialog(e.getMessage());
                }
                testNameTable.setItems(testDataModel.getTests());
            }
        });
    }

    private void storeSelectedTest()
    {
        testNameTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                testDataModel.setTest(newValue));
    }

    @FXML
    private void initialize()
    {
        try {
            categoryDataModel.fetchDataFromDataBase();
        } catch (ApplicationException e) {
            DialogsUtils.errorDialog(e.getMessage());
        }
        categoryTable.setItems(categoryDataModel.getCategories());
        disableBeginButtonUntilTestChosen();
        categoryTable.getSelectionModel().selectFirst();
        showAvailableTestsOnCategoryPicked();
        storeSelectedTest();
    }

    @FXML
    private void logout()
    {
        FxmlUtils.switchScene("/fxmlFiles/StartWindow.fxml", userChoicePanel,
                "/images/testSys.png");
    }

    private void exitOnOkPressed(Optional<ButtonType> result)
    {
        if(result.isPresent() && result.get() == ButtonType.OK)
            Platform.exit();
    }

    @FXML
    private void exit()
    {
        Optional<ButtonType> result = DialogsUtils.exitConfirmationDialog();
        exitOnOkPressed(result);
    }
    @FXML
    private void showStudentGuideDialog()
    {
        DialogsUtils.studentGuideDialog();
    }

    @FXML
    private void highlightOnEnterButtonArea(MouseEvent mouseEvent)
    {
        if(mouseEvent.getEventType().equals(MouseEvent.MOUSE_ENTERED))
        {
            ((Button) mouseEvent.getSource()).setEffect(new DropShadow());
        }
    }

    @FXML
    private void stopHighlightingOnExitButtonArea(MouseEvent mouseEvent)
    {
        if(mouseEvent.getEventType().equals(MouseEvent.MOUSE_EXITED))
        {
            ((Button) mouseEvent.getSource()).setEffect(null);
        }
    }
}
