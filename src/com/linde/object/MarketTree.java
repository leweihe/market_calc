package com.linde.object;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cn40580 at 2017-09-30 2:23 PM.
 */
public class MarketTree extends Market {
    private List<MarketTree> childMarket = new ArrayList<>();


    public List<MarketTree> getChildMarket() {
        return childMarket;
    }

    public void setChildMarket(List<MarketTree> childMarket) {
        this.childMarket = childMarket;
    }
}
