/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.model.value;

import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.model.location.Location;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 2:27 PM
 */
public interface Value extends Serializable, Comparable<Value> {



    String getNote();

    double getDoubleValue();

    String getValueWithNote();

    Date getTimestamp();

    AlertType getAlertState();

    ValueData getData();

    Location getLocation();


}
