/*
 * Copyright (c) 2011. Nimbits Inc. All Rights reserved.
 *
 * This source code is distributed under GPL v3 without any warranty.
 */

package com.nimbits.cloudplatform.client.model.point;

import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.entity.Entity;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 12:06 PM
 */
public class PointModelFactory {


    private PointModelFactory() {
    }


    public static Point createPointModel(final Point point)  {

        return new PointModel(point);

    }
    public static Point createPointModel(final Entity entity,
                                         final double highAlarm,
                                         final int expire,
                                         final String unit,
                                         final double lowAlarm,
                                         final boolean highAlarmOn,
                                         final boolean lowAlarmOn,
                                         final boolean idleAlarmOn,
                                         final Integer idleSeconds,
                                         final boolean idleAlarmSent,
                                         final FilterType filterType,
                                         final double filterValue,
                                         final boolean inferLocation,
                                         final PointType pointType,
                                         final Integer deltaAlarmSeconds,
                                         final boolean deltaAlarmOn,
                                         final double deltaAlarm
                                        )   {
        return new PointModel(
                entity,  highAlarm,
                expire,  unit,  lowAlarm,
                highAlarmOn,  lowAlarmOn,
                idleAlarmOn,  idleSeconds,  idleAlarmSent,
                filterType,  filterValue, inferLocation, pointType,deltaAlarm, deltaAlarmOn, deltaAlarmSeconds);
    }


}
