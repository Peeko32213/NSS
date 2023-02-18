package com.peeko32213.notsoshrimple.client.render;

import com.peeko32213.notsoshrimple.client.model.SwampBusterModel;
import com.peeko32213.notsoshrimple.common.item.ItemSwampBuster;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class SwampBusterRenderer extends GeoItemRenderer<ItemSwampBuster> {
    public SwampBusterRenderer() {
        super(new SwampBusterModel());
    }
}
