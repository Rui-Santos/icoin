/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icoin.trading.webui.util;

import com.icoin.trading.users.domain.model.user.UserAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Jettro Coenradie
 */
public class SecurityUtil {

    public static String obtainLoggedinUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserAccount) {
            return ((UserAccount) principal).getUsername();
        } else {
            throw new IllegalStateException("Wrong security implementation, expecting a UserAccount as principal");
        }
    }

    public static String obtainLoggedinUserIdentifier() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserAccount) {
            return ((UserAccount) principal).getPrimaryKey();
        } else {
            throw new IllegalStateException("Wrong security implementation, expecting a UserAccount as principal");
        }
    }

    public static String obtainLoggedinUserIdentifierSafely() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserAccount) {
            return ((UserAccount) principal).getPrimaryKey();
        }

        return null;
    }
}
