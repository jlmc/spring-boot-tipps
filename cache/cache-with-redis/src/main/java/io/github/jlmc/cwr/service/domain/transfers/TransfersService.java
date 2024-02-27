package io.github.jlmc.cwr.service.domain.transfers;

import io.github.jlmc.cwr.service.common.TransferRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.LongStream;

@Service
public class TransfersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransfersService.class);

    private final List<TransferRepresentation> transfers;

    public TransfersService() {
        this.transfers =
                LongStream.range(1L, 96)
                .mapToObj(it -> new TransferRepresentation("" + it, new BigDecimal("" + it), ""))
                .toList();
    }

    @Cacheable(
            cacheNames = "transfers",
            key = "#root.methodName + ':' + #root.args",
            cacheManager = "transfersCacheManager"
    )
    public Page<TransferRepresentation> getPage(int page, int size) {
        LOGGER.info("Executing fetching transfers page {}, size {}", page, size);
        int offset = page * size;

        List<TransferRepresentation> content = transfers.stream()
                .skip(offset)
                .limit(size)
                .toList();

        return new PageImpl<>(content, PageRequest.of(page, size), transfers.size());
    }
}
