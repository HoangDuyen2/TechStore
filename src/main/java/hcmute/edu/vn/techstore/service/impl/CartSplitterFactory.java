package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.service.interfaces.ICartSplitter;
import hcmute.edu.vn.techstore.service.interfaces.ICartSplitterFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CartSplitterFactory implements ICartSplitterFactory {
    private final Map<String, ICartSplitter> cartSplitters;

    @Override
    public ICartSplitter getSplitter(String type) {
        ICartSplitter splitter = cartSplitters.get(type);
        if (splitter == null) {
            throw new IllegalArgumentException("No splitter for type: " + type);
        }
        return splitter;
    }
}
