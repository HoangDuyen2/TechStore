package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.entity.CartDetailEntity;

import java.util.List;
import java.util.Map;

public interface ICartSplitter {
    Map<String, List<CartDetailEntity>> split (List<CartDetailEntity> detailEntities);
}
