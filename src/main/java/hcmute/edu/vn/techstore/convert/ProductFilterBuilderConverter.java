package hcmute.edu.vn.techstore.convert;


import hcmute.edu.vn.techstore.builder.ProductFilterBuilder;
import hcmute.edu.vn.techstore.utils.MapUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ProductFilterBuilderConverter {

    public static ProductFilterBuilder toProductFilterBuilder(Map<String, Object> params) {
        ProductFilterBuilder productFilterBuilder = new ProductFilterBuilder.Builder()
                .setName(MapUtil.getObject(params,"name",String.class))
                .setBrandNames(MapUtil.getObject(params, "brandNames", List.class))
                .setMinPrice(MapUtil.getObject(params, "minPrice", Long.class))
                .setMaxPrice(MapUtil.getObject(params, "maxPrice", Long.class))
                .setSim(MapUtil.getObject(params, "sim", List.class))
                .setConnectivity(MapUtil.getObject(params, "connectivity", List.class))
                .setOs(MapUtil.getObject(params, "os", List.class))
                .setProcessor(MapUtil.getObject(params, "processor", List.class))
                .build();
        return productFilterBuilder;
    }
}
