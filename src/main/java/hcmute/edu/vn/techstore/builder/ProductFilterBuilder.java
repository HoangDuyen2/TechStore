package hcmute.edu.vn.techstore.builder;

import lombok.Getter;

import java.util.List;

@Getter
public class ProductFilterBuilder {
    // Getter methods
    private final String name;
    private final List<String> brandNames;
    private final List<String> sim;
    private final List<String> connectivity;
    private final List<String> os;
    private final List<String> processor;
    private final Long minPrice;
    private final Long maxPrice;


    // Constructor private để đảm bảo chỉ sử dụng Builder
    private ProductFilterBuilder(Builder builder) {
        this.name = builder.name;
        this.brandNames = builder.brandNames;
        this.minPrice = builder.minPrice;
        this.maxPrice = builder.maxPrice;
        this.sim = builder.sim;
        this.connectivity = builder.connectivity;
        this.os = builder.os;
        this.processor = builder.processor;
    }

    // Builder class
    public static class Builder {
        private String name;
        private List<String> brandNames;
        private Long minPrice;
        private Long maxPrice;
        private List<String> sim;
        private List<String> connectivity;
        private List<String> os;
        private List<String> processor;


        public Builder setName(String name) {
            this.name = name;
            return this;
        }


        public Builder setBrandNames(List<String> brandNames) {
            this.brandNames = brandNames;
            return this;
        }

        public Builder setMinPrice(Long minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public Builder setMaxPrice(Long maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }
        public Builder setSim(List<String> sim) {
            this.sim = sim;
            return this;
        }
        public Builder setConnectivity(List<String> connectivity) {
            this.connectivity = connectivity;
            return this;
        }
        public Builder setOs(List<String> os) {
            this.os = os;
            return this;
        }
        public Builder setProcessor(List<String> processor) {
            this.processor = processor;
            return this;
        }

        public ProductFilterBuilder build() {
            return new ProductFilterBuilder(this);
        }
    }
}



