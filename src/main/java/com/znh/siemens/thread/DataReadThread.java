package com.znh.siemens.thread;

import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;
import com.znh.siemens.utils.FinalConstant;
import javafx.application.Platform;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * @Author JayNH
 * @Description TODO
 * @Date 2023/5/5 11:44
 * @Version 1.0
 */
@Slf4j
public class DataReadThread extends Thread {

    protected S7PLC s7PLC;
    public boolean isRunning = true;
    public boolean isStartCurveShader = false;
    private String dataType;
    private String offset;
    private int stringLength;
    public int shaderCount = 5;
    private String timeType;
    private int readInterval;
    private TextArea readResult;
    private XYChart.Series<CategoryAxis, NumberAxis> series = new XYChart.Series<>();
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    public DataReadThread(){}

    public DataReadThread(S7PLC s7PLC, String dataType, String offset, Integer stringLength, String timeType, int readInterval, TextArea readResult, XYChart.Series<CategoryAxis, NumberAxis> series){
        this.s7PLC = s7PLC;
        this.dataType = dataType;
        this.offset = offset;
        this.stringLength = stringLength;
        this.timeType = timeType;
        this.readInterval = readInterval;
        this.readResult = readResult;
        this.series = series;
    }

    @Override
    public void run() {
        try{
            log.info("----------读取线程开启----------"+Thread.currentThread().getName());
            String resultData = "";
            while(isRunning){
                if(!s7PLC.checkConnected()){
                    isRunning = false;
                }
                switch(dataType){
                    case "Bool":
                        boolean booleanResult = s7PLC.readBoolean(offset);
                        resultData = Boolean.toString(booleanResult);
                        break;
                    case "Byte":
                        byte byteResult = s7PLC.readByte(offset);
                        resultData = Byte.toString(byteResult);
                        break;
                    case "Short":
                        short shortResult = s7PLC.readInt16(offset);
                        resultData = Short.toString(shortResult);
                        break;
                    case "UShort":
                        int uShortResult = s7PLC.readUInt16(offset);
                        resultData = Integer.toString(uShortResult);
                        break;
                    case "Int":
                    case "Long":
                        int intResult = s7PLC.readInt32(offset);
                        resultData = Integer.toString(intResult);
                        break;
                    case "UInt":
                    case "ULong":
                        long uIntResult = s7PLC.readUInt32(offset);
                        resultData = Long.toString(uIntResult);
                        break;
                    case "Float":
                        float floatResult = s7PLC.readFloat32(offset);
                        resultData = Float.toString(floatResult);
                        break;
                    case "Double":
                        double doubleResult = s7PLC.readFloat32(offset);
                        resultData = Double.toString(doubleResult);
                        break;
                    case "String":
                        resultData = s7PLC.readString(offset,stringLength);
                        break;
                    default:
                        break;
                }
                long currentTime = System.currentTimeMillis();
                readResult.setText(simpleDateFormat.format(currentTime)+" : "+resultData);
                // 判断是否开启曲线渲染数据
                if(isStartCurveShader){
                    String finalResultData = resultData;
                    Platform.runLater(() -> {
                        if(series.getData().size()==shaderCount){
                            series.getData().remove(0);
                        }
                        series.getData().add(new XYChart.Data(simpleDateFormat.format(currentTime),Float.parseFloat(finalResultData)));
                    });
                }
                try {
                    if (timeType.equals(FinalConstant.MS)) {
                        TimeUnit.MILLISECONDS.sleep(readInterval);
                    } else {
                        TimeUnit.SECONDS.sleep(readInterval);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("----------读取线程结束----------"+Thread.currentThread().getName());
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
