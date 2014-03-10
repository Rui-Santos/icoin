package com.icoin.trading.tradeengine.query.activity;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/26/14
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
@CompoundIndexes({
        @CompoundIndex(name = "portfolioActivity_user_type", def = "{'username': 1, 'type': 1}", unique = true),
        @CompoundIndex(name = "portfolioActivity_portfolio_type", def = "{'portfolioId': 1, 'type': 1}", unique = true)
})
public class PortfolioActivity extends VersionedEntitySupport<PortfolioActivity, String, Long> {
    @Indexed
    private String userId;
    @Indexed
    private String username;
    @Indexed
    private String portfolioId;

    private PortfolioActivityType type;

    private Activity activity;

    public PortfolioActivity(String userId,
                             String username,
                             String portfolioId,
                             PortfolioActivityType type,
                             Activity activity) {
        notNull(userId);
        notNull(portfolioId);
        notNull(type);
        notNull(activity);

        this.userId = userId;
        this.username = username;
        this.portfolioId = portfolioId;
        this.type = type;
        this.activity = activity;
    }

    public String getUserId() {
        return userId;
    }


    public String getUsername() {
        return username;
    }


    public String getPortfolioId() {
        return portfolioId;
    }

    public PortfolioActivityType getType() {
        return type;
    }

    public void addActivityItem(Date currentTime, ActivityItem item) {
        activity.addItems(currentTime, item);
    }
}