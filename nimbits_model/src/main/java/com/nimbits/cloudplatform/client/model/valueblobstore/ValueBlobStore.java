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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.model.valueblobstore;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/23/12
 * Time: 10:47 AM
 */
public interface ValueBlobStore extends Serializable, Comparable<ValueBlobStore> {

    String getEntity();

    Date getTimestamp();

  //  String getPath();

    Date getMaxTimestamp();

    void setMaxTimestamp(Date maxTimestamp);

    Date getMinTimestamp();

    void setMinTimestamp(Date minTimestamp);

    String getBlobKey();

    long getLength();

    void validate();

    Boolean getCompressed();
}
