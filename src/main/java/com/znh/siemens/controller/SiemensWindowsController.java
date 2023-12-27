package com.znh.siemens.controller;

import com.github.xingshuangs.iot.protocol.s7.enums.EPlcType;
import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;
import com.znh.siemens.thread.DataReadThread;
import com.znh.siemens.utils.FinalConstant;
import com.znh.siemens.utils.FinalFunction;
import com.znh.siemens.utils.IpAddressUtil;
import com.znh.siemens.view.SiemensWindowsView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import org.apache.commons.lang.StringUtils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * @Author JayNH
 * @Description TODO
 * @Date 2023/5/5 11:08
 * @Version 1.0
 */
public class SiemensWindowsController extends SiemensWindowsView {

    private S7PLC s7PLC;
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    protected DataReadThread dataReadThread;
    protected XYChart.Series<CategoryAxis, NumberAxis> series = new XYChart.Series<>();

    public static FXMLLoader getFxmlLoader() {
        URL url = SiemensWindowsController.class.getResource("/fxml/SiemensWindow.fxml");
        return new FXMLLoader(url);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        modelComboBox.getItems().addAll("200", "200SMART", "300", "400", "1200", "1500");
        modelComboBox.setValue("200SMART");
        timeChoiceBox.getItems().addAll("ms", "s");
        timeChoiceBox.setValue("ms");
        dataLineChart.getData().add(series);
    }

    /**
     * 连接按钮的点击事件
     */
    public void connectButtonActionEvent() {
        try {
            sendMsgToShow("");
            String ipAddressText = ipAddress.getText();
            if (StringUtils.isNotBlank(ipAddressText)) {
                if (IpAddressUtil.isIP(ipAddressText)) {
                    // 获取模型下拉框选中的值
                    String modelText = modelComboBox.getValue();
                    switch (modelText) {
                        case "200":
                            s7PLC = new S7PLC(EPlcType.S200, ipAddressText);
                            break;
                        case "200SMART":
                            s7PLC = new S7PLC(EPlcType.S200_SMART, ipAddressText);
                            break;
                        case "300":
                            s7PLC = new S7PLC(EPlcType.S300, ipAddressText);
                            break;
                        case "400":
                            s7PLC = new S7PLC(EPlcType.S400, ipAddressText);
                            break;
                        case "1200":
                            s7PLC = new S7PLC(EPlcType.S1200, ipAddressText);
                            break;
                        case "1500":
                            s7PLC = new S7PLC(EPlcType.S1500, ipAddressText);
                            break;
                        default:
                            break;
                    }
                    // 设置超时时间
                    s7PLC.setConnectTimeout(3000);
                    s7PLC.setReceiveTimeout(5000);
                    s7PLC.connect();
                    if (s7PLC.checkConnected()) {
                        connectButton.setDisable(true);
                        disconnectButton.setDisable(false);
                        readButton.setDisable(false);
                        batchReadButton.setDisable(false);
                        statusImg.setImage(new Image("/images/greenRound.png"));
                        sendMsgToShow("PLC  " + ipAddressText + " 连接成功！");
                    }else{
                        sendMsgToShow(" PLC连接失败！");
                    }
                } else {
                    sendMsgToShow(" Ip地址格式错误！");
                }
            } else {
                sendMsgToShow(" Ip地址不能为空！");
            }
        } catch (Exception e) {
            sendMsgToShow(" PLC连接失败！");
        }
    }


    /**
     * @author JayNH
     * @description //TODO 断开连接点击事件
     * @date 2023/2/24 15:23
     */
    public void disconnectionButtonActionEvent() {
        try {
            sendMsgToShow("");
            if (s7PLC != null) {
                s7PLC.close();
                connectButton.setDisable(false);
                disconnectButton.setDisable(true);
                readButton.setDisable(true);
                stopButton.setDisable(true);
                batchReadButton.setDisable(true);
                startCurveShader.setDisable(true);
                stopCurveShader.setDisable(true);
                connectButton.requestFocus();
                statusImg.setImage(new Image("/images/redRound.png"));
                sendMsgToShow("");
            }
        } catch (Exception e) {
            tipsLabel.setText(sdf.format(new Date()) + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param actionEvent 事件
     * @author JayNH
     * @description //TODO 根据类型按钮选择对应的数据类型并填充到输入框中
     * @date 2023/2/24 16:09
     */
    public void dataTypeButtonChooseActionEvent(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        String typeName = button.getId();
        dataType.setText(typeName);
    }

    /**
     * @author JayNH
     * @description //TODO 开始读取数据按钮点击事件
     * @date 2023/2/24 16:23
     */
    public void readButtonClickEvent() {
        sendMsgToShow("");
        // 判断地址是否为空
        if (StringUtils.isNotBlank(readOffset.getText())) {
            // 判断数据类型是否有选择
            if (StringUtils.isNotBlank(dataType.getText())) {
                // 判断读取间隔时间
                if (StringUtils.isNotBlank(readTime.getText())) {
                    if (StringUtils.isNumeric(readTime.getText())) {
                        if (s7PLC != null && s7PLC.checkConnected()) {
                            // 判断地址是否为空以及地址填写的是否正确
                            String offsetString = readOffset.getText();
                            int strLength = 0;
                            if (StringUtils.isNotBlank(stringLength.getText())) {
                                strLength = Integer.parseInt(stringLength.getText());
                            }
                            dataReadThread = new DataReadThread(s7PLC, dataType.getText(), offsetString, strLength, timeChoiceBox.getValue(), Integer.parseInt(readTime.getText()), readResult, series);
                            dataReadThread.start();
                            readButton.setDisable(true);
                            stopButton.setDisable(false);
                            startCurveShader.setDisable(false);
                        } else {
                            sendMsgToShow(" PLC未连接！");
                        }
                    } else {
                        sendMsgToShow(" 读取频率格式错误！");
                    }
                } else {
                    sendMsgToShow(" 读取频率不能为空！");
                }
            } else {
                sendMsgToShow(" 数据类型未选择！");
            }
        } else {
            sendMsgToShow(" 地址不能为空！");
        }
    }

    /**
     * @author JayNH
     * @description //TODO 停止读取数据按钮点击事件
     * @date 2023/2/24 16:24
     */
    public void stopButtonClickEvent() {
        if (dataReadThread != null) {
            try {
                // 停止读取数据线程
                dataReadThread.isRunning = false;
                readButton.setDisable(false);
                stopButton.setDisable(true);
                startCurveShader.setDisable(true);
                stopCurveShader.setDisable(true);
            } catch (Exception e) {
                e.printStackTrace();
                readButton.setDisable(true);
                stopButton.setDisable(false);
            }
        }
    }

    /**
     * @author JayNH
     * @description //TODO 开启曲线数据渲染按钮点击事件
     * @date 2023/2/26 21:21
     */
    public void startCurveShaderClickEvent() {
        // 判断数据类型是否支持曲线渲染
        if(!dataType.getText().equalsIgnoreCase(FinalConstant.BOOL) && !dataType.getText().equalsIgnoreCase(FinalConstant.STRING)){
            dataReadThread.isStartCurveShader = true;
            startCurveShader.setDisable(true);
            stopCurveShader.setDisable(false);
        }else{
            sendMsgToShow("该数据类型"+dataType.getText()+"不支持图表渲染！");
        }
    }

    /**
     * @author JayNH
     * @description //TODO 关闭曲线数据渲染按钮点击事件
     * @date 2023/2/26 21:21
     */
    public void stopCurveShaderClickEvent() {
        dataReadThread.isStartCurveShader = false;
        startCurveShader.setDisable(false);
        stopCurveShader.setDisable(true);
    }

    /**
     * @author JayNH
     * @description //TODO 批量读取数据按钮点击事件
     * @date 2023/2/27 15:41
     */
    public void batchReadDataClickEvent() {
        sendMsgToShow("");
        if (StringUtils.isNotBlank(batchReadOffset.getText())) {
            if (StringUtils.isNotBlank(batchReadLength.getText())) {
                if (StringUtils.isNumeric(batchReadLength.getText())) {
                    if (s7PLC != null && s7PLC.checkConnected()) {
                        int length = Integer.parseInt(batchReadLength.getText());
                        byte[] data = s7PLC.readByte(batchReadOffset.getText(), length);
                        String byteString = FinalFunction.toHexString(data);
                        batchReadResult.setText(byteString);
                    } else {
                        sendMsgToShow(" PLC连接失败！");
                    }
                } else {
                    sendMsgToShow(" 批量读取长度格式错误！");
                }
            } else {
                sendMsgToShow(" 批量读取长度不能为空！");
            }
        } else {
            sendMsgToShow(" 批量读取地址不能为空！");
        }
    }

    /**
     * @param actionEvent 事件
     * @author JayNH
     * @description //TODO 数据类型写入PLC
     * @date 2023/2/27 17:04
     */
    public void dataTypeWriteClickEvent(ActionEvent actionEvent) {
        sendMsgToShow("");
        try {
            Button button = (Button) actionEvent.getSource();
            String dataTypeStr = button.getId();
            String dataType = dataTypeStr.split("_")[1];
            String offset = writeReadOffset.getText();
            String value = writeDataValue.getText();
            if (StringUtils.isNotBlank(offset)) {
                if (StringUtils.isNotBlank(value)) {
                    if (s7PLC != null && s7PLC.checkConnected()) {
                        switch (dataType.toLowerCase()) {
                            case "bool":
                                boolean boolVal = "true".equals(value) || "1".equals(value);
                                s7PLC.writeBoolean(offset, boolVal);
                                break;
                            case "byte":
                                byte byteVal = Byte.parseByte(value);
                                s7PLC.writeByte(offset, byteVal);
                                break;
                            case "short":
                                short shortVal = Short.parseShort(value);
                                s7PLC.writeInt16(offset, shortVal);
                                break;
                            case "ushort":
                                int uShortVal = Integer.parseInt(value);
                                s7PLC.writeUInt16(offset, uShortVal);
                                break;
                            case "int":
                            case "long":
                                int intVal = Integer.parseInt(value);
                                s7PLC.writeInt32(offset, intVal);
                                break;
                            case "uint":
                            case "ulong":
                                long uintVal = Long.parseLong(value);
                                s7PLC.writeUInt32(offset, uintVal);
                                break;
                            case "float":
                                float floatVal = Float.parseFloat(value);
                                s7PLC.writeFloat32(offset, floatVal);
                                break;
                            case "double":
                                double doubleVal = Double.parseDouble(value);
                                s7PLC.writeFloat64(offset, doubleVal);
                                break;
                            default:
                                break;
                        }
                    } else {
                        sendMsgToShow(" PLC连接失败！");
                    }
                } else {
                    sendMsgToShow(" 写入数值不能为空！");
                }
            } else {
                sendMsgToShow(" 写入地址不能为空！");
            }
        } catch (Exception e) {
            sendMsgToShow(" 数据写入失败！");
            e.printStackTrace();
        }
    }

    /**
     * 将错误内容进行显示
     *
     * @param content 错误信息
     */
    private void sendMsgToShow(String content) {
        tipsLabel.setText(sdf.format(new Date()) + " --> " + content);
    }
}
