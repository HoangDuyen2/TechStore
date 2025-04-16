package hcmute.edu.vn.techstore.convert;

import hcmute.edu.vn.techstore.dto.ProductDTO;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "brand", ignore = true)
    @Mapping(source = "actived", target = "actived")
    ProductEntity toEntity(ProductDTO dto);

    @Mapping(source = "actived", target = "actived")
    ProductDTO toDTO(ProductEntity entity);
}