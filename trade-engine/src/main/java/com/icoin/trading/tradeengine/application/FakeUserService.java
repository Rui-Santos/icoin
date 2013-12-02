package com.icoin.trading.tradeengine.application;

import com.homhon.base.domain.model.ValueObjectSupport;
import com.homhon.base.domain.model.user.User;
import com.homhon.base.domain.service.UserService;

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

    @Override
    public User getUserById(String id) {
        return new FakeUser();
    }

    private static class FakeUser extends ValueObjectSupport<FakeUser> implements User<FakeUser>{
        @Override
        public String getId() {
            return "falcon";  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getName() {
            return "falcon";  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}