package com.bgasol.common.core.base.info;

import java.util.Date;

/**
 * 位置信息接口
 */
public interface LocationInfo {

    /**
     * @return 经度
     */
    Double getLocationLongitude();

    /**
     * @param longitude 经度
     */
    void setLocationLongitude(Double longitude);

    /**
     * @return 纬度
     */
    Double getLocationLatitude();

    /**
     * @param latitude 纬度
     */
    void setLocationLatitude(Double latitude);

    /**
     * @return 描述
     */
    String getLocationDescription();

    /**
     * @param description 描述
     */
    void setLocationDescription(String description);

    /**
     * @return 海拔
     */
    Double getLocationAltitude();

    /**
     * @param altitude 海拔
     */
    void setLocationAltitude(Double altitude);

    /**
     * @return 误差
     */
    Double getLocationAccuracy();

    /**
     * @param accuracy 误差
     */
    void setLocationAccuracy(Double accuracy);

    /**
     * @return 记录时间
     */
    Date getLocationTimestamp();

    /**
     * @param timestamp 记录时间
     */
    void setLocationTimestamp(Date timestamp);
}
