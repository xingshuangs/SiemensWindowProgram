package com.znh.siemens.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import lombok.Data;

/**
 * @Author JayNH
 * @Version 1.0
 * @Description TODO
 * @Date 2023/5/5 10:35
 */
@Data
public abstract class SiemensWindowsView implements Initializable {

    @FXML
    protected TextField ipAddress;
    @FXML
    protected ComboBox<String> modelComboBox;
    @FXML
    protected Button connectButton;
    @FXML
    protected Button disconnectButton;
    @FXML
    protected Label tipsLabel;
    @FXML
    protected TextField stringLength;
    @FXML
    protected ImageView statusImg;
    @FXML
    protected TextField readOffset;
    @FXML
    protected TextArea readResult;
    @FXML
    protected TextField readTime;
    @FXML
    protected TextField dataType;
    @FXML
    protected Button readButton;
    @FXML
    protected Button stopButton;
    @FXML
    protected Button startCurveShader;
    @FXML
    protected Button stopCurveShader;
    @FXML
    protected ChoiceBox<String> timeChoiceBox;
    @FXML
    protected LineChart<CategoryAxis, NumberAxis> dataLineChart;
    @FXML
    protected TextField batchReadOffset;
    @FXML
    protected TextField batchReadLength;
    @FXML
    protected TextArea batchReadResult;
    @FXML
    protected Button batchReadButton;
    @FXML
    protected TextField writeReadOffset;
    @FXML
    protected TextField writeDataValue;

}
