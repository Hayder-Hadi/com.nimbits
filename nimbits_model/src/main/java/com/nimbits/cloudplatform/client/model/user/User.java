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

package com.nimbits.cloudplatform.client.model.user;


import com.nimbits.cloudplatform.client.model.accesskey.AccessKey;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.entity.Entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 4:10 PM
 */
public interface User extends Entity, Serializable {

    Date getLastLoggedIn();

    void setLastLoggedIn(final Date lastLoggedIn);

    EmailAddress getEmail();


    boolean isRestricted();

    void addAccessKey(AccessKey key);

    List<AccessKey> getAccessKeys();

    boolean isLoggedIn();

    void setLoggedIn(boolean loggedIn);

    String getLoginUrl();

    void setLoginUrl(String loginUrl);

    String getLogoutUrl();

    void setLogoutUrl(String logoutUrl);

    boolean isUserAdmin();

    void setUserAdmin(boolean userAdmin);


}
