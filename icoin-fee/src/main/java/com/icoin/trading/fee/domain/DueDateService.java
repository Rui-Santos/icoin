package com.icoin.trading.fee.domain;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/26/14
 * Time: 1:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DueDateService {
    Date computeDueDate(Date occurringTime);
}
