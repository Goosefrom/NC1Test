package com.goose.nc1test.controller;

import com.goose.nc1test.config.PeriodOfDay;
import com.goose.nc1test.config.Util;
import com.goose.nc1test.dto.NewsDTO;
import com.goose.nc1test.service.NewsService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** main page controller class**/
@Slf4j
@Component
@RequiredArgsConstructor
public class MainPageController {

    private final NewsService service;

    @FXML
    public TextField searchHeadlineField;
    @FXML
    public TextField searchDescriptionField;
    @FXML
    public ChoiceBox<Integer> searchHourPicker;
    @FXML
    public ChoiceBox<Integer> searchMinutePicker;
    @FXML
    public ChoiceBox<Integer> searchSecondPicker;
    @FXML
    private ChoiceBox<String> searchTimePeriodPicker;

    @FXML
    public ListView<NewsDTO> newsListView;

    @FXML
    public Button previousButton;
    @FXML
    public Label currentPageLabel;
    @FXML
    public Button nextButton;
    @FXML
    public Button refreshButton;
    @FXML
    public ChoiceBox<Integer> elementsNumberPicker;

    @FXML
    public VBox addNewsDialog;
    @FXML
    public TextField addNewsField;

    public Long changeNewsId;
    @FXML
    public VBox changeNewsDialog;
    @FXML
    public TextArea changeHeadlineField;
    @FXML
    public TextArea changeDescriptionField;
    @FXML
    public ChoiceBox<Integer> changeHourPicker;
    @FXML
    public ChoiceBox<Integer> changeMinutePicker;
    @FXML
    public ChoiceBox<Integer> changeSecondPicker;

    @FXML
    public void initialize() {
        List<Integer> integers = new ArrayList<>();
        for(int i = 0; i < 60; i++) {
            integers.add(i);
        }
        List<String> periods = List.of("All", "Morning", "Day", "Evening", "Night");
        searchTimePeriodPicker.setItems(FXCollections.observableArrayList(periods));
        searchHourPicker.setItems(FXCollections.observableArrayList(integers.subList(0, 24)));
        searchMinutePicker.setItems(FXCollections.observableArrayList(integers));
        searchSecondPicker.setItems(FXCollections.observableArrayList(integers));
        elementsNumberPicker.setItems(FXCollections.observableArrayList(1, 2, 5, 7, 10, 15, 20, 30, 50));
        elementsNumberPicker.setValue(10);
        changeHourPicker.setItems(FXCollections.observableArrayList(integers.subList(0, 24)));
        changeMinutePicker.setItems(FXCollections.observableArrayList(integers));
        changeSecondPicker.setItems(FXCollections.observableArrayList(integers));

        Image refreshIcon = new Image("/icons/refresh.png");
        ImageView refreshView = new ImageView(refreshIcon);
        refreshView.setFitHeight(20);
        refreshView.setPreserveRatio(true);
        refreshButton.setGraphic(refreshView);

        fillNews();
    }


    @FXML
    public void onSearch() {
        fillNews();
    }

    @FXML
    public void onPreviousPage() {
        int pageNumber = Integer.parseInt(currentPageLabel.getText().split(" ")[1]);
        int newPageNumber = pageNumber - 1;
        currentPageLabel.setText("Page: " + newPageNumber);
        fillNews();
        if (newPageNumber == 1) previousButton.setDisable(true);
    }
    @FXML
    public void onNextPage() {
        int pageNumber = Integer.parseInt(currentPageLabel.getText().split(" ")[1]);
        int newPageNumber = pageNumber + 1;
        currentPageLabel.setText("Page: " + newPageNumber);
        fillNews();
        if (newPageNumber > 1) previousButton.setDisable(false);
    }
    @FXML
    public void onRefresh() {
        fillNews();
    }
    /** action for change number of list elements**/
    @FXML
    public void onElementsNumberSelected() {
        fillNews();
    }

    /** action for open add dialog**/
    @FXML
    public void onAddNewsClicked() {
        addNewsDialog.setVisible(true);
    }
    /** action for cancel add**/
    @FXML
    public void onAddCancelButtonClicked() {
        addNewsField.setText("");
        addNewsDialog.setVisible(false);
    }
    /** action for commit add**/
    @FXML
    public void onAddCommitButtonClicked() throws IOException {
        LocalTime timeNow = LocalTime.now();
        if (Objects.nonNull(addNewsField.getText())
                && addNewsField.getText().contains(Util.URL + "/")) {
            NewsDTO dto = Util.parseArticle(addNewsField.getText(), timeNow);
            NewsDTO savedNews = service.create(dto);
            log.info("News saved: {}", savedNews);
            fillNews();
            addNewsDialog.setVisible(false);
        }
    }

    /** action for cancel change**/
    @FXML
    public void onChangeCancelButtonClicked() {
        changeNewsId = null;
        changeHeadlineField.setText("");
        changeDescriptionField.setText("");
        changeHourPicker.setValue(null);
        changeMinutePicker.setValue(null);
        changeSecondPicker.setValue(null);
        changeNewsDialog.setVisible(false);
    }
    /** action for commit change**/
    @FXML
    public void onChangeCommitButtonClicked() {
        NewsDTO dto = new NewsDTO();
        if (Objects.nonNull(changeNewsId)) dto.setId(changeNewsId);
        if (Objects.nonNull(changeHeadlineField.getText())
                && !Objects.equals(changeHeadlineField.getText(), "")
                && !Objects.equals(changeHeadlineField.getText(), " "))
            dto.setHeadline(changeHeadlineField.getText());
        if (Objects.nonNull(changeDescriptionField.getText())
                && !Objects.equals(changeDescriptionField.getText(), "")
                && !Objects.equals(changeDescriptionField.getText(), " "))
            dto.setDescription(changeDescriptionField.getText());
        if (Objects.nonNull(changeHourPicker.getValue())
                && Objects.nonNull(changeMinutePicker.getValue())
                && Objects.nonNull(changeSecondPicker.getValue()))
            dto.setTime(LocalTime.of(changeHourPicker.getValue(), changeMinutePicker.getValue(), changeSecondPicker.getValue()));

        if (Objects.nonNull(dto.getId())
                && Objects.nonNull(dto.getHeadline())
                && Objects.nonNull(dto.getDescription())
                && Objects.nonNull(dto.getTime())) {
            NewsDTO updatedDto = service.update(dto);
            log.info("Update complete: {}", updatedDto);
            fillNews();
            changeNewsDialog.setVisible(false);
        }
    }

    /** fill list with elements**/
    public void fillNews() {
        NewsDTO dto = new NewsDTO();
        LocalTime timeFrom = LocalTime.of(0, 0, 0);
        dto.setHeadline(searchHeadlineField.getText());
        dto.setDescription(searchDescriptionField.getText());
        if (Objects.nonNull(searchHourPicker.getValue())) timeFrom = timeFrom.plusHours(searchHourPicker.getValue());
        if (Objects.nonNull(searchMinutePicker.getValue())) timeFrom = timeFrom.plusMinutes(searchMinutePicker.getValue());
        if (Objects.nonNull(searchSecondPicker.getValue())) timeFrom = timeFrom.plusSeconds(searchSecondPicker.getValue());
        dto.setTime(timeFrom);
        if (Objects.nonNull(searchTimePeriodPicker.getValue()))
            dto.setPeriod(Arrays.stream(PeriodOfDay.values()).filter(v -> searchTimePeriodPicker.getValue().toUpperCase().equals(v.name())).findFirst().orElse(null));
        int pageNumber = Integer.parseInt(currentPageLabel.getText().split(" ")[1]);
        Page<NewsDTO> news = service.searchAll(dto, PageRequest.of(
                pageNumber - 1,
                Objects.nonNull(elementsNumberPicker.getValue()) ? elementsNumberPicker.getValue() : 10));

        previousButton.setDisable(pageNumber == 1);
        nextButton.setDisable(news.getNumberOfElements() < elementsNumberPicker.getValue());

        newsListView.setItems(FXCollections.observableArrayList(news.toList()));
        newsListView.setCellFactory(newsCellList -> new CellController(this, service));
    }


}
