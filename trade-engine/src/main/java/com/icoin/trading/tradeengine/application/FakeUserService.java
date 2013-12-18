package com.icoin.trading.tradeengine.application;

import com.homhon.base.domain.model.ValueObjectSupport;
import com.homhon.base.domain.model.user.User;
import com.homhon.base.domain.service.UserService;
import com.icoin.trading.users.domain.UserAccount;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-28
 * Time: AM8:10
 * To change this template use File | Settings | File Templates.
 */
public class FakeUserService implements UserService {
    @Override
    public User getCurrentUser() {
        return new FakeUser() {

        };
    }


    private static class FakeUser extends ValueObjectSupport<FakeUser> implements UserAccount<FakeUser> {
        @Override
        public String getId() {
            return "falcon";
        }

        @Override
        public String getName() {
            return "falcon";
        }

        @Override
        public String getPrimaryKey() {
            return getId();
        }

        @Override
        public String getUserName() {
            return getId();
        }

        @Override
        public String getFullName() {
            return getName();
        }
    }
}