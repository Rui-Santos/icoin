package com.icoin.trading.users.domain.model.role;

import com.homhon.base.domain.model.ValueObjectSupport;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-6-24
 * Time: PM8:49
 * To change this template use File | Settings | File Templates.
 */
public class Role extends ValueObjectSupport<Role> {
    private String name;
    private String description;

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
