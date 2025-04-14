package hcmute.edu.vn.techstore.convert;

import hcmute.edu.vn.techstore.dto.ProductDTO;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "brand", ignore = true)
    ProductEntity toEntity(ProductDTO dto);

    ProductDTO toDTO(ProductEntity entity);
}