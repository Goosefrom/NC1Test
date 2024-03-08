package com.goose.nc1test.controller;

import com.goose.nc1test.dto.NewsDTO;
import com.goose.nc1test.service.NewsService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/** list item controller class**/
@Slf4j
@Component
@RequiredArgsConstructor
public class CellController extends ListCell<NewsDTO> {

    private final MainPageController mainPage;
    private final NewsService service;

    @FXML
    public GridPane gridPane;
    @FXML
    public Label headlineLabel;
    @FXML
    public Label descriptionText;
    @FXML
    public Label publicationTimeLabel;
    @FXML
    public Button changeIcon;
    @FXML
    public Button deleteIcon;


    private FXMLLoader loader;
    private NewsDTO dto;
    
    @Override
    protected void updateItem(NewsDTO dto, boolean empty) {
        super.updateItem(dto, empty);
        
        if (empty || dto == null) {
            setText(null);
            setGraphic(null);
        }
        else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/cell.fxml"));
                loader.setController(this);
                
                try {
                    loader.load();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            this.dto = dto;
            headlineLabel.setText(dto.getHeadline());
            descriptionText.setText(dto.getDescription());
            publicationTimeLabel.setText(dto.getTime().toString());

            Image changeImg = new Image("/icons/change.png");
            Image deleteImg = new Image("/icons/remove.png");
            ImageView changeView = new ImageView(changeImg);
            ImageView deleteView = new ImageView(deleteImg);
            changeView.setFitHeight(20);
            changeView.setPreserveRatio(true);
            deleteView.setFitHeight(20);
            deleteView.setPreserveRatio(true);
            changeIcon.setGraphic(changeView);
            deleteIcon.setGraphic(deleteView);

            gridPane.setOnMouseClicked(actionEvent -> {
                descriptionText.setMaxHeight(descriptionText.getMaxHeight() == 20 ? Double.MAX_VALUE : 20);
            });
            changeIcon.setOnAction(actionEvent -> {
                mainPage.changeNewsId = dto.getId();
                mainPage.changeHeadlineField.setText(dto.getHeadline());
                mainPage.changeDescriptionField.setText(dto.getDescription());
                if (Objects.nonNull(dto.getTime())) {
                    mainPage.changeHourPicker.setValue(dto.getTime().getHour());
                    mainPage.changeMinutePicker.setValue(dto.getTime().getMinute());
                    mainPage.changeSecondPicker.setValue(dto.getTime().getSecond());
                }
                mainPage.changeNewsDialog.setVisible(true);
            });
            deleteIcon.setOnAction(actionEvent -> {
                service.delete(dto.getId());
                log.info("Delete success");
                mainPage.fillNews();
            });

            setText(null);
            setGraphic(gridPane);
        }
    }
}
